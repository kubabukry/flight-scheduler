import React from "react";
import { useLocalState } from "../util/useLocalStorage";
import '../styles/Login.css';

export default function Login() {
    const [jwt, setJwt] = useLocalState("", "jwt");
    const [login, setLogin] = React.useState("");
    const [password, setPassword] = React.useState("");
    const [errorMessage, setErrorMessage] = React.useState(null);
    const [userRole, setUserRole] = React.useState(null);

    let logo = `${process.env.PUBLIC_URL}/header-logo.png`;
    
    function handleSubmit() {

            const reqBody = {
              login: login,
              password: password
            };

            fetch('http://localhost:8080/auth/login', {
                headers: {
                    "Content-Type": "application/json"
                },
                method: "post",
                body: JSON.stringify(reqBody),
            })
            .then((response) => {
                if(response.status === 200){
                    return response.json();
                } else {
                    throw new Error("Invalid username or password");
                }
            })
            .then((json) => {
                setJwt(json.token)

                fetch(`http://localhost:8080/person/login/${login}`, {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${json.token}`
                    },
                    method: "get"
                })
                .then(response => response.json())
                .then(userJson => {
                    if(userJson.role === 'ADMIN'){
                        window.location.href = "/users"
                    } else if (userJson.role){
                        window.location.href = "/schedules"
                    }
                })
                .catch(error => console.error('Error:', error));
            })
            .catch(error => {
                setErrorMessage(error.message);
            });
    }

    return (
        <form className="authentication-container">
            <img className="authentication-logo" src={logo} alt="logo" />
            <div className="login-container">
                <label htmlFor="username">Username</label>
                <input className="authentication-input"
                    id="username" 
                    type="text" 
                    value={login}
                    onChange={e => setLogin(e.target.value)}
                />
            </div>
            <div className="password-containter">
                <label htmlFor="password">Password</label>
                <input className="authentication-input"
                    id="password" 
                    type="password" 
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                />
            </div>
            <button className="authentication-button" type="button" onClick={() => handleSubmit()}>Login</button>
            {errorMessage && <p>{errorMessage}</p>}
        </form>
    );
}