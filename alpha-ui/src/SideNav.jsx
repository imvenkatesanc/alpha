import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import logo from './assets/pms_logo.png';
import userProfile from './assets/user_profile.png';

const SideNav = () => {
  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isToggled, setIsToggled] = useState(false); // Default is false (toggle off)
  const navigate = useNavigate();

  const token = localStorage.getItem('authToken');
  const role = localStorage.getItem('userRole');

  const logout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    navigate('/'); // Navigate to home after logout
  };

  useEffect(() => {
    const fetchUserData = async () => {
      if (!token || !role) {
        setLoading(false);
        return;
      }

      try {
        const endpoint = role === 'ROLE_ADMIN'
          ? 'http://localhost:8080/users/get/admin/dashboard'
          : 'http://localhost:8080/users/get/user/profile';

        const response = await axios.get(endpoint, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUserData(response.data);
      } catch (err) {
        setError('Failed to fetch user data. Please try again later.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [token, role]);

  if (loading) return <div>Loading user data...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div id="nav-bar">
      <input 
        id="nav-toggle" 
        type="checkbox" 
        checked={!isToggled} // Checkbox should be unchecked when isToggled is false
        onChange={() => setIsToggled(!isToggled)} 
      />
      <div id="nav-header">
        {isToggled && <img src={logo} alt="pms-logo" className='pms-logo' />} {/* Conditionally render logo */}
        <label htmlFor="nav-toggle" className="nav-toggle-label">
          <span id="nav-toggle-burger"></span>
        </label>
        <hr />
      </div>
      <div id="nav-content">
        {role === 'ROLE_ADMIN' ? (
          <>
            <Link to="/dashboard" className="nav-button">
              <i className="fas fa-home"></i><span>Dashboard</span>
            </Link>
            <Link to="/create-property" className="nav-button">
              <i className="fas fa-plus"></i><span>Property Creation</span>
            </Link>
            <Link to="/my-properties" className="nav-button">
              <i className="fas fa-list"></i><span>My Properties</span>
            </Link>
            <Link to="/agreements" className="nav-button">
              <i className="fas fa-file-contract"></i><span>Agreements</span>
            </Link>
            <Link to="/myprofile" className="nav-button">
              <i className="fas fa-user"></i><span>My Profile</span>
            </Link>
            <Link to="/actions" className="nav-button">
              <i className="fas fa-cogs"></i><span>Actions</span>
            </Link>
            <Link to="/transactions" className="nav-button">
              <i className="fas fa-exchange-alt"></i><span>Transactions</span>
            </Link>
          </>
        ) : (
          <>
            <Link to="/dashboard" className="nav-button">
              <i className="fas fa-home"></i><span>Dashboard</span>
            </Link>
            <Link to="/feed-property" className="nav-button">
              <i className="fas fa-search"></i><span>Properties Feed</span>
            </Link>
            <Link to="/interested-properties" className="nav-button">
              <i className="fas fa-heart"></i><span>Interested Properties</span>
            </Link>
            <Link to="/purchased-properties" className="nav-button">
              <i className="fas fa-shopping-cart"></i><span>Purchased Properties</span>
            </Link>
            <Link to="/requests" className="nav-button">
              <i className="fas fa-envelope"></i><span>Requests</span>
            </Link>
          </>
        )}
      </div>
      <input id="nav-footer-toggle" type="checkbox" />
      <div id="nav-footer">
        <div id="nav-footer-heading">
          <div id="nav-footer-avatar">
            <img src={userProfile} alt="User Avatar" />
          </div>
          <div id="nav-footer-titlebox">
            <span>{userData?.name || 'User'}</span>
            <span id="nav-footer-subtitle">{role === 'ROLE_ADMIN' ? 'Admin' : 'User'}</span>
          </div>
          <label htmlFor="nav-footer-toggle">
            <i className="fas fa-caret-up"></i>
          </label>
        </div>
        <div id="nav-footer-content">
          <button onClick={logout} className="btn-logout-btn">Logout</button>
        </div>
      </div>
    </div>
  );
};

export default SideNav;
