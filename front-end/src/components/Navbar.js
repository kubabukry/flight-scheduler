import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/Navbar.css';

function Navbar() {
  return (
    <nav className='navbar'>
        <div className='navbar-item'><Link className='text' to="/schedules">Schedules</Link></div>
        <div className='navbar-item'><Link className='text' to="/flights">Flights</Link></div>
    </nav>
  );
}

export default Navbar;