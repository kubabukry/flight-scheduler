import './App.css';
import React from 'react';
import { Routes, Route } from 'react-router';
import Dashboard from './components/Dashboard';
import Homepage from './components/Homepage';
import Login from './components/Login';
import PrivateRoute from './util/PrivateRoute';

function App() {

  return (
    <Routes>
      <Route 
        path="/dashboard" 
        element={
          <PrivateRoute>
            <Dashboard/>
          </PrivateRoute>
        } />
      <Route path="/" element={<Login/>} />
    </Routes>
  );
}

export default App;
