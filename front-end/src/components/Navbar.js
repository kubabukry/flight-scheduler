import React from 'react';
import { Link } from 'react-router-dom';

function Navbar() {
  return (
    <nav>
      <ul>
        <li><Link to="/schedules">Schedules</Link></li>
        <li><Link to="/flights">Flights</Link></li>
      </ul>
    </nav>
  );
}

export default Navbar;