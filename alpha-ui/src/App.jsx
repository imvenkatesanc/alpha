import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import LoginForm from './LoginForm';
import Dashboard from './Dashboard'; // Placeholder for the user's dashboard component
import RegistrationForm from './RegistrationForm';
import Home from './Home';
import CreateProperty from './CreateProperty';
import PropertyView from './PropertyView';
import PropertyFeed from './PropertyFeed';
import MyProfile from './MyProfile';
import SideNav from './SideNav';

// Example authentication check function
const isAuthenticated = () => {
    // Replace with your authentication logic
    return !!localStorage.getItem('authToken');
};

// Private Route component
const PrivateRoute = ({ element }) => {
    return isAuthenticated() ? (
        <div style={{ display: 'flex' }}>
            <SideNav />
            <div style={{ flex: 1, padding: '20px', overflowY: 'auto' }}>
                {element}
            </div>
        </div>
    ) : (
        <Navigate to="/login" />
    );
};

function App() {
    return (
        <>
            <Router>
                <Routes>
                    <Route path='/' element={<Home />} />
                    <Route path="/login" element={<LoginForm />} />
                    <Route path="/register" element={<RegistrationForm />} />

                    {/* Private Routes */}
                    <Route path="/dashboard" element={<PrivateRoute element={<Dashboard />} />} />
                    <Route path="/myprofile" element={<PrivateRoute element={<MyProfile />} />} />
                    <Route path="/my-properties" element={<PrivateRoute element={<PropertyView />} />} />
                    <Route path="/create-property" element={<PrivateRoute element={<CreateProperty />} />} />
                    <Route path="/feed-property" element={<PrivateRoute element={<PropertyFeed />} />} />
                    {/* Add other private routes here */}
                </Routes>
            </Router>
        </>
    );
}

export default App;
