import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const Dashboard = () => {
    const [userData, setUserData] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem('authToken');
        const role = localStorage.getItem('userRole');

        const fetchUserData = async () => {
            try {
                let endpoint = '';
                if (role === 'ROLE_ADMIN') {
                    endpoint = 'http://localhost:8080/users/get/admin/dashboard'; // Admin-specific endpoint
                } else if (role === 'ROLE_USER') {
                    endpoint = 'http://localhost:8080/users/get/user/profile'; // User-specific endpoint
                }

                const response = await axios.get(endpoint, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setUserData(response.data); // Set the user or admin details
            } catch (err) {
                console.error('Error fetching user data', err);
            }
        };

        fetchUserData();
    }, []);

    return (
        <div>
            {userData ? (
                <div>
                    <h1>{userData.roles[0].name} Dashboard</h1>
                    <h2>Welcome, {userData.name}</h2>
                    <div>
                        <p><strong>Username:</strong> {userData.username}</p>
                        <p><strong>Email:</strong> {userData.email}</p>
                        <p><strong>Phone:</strong> {userData.phone}</p>
                        <p><strong>Name:</strong> {userData.name}</p>

                        {/* If the user is admin, show admin-specific details */}
                        {localStorage.getItem('userRole') === 'ROLE_ADMIN' ? (
                            <div>
                                <h3>Admin-specific data here</h3>
                                {/* You can add admin-related data display */}
                                <Link to="/create-property" className="btn btn-primary">
                                    Create New Property
                                </Link> 
                                <Link to="/view-property" className="btn btn-primary">
                                    View Property You created!
                                </Link> 
                            </div>
                        ) : (
                            <div>
                                <h3>User-specific data here</h3>
                                {/* You can add user-related data display */}
                            </div>
                        )}
                    </div>
                </div>
            ) : (
                <p>Loading...</p>
            )}
        </div>
    );
};

export default Dashboard;
