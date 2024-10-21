import React from 'react';

function Dashboard() {
  // Get the role from local storage
  const role = localStorage.getItem('userRole'); // Assume role is stored as a string

  return (
    <div className="ROLE_ADMIN">
      <h1>Dashboard</h1>

      {role === 'ROLE_ADMIN' && (
        <section className="overview-section">
          <div className="card" style={{ backgroundColor: 'whitesmoke' }}>
            <h3>Properties</h3>
            <p>Count: 2</p>
          </div>

          <div className="card" style={{ backgroundColor: 'whitesmoke' }}>
            <h3>Tenant Information</h3>
            <p>Count: 2</p>
          </div>

          <div className="card" style={{ backgroundColor: 'whitesmoke' }}>
            <h3>Financial Summary</h3>
            <p>Total Income: $2000</p>
            <p>Total Expenses: $500</p>
            <p>Outstanding Payments: $300</p>
          </div>

          <div className="card" style={{ backgroundColor: 'whitesmoke' }}>
            <h3>Maintenance Requests</h3>
            <p>Count: 2</p>
          </div>

          <div className="card" style={{ backgroundColor: 'whitesmoke' }}>
            <h3>Upcoming Rent Due Dates</h3>
            <p>Count: 1</p>
          </div>
        </section>
      )}

      {role === 'ROLE_USER' && (
        <section className="overview-section">
          <div className="card" style={{ backgroundColor: 'whitesmoke' }}>
            <h3>Current Lease</h3>
            <p>Lease ID: 12345</p>
            <p>Lease Start: 01/01/2023</p>
            <p>Lease End: 01/01/2024</p>
          </div>

          <div className="card" style={{ backgroundColor: 'whitesmoke' }}>
            <h3>Payment History</h3>
            <p>Count: 2</p>
          </div>

          <div className="card" style={{ backgroundColor: 'whitesmoke' }}>
            <h3>Maintenance Requests</h3>
            <p>Count: 2</p>
          </div>

          <div className="card" style={{ backgroundColor: 'whitesmoke' }}>
            <h3>Upcoming Payments</h3>
            <p>Next Rent Due: 11/01/2023</p>
          </div>
        </section>
      )}

      {role === null && (
        <div className="card" style={{ backgroundColor: 'whitesmoke' }}>
          <h3>Error</h3>
          <p>User role not found. Please log in.</p>
        </div>
      )}
    </div>
  );
}

export default Dashboard;
