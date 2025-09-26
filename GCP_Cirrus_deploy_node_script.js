const sql = require('mssql');
const axios = require('axios');
const { SecretManagerServiceClient } = require('@google-cloud/secret-manager');

const TeamsWebhookUrl = 'https://default00a2f2d91d7b4a75adb10c64636b80.6b.environment.api.powerplatform.com:443/powerautomate/automations/direct/workflows/e31987fcfce1413eb198ccb103cf2d6e/triggers/manual/paths/invoke?api-version=1&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=bFMEFFg7ovLbbC5a_2rxa0nLvvThrMZIiJ3rL89ODL8';

const client = new SecretManagerServiceClient();
let initialMessageSent = false;

const checkIntervalSeconds = 15 * 1000;
const majorCodes = ['303', '304', '305'];
const completedMajor = Object.fromEntries(majorCodes.map(code => [code, false]));
const notified = {};

async function getSecret(secretName) {
  const [version] = await client.accessSecretVersion({
    name: `projects/sbx-msh-13e4/secrets/${secretName}/versions/latest`
  });
  return version.payload.data.toString();
}

async function sendTeamsNotification(messageText) {
  if (!TeamsWebhookUrl) return false;

  const payload = {
    type: 'message',
    attachments: [{
      contentType: 'application/vnd.microsoft.card.adaptive',
      content: {
        $schema: 'http://adaptivecards.io/schemas/adaptive-card.json',
        type: 'AdaptiveCard',
        version: '1.2',
        body: [{ type: 'TextBlock', text: messageText, wrap: true }]
      }
    }]
  };

  try {
    await axios.post(TeamsWebhookUrl, payload, {
      headers: { 'Content-Type': 'application/json' },
      timeout: 10000
    });
    console.log('✅ Teams notification sent.');
    return true;
  } catch (error) {
    console.error('❌ Failed to send Teams notification:', error.message);
    return false;
  }
}

async function checkJobs(results) {
  if (!initialMessageSent && results.length > 0) {
    const sent = await sendTeamsNotification('Cutoff triggered. We are monitoring the cutoff process.');
    if (sent) {
      console.log('✅ Start-of-process notification sent.');
      initialMessageSent = true;
    }
  }

  for (const row of results) {
    const id = row.PRCS_ID?.toString();
    const statusId = row.PRCS_STS_ID;
    const statusDesc = row.PRCS_STS_DESC;
    const code = row.PRTFL_CD?.toString();
    const processName = row.PRCS_NM;
    const startTime = row.STRT_DTTM || 'N/A';
    const endTime = row.END_DTTM || 'N/A';
    const execId = row.EXEC_RQST_UNQ_ID;
    const key = `${id}-${statusId}`;

    if (!majorCodes.includes(id)) continue; // 🚫 Skip non-major jobs

    if (statusId === 3) completedMajor[id] = true;

    if (!notified[key] && [3, 4, 5].includes(statusId)) {
      let messageText = `Portfolio: ${code} Cutoff\n\n**Status: ${statusDesc}**\n\n`;

      if (statusId === 4) {
        messageText += `**Failure Details:**\n\nProcess: ${processName}\nStart Time: ${startTime}\nEnd Time: ${endTime}\nExecution ID: ${execId}\nProcess ID: ${id}`;
      } else if (statusId === 5) {
        messageText += `**Timeout Details:**\n\nProcess: ${processName}\nStart Time: ${startTime}\nEnd Time: ${endTime}\nExecution ID: ${execId}\nProcess ID: ${id}`;
      } else if (statusId === 3) {
        messageText += `Start Time: ${startTime}\nEnd Time: ${endTime}`;
      }

      const sent = await sendTeamsNotification(messageText);
      console.log(`${statusDesc} notification ${sent ? '✅ sent' : '❌ FAILED'} for ${processName} (${id})`);
      notified[key] = true;
    }
  }

  // ✅ Exit if all major jobs are completed
  if (!Object.values(completedMajor).includes(false)) {
    const sent = await sendTeamsNotification("Today's Cutoff process completed.");
    console.log(`🎉 Final summary notification ${sent ? 'sent' : 'FAILED'}.`);
    console.log('✅ All major jobs completed. Monitoring finished.');
    process.exit(0);
  }
}

(async function monitor() {
  const query = `
    SELECT * FROM [dbo].[cutoff]
    ORDER BY STRT_DTTM DESC;
  `;

  try {
    const user = await getSecret('sql-username');
    const password = await getSecret('sql-password');

    const config = {
      user,
      password,
      server: '34.171.68.190',
      database: 'CIRRUS',
      options: { trustServerCertificate: true }
    };

    await sql.connect(config);
    console.log('✅ Connected to SQL Server.');

    while (true) {
      try {
        const result = await sql.query(query);
        const rows = result.recordset;
        console.log(`🔍 Retrieved ${rows.length} rows.`);
        await checkJobs(rows);
      } catch (err) {
        console.error('❌ Error during monitoring:', err.message);
        await sendTeamsNotification(`Monitoring Script Error\n\nError: ${err.message}`);
      }

      await new Promise(resolve => setTimeout(resolve, checkIntervalSeconds));
    }
  } catch (err) {
    console.error('❌ Failed to connect to SQL Server:', err.message);
    await sendTeamsNotification(`SQL Connection Error\n\nError: ${err.message}`);
  }
})();
