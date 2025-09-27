const sql = require('mssql');
const axios = require('axios');
require('dotenv').config(); // For loading environment variables

// Teams Webhook Configuration
const TEAMS_WEBHOOK_URL = 'https://default00a2f2d91d7b4a75adb10c64636b80.6b.environment.api.powerplatform.com:443/powerautomate/automations/direct/workflows/e31987fcfce1413eb198ccb103cf2d6e/triggers/manual/paths/invoke?api-version=1&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=bFMEFFg7ovLbbC5a_2rxa0nLvvThrMZIiJ3rL89ODL8';
// Alternative URL commented out
// const TEAMS_WEBHOOK_URL = 'https://default00a2f2d91d7b4a75adb10c64636b80.6b.environment.api.powerplatform.com:443/powerautomate/automations/direct/workflows/88ef2b6713fe46c984a5164f209ba5db/triggers/manual/paths/invoke?api-version=1&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=0N7ut56H1F9CQQMKnWBTOMDQ_mDELszP-Exxq91rlOA';

// SQL Server connection details
const SQL_CONFIG = {
    user: process.env.SQL_USER,
    password: process.env.SQL_PASSWORD,
    server: '10.32.56.5',
    database: 'CIRRUS',
    options: {
        encrypt: false, // Set to true if using Azure or encrypted connections
        trustServerCertificate: true // Bypass cert validation if needed
    }
};

// Function to Send Simple Teams Notifications
async function sendTeamsNotification(messageText) {
    if (!TEAMS_WEBHOOK_URL) {
        console.log('Teams Webhook URL not configured. Skipping notification.');
        return false;
    }

    const payload = {
        type: 'message',
        attachments: [
            {
                contentType: 'application/vnd.microsoft.card.adaptive',
                contentUrl: null,
                content: {
                    '$schema': 'http://adaptivecards.io/schemas/adaptive-card.json',
                    type: 'AdaptiveCard',
                    version: '1.2',
                    body: [
                        {
                            type: 'TextBlock',
                            text: messageText,
                            wrap: true
                        }
                    ]
                }
            }
        ]
    };

    try {
        await axios.post(TEAMS_WEBHOOK_URL, payload, {
            headers: { 'Content-Type': 'application/json' },
            timeout: 10000
        });
        console.log('Teams notification sent.');
        return true;
    } catch (error) {
        console.error(`Failed to send Teams notification: ${error.message}`);
        return false;
    }
}

// SQL Query for Process Log Monitoring
const QUERY = `
SELECT TOP 1000 
    prcs.PRCS_ID, 
    prcs.PRTFL_CD, 
    prcs.PRCS_STS_ID, 
    prcs.STRT_DTTM, 
    prcs.END_DTTM,
    prcs.EXEC_RQST_UNQ_ID,
    sts.PRCS_STS_DESC, 
    enum.PRCS_NM
FROM [RPT].[PRCS_LOG] (NOLOCK) prcs
JOIN [ENUM].[PRCS] enum ON enum.PRCS_ID = prcs.PRCS_ID
JOIN [ENUM].[PRCS_STS] sts ON sts.PRCS_STS_ID = prcs.PRCS_STS_ID
WHERE prcs.BUS_DT = CAST(GETUTCDATE()-2 AS DATE)
ORDER BY prcs.STRT_DTTM DESC;
`;

// Monitoring Configuration
const CHECK_INTERVAL_MS = 30 * 1000; // 30 seconds
const MAJOR_CODES = ['303', '304', '305'];

const completedMajor = {};
const notified = {};
MAJOR_CODES.forEach(code => completedMajor[code] = false);

let initialMessageSent = false;

// Function to Check Job Status and Send Notifications
async function checkJobs(results) {
    // Send initial message only once if results exist
    if (!initialMessageSent && results.length > 0) {
        const messageText = 'Cutoff triggered. We are monitoring the cutoff process.';
        const sent = await sendTeamsNotification(messageText);
        if (sent) {
            console.log('Start-of-process notification sent.');
            initialMessageSent = true;
        } else {
            console.log('Start-of-process notification FAILED.');
        }
    }

    for (const row of results) {
        const id = row.PRCS_ID.toString();
        const code = row.PRTFL_CD.toString();
        const statusId = parseInt(row.PRCS_STS_ID);
        const statusDesc = row.PRCS_STS_DESC;
        const processName = row.PRCS_NM;
        const key = `${id}-${statusId}`;
        const startTime = row.STRT_DTTM.toISOString().slice(0, 19).replace('T', ' ');

        // Handle potential NULL for END_DTTM
        const endTime = row.END_DTTM ? row.END_DTTM.toISOString().slice(0, 19).replace('T', ' ') : 'N/A';

        // Track major completions
        if (statusId === 3 && MAJOR_CODES.includes(id)) {
            completedMajor[id] = true;
        }

        // Only notify once per job+status
        if (!notified[key]) {
            let messageText;
            let sent;
            switch (statusId) {
                case 4: // FAILED
                    const execRqstUnqIdFail = row.EXEC_RQST_UNQ_ID.toString();
                    messageText = `Portfolio: ${code} Cutoff\n\n**Status: ${statusDesc}**\n\n**Failure Details:**\n`;
                    messageText += `\n\nProcess: ${processName}\n`;
                    messageText += `\n\nStart Time: ${startTime}\n`;
                    messageText += `\n\nEnd Time: ${endTime}\n`;
                    messageText += `\n\nExecution ID: ${execRqstUnqIdFail}\n`;
                    messageText += `\n\nProcess ID: ${id}\n`;
                    sent = await sendTeamsNotification(messageText);
                    if (sent) {
                        console.log(`Failure notification sent for ${processName} (${id})`);
                    } else {
                        console.log(`Failure notification FAILED for ${processName} (${id})`);
                    }
                    break;
                case 5: // TIMED-OUT
                    const execRqstUnqIdTimeout = row.EXEC_RQST_UNQ_ID.toString();
                    messageText = `Portfolio: ${code} Cutoff\n\n**Status: ${statusDesc}**\n\n**Timeout Details:**\n`;
                    messageText += `\n\nProcess: ${processName}\n`;
                    messageText += `\n\nStart Time: ${startTime}\n`;
                    messageText += `\n\nEnd Time: ${endTime}\n`;
                    messageText += `\n\nExecution ID: ${execRqstUnqIdTimeout}\n`;
                    messageText += `\n\nProcess ID: ${id}\n`;
                    sent = await sendTeamsNotification(messageText);
                    if (sent) {
                        console.log(`Timeout notification sent for ${processName} (${id})`);
                    } else {
                        console.log(`Timeout notification FAILED for ${processName} (${id})`);
                    }
                    break;
                case 3: // COMPLETED
                    if (MAJOR_CODES.includes(id)) {
                        messageText = `Portfolio: ${code} Cutoff\n\n**Status: ${statusDesc}**\n\nStart Time: ${startTime}\n\nEnd Time: ${endTime}`;
                        sent = await sendTeamsNotification(messageText);
                        if (sent) {
                            console.log(`Completion notification sent for ${processName} (${id})`);
                        } else {
                            console.log(`Completion notification FAILED for ${processName} (${id})`);
                        }
                    }
                    break;
            }
            notified[key] = true;
        }
    }
}

// Main Monitoring Loop
async function startMonitoring() {
    const intervalId = setInterval(async () => {
        try {
            const pool = await sql.connect(SQL_CONFIG);
            const result = await pool.request().query(QUERY);
            const results = result.recordset;

            await checkJobs(results);

            // Exit condition
            if (!Object.values(completedMajor).includes(false)) {
                const finalMessage = "Today's Cutoff process completed.";
                const sent = await sendTeamsNotification(finalMessage);
                if (sent) {
                    console.log('Final summary notification sent.');
                } else {
                    console.log('Final summary notification FAILED.');
                }

                console.log('All major jobs completed successfully. Monitoring finished.');
                clearInterval(intervalId);
                sql.close();
            }
        } catch (error) {
            console.error(`Error during monitoring: ${error.message}`);
            const errorMessage = `Monitoring Script Error\n\nError: ${error.message}`;
            const sent = await sendTeamsNotification(errorMessage);
            if (sent) {
                console.log('Error notification sent.');
            } else {
                console.log('Error notification FAILED.');
            }
        }
    }, CHECK_INTERVAL_MS);
}

startMonitoring().catch(console.error);
