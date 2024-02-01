import React from 'react';
import { Link } from 'react-router-dom';

function NavbarAdmin() {
  return (
    <nav>
      <ul>
        <li><Link to="/users">Users</Link></li>
        <li><Link to="/resources">Resources</Link></li>
        <li><Link to="/operations">Operations</Link></li>
      </ul>
    </nav>
  );
}

export default NavbarAdmin;