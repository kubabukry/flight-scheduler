import './App.css';
import React from 'react';
import { useLocalState } from './util/useLocalStorage';
import { Routes, Route } from 'react-router';
import Dashboard from './components/Dashboard';
import Homepage from './components/Homepage';

function App() {
  const [jwt, setJwt] = useLocalState("", "jwt");

  React.useEffect(() => {
    const reqBody = {
      login: "user",
      password: "P@ssw0rd"
    };
    
    if(!jwt){
      fetch('http://localhost:8080/person/auth/authenticate', {
        headers: {
          "Content-Type": "application/json"
        },
        method: "post",
        body: JSON.stringify(reqBody),
      })
      .then(response => response.json())
      .then(json => setJwt(json.token));
    }
  }, []);

  return (
    <Routes>
      <Route path="/dashboard" element={<Dashboard/>} />
      <Route path="/" element={<Homepage/>} />
    </Routes>
  );
}

export default App;
