import React, { useState, useEffect } from 'react';
import axios from 'axios';

const PropertyView = ({ propertyId }) => {
    const [fileData, setFileData] = useState(null);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const fetchFileData = async () => {
            setLoading(true);
            setError('');
            try {
                const token = localStorage.getItem('authToken'); // If your API requires auth

                // Fetch the property file data
                const response = await axios.get(`http://localhost:8080/api/properties/download/${2}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                setFileData(response.data);
            } catch (err) {
                setError('Failed to fetch property file data.');
                console.error('Error fetching file data', err);
            } finally {
                setLoading(false);
            }
        };

        fetchFileData();
    }, [propertyId]);

    const downloadFile = () => {
        if (fileData && fileData.downloadURL) {
            const link = document.createElement('a');
            link.href = fileData.downloadURL;
            link.setAttribute('download', fileData.fileName); // Set the file name for download
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link); // Clean up after download
        }
    };

    return (
        <div className="download-property">
            <h3>Download Property Attachment</h3>
            {error && <p className="error">{error}</p>}
            {loading ? (
                <p>Loading...</p>
            ) : (
                fileData && (
                    <div>
                        <p><strong>Property Name:</strong> {fileData.propertyName}</p>
                        <p><strong>Description:</strong> {fileData.description}</p>
                        <p><strong>Price:</strong> ${fileData.price}</p>
                        <p><strong>File Name:</strong> {fileData.fileName}</p>
                        <p><strong>File Size:</strong> {fileData.fileSize} bytes</p>
                        <button onClick={downloadFile}>Download File</button>
                    </div>
                )
            )}
        </div>
    );
};

export default PropertyView;
