import React, { useState } from 'react';
import axios from 'axios';

const CreateProperty = () => {
    const [property, setProperty] = useState({
        name: '',
        type: '',
        address: '',
        description: '',
        price: '',
        isAvailable: false,
    });
    const [file, setFile] = useState(null);
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setProperty((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value,
        }));
    };

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccessMessage('');
        setLoading(true);

        const formData = new FormData();
        formData.append('property', JSON.stringify(property));

        if (file) {
            formData.append('file', file);
        }

        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.post('http://localhost:8080/api/properties/create', formData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data',
                },
            });

            setSuccessMessage('Property created successfully!');
            setProperty({
                name: '',
                type: '',
                address: '',
                description: '',
                price: '',
                isAvailable: false,
            });
            setFile(null);
        } catch (err) {
            setError('Failed to create property. Please try again.');
            console.error('Error creating property', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
        <div className="create-property-container">
            <h2>Create Property</h2>
            {error && <p className="error">{error}</p>}
            {successMessage && <p className="success">{successMessage}</p>}
            {loading ? (
                <p>Loading...</p>
            ) : (
                <form onSubmit={handleSubmit}>
                    <div>
                        <label htmlFor="name">Property Name:</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            value={property.name}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div>
                        <label htmlFor="type">Property Type:</label>
                        <input
                            type="text"
                            id="type"
                            name="type"
                            value={property.type}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div>
                        <label htmlFor="address">Address:</label>
                        <input
                            type="text"
                            id="address"
                            name="address"
                            value={property.address}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div>
                        <label htmlFor="description">Description:</label>
                        <textarea
                            id="description"
                            name="description"
                            value={property.description}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div>
                        <label htmlFor="price">Price:</label>
                        <input
                            type="number"
                            id="price"
                            name="price"
                            value={property.price}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div>
                        <label htmlFor="file">Attachment (Image/Document):</label>
                        <input
                            type="file"
                            id="file"
                            name="file"
                            onChange={handleFileChange}
                        />
                    </div>
                    <div>
                        <label htmlFor="isAvailable">Available:</label>
                        <input
                            type="checkbox"
                            id="isAvailable"
                            name="isAvailable"
                            checked={property.isAvailable}
                            onChange={handleChange}
                        />
                    </div>
                    <button type="submit">Create Property</button>
                </form>
            )}
        </div>
        </>
    );
};

export default CreateProperty;
