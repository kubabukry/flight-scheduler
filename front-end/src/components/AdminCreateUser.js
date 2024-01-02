import React from 'react';
import { useLocalState } from '../util/useLocalStorage';

export default function AdminCreateUser({ onUserCreated, selectedUser }) {
    const [jwt, setJwt] = useLocalState("", "jwt");
    const [login, setLogin] = React.useState("");
    const [password, setPassword] = React.useState("");
    const [firstName, setFirstName] = React.useState("");
    const [lastName, setLastName] = React.useState("");
    const [role, setRole] = React.useState("");

    React.useEffect(() => {
        if (selectedUser) {
            setLogin(selectedUser.login);
            setPassword(selectedUser.password);
            setFirstName(selectedUser.firstName);
            setLastName(selectedUser.lastName);
            setRole(selectedUser.role);
        }
    }, [selectedUser]);

    const errorBody = {
        statusCode: "",
        message: "Unexpected error happened"
    }
    
    const userDetails = {
        login,
        password,
        firstName,
        lastName,
        role
    };
    
    const validRoles = ["ADMIN", "FLIGHT_CONTROL", "GROUND_PILOT", "SERVICE", "STAFF"];

    const createUser = () => {
        if(!validRoles.includes(role)){
            alert("You must specify role");
            return;
        }

        fetch('http://localhost:8080/person/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            body: JSON.stringify(userDetails)
        })
        .then(response => {
            if(response.ok){
                alert(`User ${login} created`)
                onUserCreated();
            } else {
                return response.json()
                .then(json => {
                    errorBody.statusCode = json.statusCode;
                    errorBody.message = json.message;
                    throw new Error();
                });
            }
        })
        .catch(() => {
            alert(`Error ${errorBody.statusCode}: ${errorBody.message}`);
        });
    }
    
    return (
        <div id=''>
            <label htmlFor='login'>Login</label>
            <input 
                id='login' 
                type="text" 
                value={login} 
                onChange={e => setLogin(e.target.value)}
            />

            <label htmlFor='password'>Password</label>
            <input 
                id='password' 
                type="password" 
                value={password ?? ""} 
                onChange={e => setPassword(e.target.value)} 
            />

            <label htmlFor='first-name'>First Name</label>
            <input 
                id='first-name'
                type='text' 
                value={firstName} 
                onChange={e => setFirstName(e.target.value)} 
            />

            <label htmlFor='last-name'>Last Name</label>
            <input 
                id='last-name' 
                type='text' 
                value={lastName} 
                onChange={e => setLastName(e.target.value)} 
            />

            <label htmlFor='role'>Role</label>
            <select
                id='role'
                value={role}
                onChange={e => setRole(e.target.value)}
            >   
                <option value="">--choose Role--</option>
                <option value="ADMIN">ADMIN</option>
                <option value="FLIGHT_CONTROL">FLIGHT_CONTROL</option>
                <option value="GROUND_PILOT">GROUND_PILOT</option>
                <option value="SERVICE">SERVICE</option>
                <option value="STAFF">STAFF</option>
            </select>

            <button id='create-button' onClick={createUser}>Create User</button>
        </div>
    );
};