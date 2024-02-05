import React, { useState, useEffect } from 'react';
import { useLocalState }  from "../util/useLocalStorage";
import { jwtDecode } from 'jwt-decode';
import '../styles/Header.css';

function Header() {
    let logo = `${process.env.PUBLIC_URL}/header-logo.png`;
    let user_icon = `${process.env.PUBLIC_URL}/user-icon.png`;

    const formatTime = (date) => {
        const hours = date.getHours();
        const minutes = date.getMinutes();
        const seconds = date.getSeconds();
        return `${hours < 10 ? '0' + hours : hours}:${minutes < 10 ? '0' + minutes : minutes}:${seconds < 10 ? '0' + seconds : seconds}`;
    };
    const [jwt, setJwt] = useLocalState("", "jwt");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [time, setTime] = useState(formatTime(new Date()));

    const decodedToken = jwtDecode(jwt);
    const login = decodedToken.sub;

    useEffect(() => {
        const timer = setInterval(() => {
            setTime(formatTime(new Date()));
        }, 1000);
        return () => clearInterval(timer);
    }, []);

    const handleLogout = () => {
        setJwt(""); // clear the JWT token
        window.location.href = "/"; // redirect to login page
    };

    useEffect(() => {
        const decodedToken = jwtDecode(jwt);
        const currentTime = Date.now() / 1000;
    
        if (decodedToken.exp < currentTime) {
            handleLogout();
        }
    }, [jwt]);

    useEffect(() => {
        fetch(`http://localhost:8080/person/login/${login}`, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${jwt}`
        },
        method: "GET"
        })
        .then(response => response.json())
        .then(data => {
            setFirstName(data.firstName);
            setLastName(data.lastName);
        })
        .catch(error => console.error('Error:', error));
        }, [jwt, login]
    )


    return (
        <header id="header">
            <div className="left-section">
                <img className="logo" src={logo} alt="logo" />
                <h1 className='title'>Flight Scheduler</h1>
            </div>
            <div className="right-section">
                <button className="logout" onClick={handleLogout}>Logout</button>
                <h3 className='user'>
                    {firstName} {lastName}
                </h3>
                <h3 className="current-time">{time}</h3>
            </div>
        </header>
    );
}

export default Header;