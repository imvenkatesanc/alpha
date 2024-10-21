import React, { useEffect, useState } from 'react';
import axios from 'axios';

const MyProfile = () => {
    const [userData, setUserData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem('authToken');
        const role = localStorage.getItem('userRole');

        const fetchUserData = async () => {
            try {
                let endpoint = '';
                if (role === 'ROLE_ADMIN') {
                    endpoint = 'http://localhost:8080/users/get/admin/dashboard';
                } else if (role === 'ROLE_USER') {
                    endpoint = 'http://localhost:8080/users/get/user/profile';
                }

                const response = await axios.get(endpoint, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setUserData(response.data);
            } catch (err) {
                setError('Error fetching user data');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        if (token && role) {
            fetchUserData();
        } else {
            setLoading(false); // No token or role, stop loading
        }
    }, []);

    if (loading) return <div>Loading...</div>;
    if (error) return <div>{error}</div>;

    return (
        <div style={styles.dashboardContainer}>
            <div style={styles.dashboardContent}>
                <div style={styles.header}>
                    <h1>{userData && userData.roles && userData.roles.length > 0 ? `${userData.roles[0].name} Dashboard` : "Loading..."}</h1>
                </div>
                {userData ? (
                    <div style={styles.userInfo}>
                        <h2 style={styles.welcomeText}>Welcome, {userData.name}</h2>
                        <div style={styles.detailsContainer}>
                            <p><strong>Username:</strong> {userData.username}</p>
                            <p><strong>Email:</strong> {userData.email}</p>
                            <p><strong>Phone:</strong> {userData.phone}</p>
                            <p><strong>Name:</strong> {userData.name}</p>
                        </div>
                    </div>
                ) : (
                    <p>No user data available.</p>
                )}
            </div>
            <style>{`
                @media (max-width: 700px) {
                    #nav-bar {
                        display: none; /* Hide the sidebar on small screens */
                    }
                    .dashboard-container {
                        padding: 10px; /* Reduce padding for small screens */
                    }
                }
            `}</style>
        </div>
    );
};

const styles = {
    dashboardContainer: {
        display: 'flex',
        height: '100vh',
        backgroundColor: '#f5f6fa',
        fontFamily: 'Arial, sans-serif',
        maxWidth: '1200px',
        flexDirection:'column',
        marginLeft: 'auto',
        borderRadius: '8px',
    },
    dashboardContent: {
        flex: 1,
        padding: '20px',
        overflowY: 'auto',
        backgroundColor: '#fff',
        boxShadow: '0 0 10px rgba(0, 0, 0, 0.1)',
        borderRadius: '8px',
        // marginLeft: '100px',
        transition: 'margin-left 0.3s ease',
    },
    header: {
        backgroundColor: '#e0e0e0',
        textAlign: 'center',
        padding: '20px',
        borderRadius: '8px',
        marginBottom: '20px',
    },
    welcomeText: {
        textAlign: 'center',
        color: '#333',
        margin: '10px 0',
    },
    userInfo: {
        marginBottom: '20px',
        textAlign: 'center',
    },
    detailsContainer: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        marginBottom: '20px',
    },
    link: {
        display: 'inline-block',
        margin: '10px 0',
        padding: '10px 15px',
        backgroundColor: '#007bff',
        color: '#fff',
        textDecoration: 'none',
        borderRadius: '5px',
        transition: 'background-color 0.3s',
        textAlign: 'center',
    },
};

export default MyProfile;
