import React from "react";
import { useLocalState } from "../util/useLocalStorage";

export default function Login() {
    const [jwt, setJwt] = useLocalState("", "jwt");
    const [login, setLogin] = React.useState("");
    const [password, setPassword] = React.useState("");
    const [errorMessage, setErrorMessage] = React.useState(null);
    
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
                window.location.href = "/dashboard"
            })
            .catch(error => {
                setErrorMessage(error.message);
            });
    }

    return (
        <form>
            <div>
                <label htmlFor="username">Username</label>
                <input 
                    id="username" 
                    type="text" 
                    value={login}
                    onChange={e => setLogin(e.target.value)}
                />
            </div>
            <div>
                <label htmlFor="password">Password</label>
                <input 
                    id="password" 
                    type="password" 
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                />
            </div>
            <button type="button" onClick={() => handleSubmit()}>Login</button>
            {errorMessage && <p>{errorMessage}</p>}
        </form>
    );
}