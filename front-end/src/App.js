import './App.css';
import React from 'react';
import { Routes, Route } from 'react-router';
import Dashboard from './components/Dashboard';
import Login from './components/Login';
import PrivateRoute from './util/PrivateRoute';
import Schedules from './components/Schedules';
import Flights from './components/Flights';
import Header from './components/Header';
import Resources from './components/Resources';
import Operations from './components/Operations';

function App() {

  return (
    <Routes>
      <Route 
        path="/users" 
        element={
          <PrivateRoute>
            <Dashboard/>
          </PrivateRoute>
        } />
        <Route 
        path="/operations" 
        element={
          <PrivateRoute>
            <Operations/>
          </PrivateRoute>
        } />
        <Route 
        path="/resources" 
        element={
          <PrivateRoute>
            <Resources/>
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
