import './App.css';
import React from 'react';
import { Routes, Route } from 'react-router';
import Dashboard from './components/Dashboard';
import Login from './components/Login';
import PrivateRoute from './util/PrivateRoute';
import Schedules from './components/Schedules';
import Flights from './components/Flights';
import Header from './components/Header';

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
        <Route 
        path="/flights" 
        element={
          <PrivateRoute>
            <Flights/>
          </PrivateRoute>
        } />
        <Route 
        path="/header" 
        element={
          <PrivateRoute>
            <Header/>
          </PrivateRoute>
        } />
        <Route 
        path="/schedules" 
        element={
          <PrivateRoute>
            <Schedules/>
          </PrivateRoute>
        } />
      <Route path="/" element={<Login/>} />
    </Routes>
  );
}

export default App;
