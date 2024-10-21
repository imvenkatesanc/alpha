import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate,Link } from 'react-router-dom';
import Navigation from './Navigation';
import logo from './assets/pms_logo.png';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSignInAlt, faUser } from '@fortawesome/free-solid-svg-icons';

const LoginForm = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const response = await axios.post('http://localhost:8080/users/authenticate', {
                username,
                password,
            });

            const receivedToken = response.data.token;
            const userRole = response.data.role; // Retrieve role from the response (ROLE_ADMIN or ROLE_USER)
            const userId = response.data.userId;

            localStorage.setItem('authToken', receivedToken);
            localStorage.setItem('userRole', userRole); // Save role to localStorage
            localStorage.setItem('userId', userId);

            // Redirect to the dashboard
            navigate('/dashboard');
        } catch (err) {
            setError('Login failed. Please check your credentials.');
        }
    };

    return (
        <>
        {/* <Navigation/> */}
        <div className="auth-container">
            <div className="auth-card">
                <div className="auth-header">
                    <img src={logo} alt="Logo" className="auth-logo" />
                    <h2 className='auth-title'>Welcome to PMS</h2>
                    <h3 className="auth-title">
                        <FontAwesomeIcon className='fa-user' icon={faUser} />
                    </h3>
                </div>
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="Username"
                        className="auth-input"
                        aria-label="Username"
                        required
                    />
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Password"
                        className="auth-input"
                        aria-label="Password"
                        required
                    />
                    <button type="submit" className="auth-button">
                        <FontAwesomeIcon className="fa-sign" icon={faSignInAlt} /> Login
                    </button>
                    {error && <p className="error-message">{error}</p>} {/* Display error if exists */}
                </form>
                <p className="auth-link">
                    Don't have an account? <Link to="/register">Register</Link>
                </p>
            </div>
        </div>
        </>
    );
};

export default LoginForm;