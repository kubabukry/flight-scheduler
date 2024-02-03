import React from 'react';
import { useLocalState } from '../util/useLocalStorage';
import '../styles/CreateUser.css';

export default function AdminCreateUser({ onUserChange, selectedUser }) {
    const [jwt, setJwt] = useLocalState("", "jwt");
    const [id, setId] = React.useState(0)
    const [login, setLogin] = React.useState("");
    const [password, setPassword] = React.useState("");
    const [firstName, setFirstName] = React.useState("");
    const [lastName, setLastName] = React.useState("");
    const [role, setRole] = React.useState("");
    const [message, setMessage] = React.useState(null);

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
    
    const validRoles = ["ADMIN", "FLIGHT_CONTROL", "GROUND_PILOT", "BRIDGE_CREW", "FUELING_CREW", "CABIN_MAINTENANCE", "BAGGAGE_CREW"];

    const createUser = () => {
        if(!validRoles.includes(role)){
            setMessage("You must specify role");
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
                setMessage(`User ${login} created`)
                onUserChange();
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
            setMessage(`Error: ${errorBody.message}`);
        });
    };

    const updateUser = () => {
        if(!validRoles.includes(role)){
            setMessage("You must specify role");
            return;
        }
    
        fetch(`http://localhost:8080/person/update/${selectedUser.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            body: JSON.stringify(userDetails)
        })
        .then(response => {
            if(response.ok){
                setMessage(`User ${userDetails.login} updated`)
                onUserChange();
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
            setMessage(`Error: ${errorBody.message}`);
        });
    };

    const deleteUser = () => {
        fetch(`http://localhost:8080/person/delete/${selectedUser.id}`, {
            method: "DELETE",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            }
        })
        .then(response => {
            if(response.ok){
                setMessage(`User ${selectedUser.login} deleted`);
                onUserChange()
            } else {
                throw new Error()
            }
        })
        .catch(() => {
            setMessage("Unexpected error happened")
        })
    }
    
    return (
    <div>
      <div className='create-user-container'>  
        <div className='inputs-user-container'>
            <label className='create-label' htmlFor='login'>Login</label>
            <input 
                id='login' 
                type="text" 
                value={login} 
                onChange={e => setLogin(e.target.value)}
            />

            <label className='create-label' htmlFor='password'>Password</label>
            <input 
                id='password' 
                type="password" 
                value={password ?? ""} 
                onChange={e => setPassword(e.target.value)} 
            />

            <label className='create-label' htmlFor='first-name'>First Name</label>
            <input 
                id='first-name'
                type='text' 
                value={firstName} 
                onChange={e => setFirstName(e.target.value)} 
            />

            <label className='create-label' htmlFor='last-name'>Last Name</label>
            <input 
                id='last-name' 
                type='text' 
                value={lastName} 
                onChange={e => setLastName(e.target.value)} 
            />

            <label className='create-label' htmlFor='role'>Role</label>
            <select className='select-role'
                id='role'
                value={role}
                onChange={e => setRole(e.target.value)}
            >   
                <option value="">--choose Role--</option>
                <option value="ADMIN">ADMIN</option>
                <option value="FLIGHT_CONTROL">FLIGHT_CONTROL</option>
                <option value="GROUND_PILOT">GROUND_PILOT</option>
                <option value="BRIDGE_CREW">BRIDGE_CREW</option>
                <option value="FUELING_CREW">FUELING_CREW</option>
                <option value="CABIN_MAINTENANCE">CABIN_MAINTENANCE</option>
                <option value="BAGGAGE_CREW">BAGGAGE_CREW</option>
            </select>
        </div>
        <div className='button-container'>
            <button id='create-button' onClick={createUser}>Create User</button>
            <button id='update-button' onClick={updateUser}>Update User</button>
            <button id='update-button' onClick={deleteUser}>Delete User</button>
        </div>
      </div>
      {message && <div className='message'>{message}</div>}
    </div>
    );
};