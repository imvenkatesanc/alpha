import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import LoginForm from './LoginForm';
import Dashboard from './Dashboard'; // Placeholder for the user's dashboard component
import RegistrationForm from './RegistrationForm';
import CreateProperty from './CreateProperty';
import PropertyView from './PropertyView';
function App() {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<LoginForm />} />
                <Route path="/register" element={<RegistrationForm />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/create-property" element={<CreateProperty/>} />
                <Route path="/view-property" element={<PropertyView/>} />
            </Routes>
        </Router>
    );
}

export default App;
