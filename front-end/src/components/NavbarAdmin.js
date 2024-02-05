import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/Navbar.css';  

function NavbarAdmin() {
  return (
    <nav className='navbar'>
        <div className='navbar-item'><Link className='text' to="/users">Users</Link></div>
        <div className='navbar-item'><Link className='text' to="/resources">Resources</Link></div>
        <div className='navbar-item'><Link className='text' to="/operations">Operations</Link></div>
    </nav>
  );
}

export default NavbarAdmin;