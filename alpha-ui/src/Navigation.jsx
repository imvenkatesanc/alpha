import React from 'react';
import { Link } from 'react-router-dom';
import logo from './assets/pms_logo.png'; // Update this path to your logo
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHome } from '@fortawesome/free-solid-svg-icons';

const Navigation = () => {
    const token = localStorage.getItem('authToken');
    const role = localStorage.getItem('userRole');

    return (
        <nav className="navbar">
            <div className="navbar__logo">
                <img src={logo} alt="Property Management System Logo" className="navbar__logo-image" />
            </div>
            <Link to="/" className="navbar__link">
                <FontAwesomeIcon className='fa-home' icon={faHome} /> Home
            </Link>
            {
                <div className="navbar__auth-links">
                    <Link to="/login" className="btn">Login</Link>
                    <Link to="/register" className="btn">Register</Link>
                </div>
                }
        </nav>
    );
};

export default Navigation;
