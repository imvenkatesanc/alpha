import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import logo from './assets/pms_logo.png';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSignInAlt,faUser } from '@fortawesome/free-solid-svg-icons';


const RegistrationForm = () => {
    const [formData, setFormData] = useState({
        username: '',
        password: '',
        email: '',
        phone: '',
        name: '',
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess(false);

        try {
            const response = await axios.post('http://localhost:8080/users/register', formData);
            if (response.status === 200) {
                setSuccess(true);
                // Redirect to login page after successful registration
                navigate('/login');
            }
        } catch (err) {
            setError('Registration failed. Please try again.');
        }
    };

    return (
        <>
         <div className="auth-container">
            <div className="auth-card">
                <img src={logo} alt="Logo" className="auth-logo" />
                <h2 className='auth-title'>Welcome to PMS</h2>
                <h3 className="auth-title">
                    <FontAwesomeIcon className='fa-user' icon={faUser} />
                </h3>
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        name="username"
                        value={formData.username}
                        onChange={handleChange}
                        placeholder="Username"
                        required
                        className="auth-input"
                    />
                    <input
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        placeholder="Password"
                        required
                        className="auth-input"
                    />
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        placeholder="Email"
                        required
                        className="auth-input"
                    />
                    <input
                        type="text"
                        name="phone"
                        value={formData.phone}
                        onChange={handleChange}
                        placeholder="Phone Number"
                        required
                        className="auth-input"
                    />
                    <input
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        placeholder="Name"
                        required
                        className="auth-input"
                    />
                    {error && <p className="error-message">{error}</p>}
                    {success && <p className="success-message">Registration successful! Redirecting to login...</p>}
                    <button type="submit" className="auth-button">
                        <FontAwesomeIcon className="fa-sign" icon={faSignInAlt} /> Register
                    </button>
                </form>
                <p className="auth-link">
                    Already have an account? <Link to="/login">Login</Link>
                </p>
            </div>
        </div>
        </>
    );
};

export default RegistrationForm;
