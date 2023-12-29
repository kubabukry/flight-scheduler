import './App.css';
import React from 'react';
import { useLocalState } from './util/useLocalStorage';

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
    <div className="App">
     <h1>Hello world!</h1>
     <div>JWT value: {jwt}</div>
    </div>
  );
}

export default App;
