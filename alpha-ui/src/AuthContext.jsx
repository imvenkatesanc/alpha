import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(() => {
        const token = localStorage.getItem('authToken');
        const role = localStorage.getItem('role');
        const userId = localStorage.getItem('userId');
        return token ? { role, userId, token } : null; // Initialize user if token exists
    });
    const [error, setError] = useState(null); // Error state

    useEffect(() => {
        if (user) {
            axios.defaults.headers.common['Authorization'] = `Bearer ${user.token}`;
        } else {
            delete axios.defaults.headers.common['Authorization'];
        }
    }, [user]);

    const login = async (username, password) => {
        try {
            const response = await axios.post('http://localhost:8080/users/authenticate', { username, password });
            const { token, role, userId } = response.data; // Destructure the response
            const newUser = { role, userId, token };
            setUser(newUser);
            localStorage.setItem('authToken', token);
            localStorage.setItem('role', role);
            localStorage.setItem('userId', userId);
            setError(null); // Clear any previous errors
        } catch (err) {
            // Handle 401 Unauthorized error specifically
            if (err.response && err.response.status === 401) {
                const message = err.response.data || 'Invalid credentials. Please try again.';
                setError(message); // Set error message from response
            } else {
                setError('An unexpected error occurred. Please try again.');
            }
            console.error(err);
        }
    };

    const register = async (userData) => {
        try {
            await axios.post('http://localhost:8080/users/register', userData);
        } catch (error) {
            console.error('Registration failed:', error);
        }
    };

    const logout = () => {
        setUser(null);
        localStorage.removeItem('authToken');
        localStorage.removeItem('role');
        localStorage.removeItem('userId');
        delete axios.defaults.headers.common['Authorization'];
    };

    return (
        <AuthContext.Provider value={{ user, error, setError, login, register, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

// Hook to use the Auth context
export const useAuth = () => useContext(AuthContext);
