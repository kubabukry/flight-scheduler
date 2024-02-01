import React from "react";
import { useLocalState } from "../util/useLocalStorage";
import Header from "./Header";
import NavbarAdmin from "./NavbarAdmin";

export default function Resources(){
    const [jwt, setJwt] = useLocalState("", "jwt");
    const [resources, setResources] = React.useState([]);
    const [newAvailable, setNewAvailable] = React.useState(0);
    const [message, setMessage] = React.useState('');
    const [showNotification, setShowNotification] = React.useState(false);

    React.useEffect(() => {
        fetchResources();
    }, [jwt]);

    const fetchResources = () => {
        fetch('http://localhost:8080/resource/all', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            }
        })
        .then(response => response.json())
        .then(json => {
            setResources(json);
        });
    };


    const handleAvailableChange = (e, resourceId) => {
        setNewAvailable({...newAvailable, [resourceId]:  e.target.value});
    };
    
    const handleSubmit = (resourceId) => {
        if (newAvailable[resourceId] < 1) {
            setShowNotification(true);
            setTimeout(() => setShowNotification(false), 30000);
        } else {
            fetch(`http://localhost:8080/resource/update/${resourceId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            body: JSON.stringify({ available: newAvailable[resourceId] })
            })
            .then(response => {
                if(response.ok){
                    fetchResources();
                } else {
                    return response.json()
                    .then(json => {
                        setMessage(`Error: ${json.message}`);
                    });
                }
            })
            .catch((error) => {
                setMessage(`Error: ${error.message}`);
            });
        }
    };


    return(
        <div>
            <Header />
            <NavbarAdmin />
            {showNotification && <div className="notification">Available number cannot be less than 1</div>}
            <table id="resources-list">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Available</th>
                    <th>Change Available</th>
                </tr>
            </thead>
            <tbody>
            {resources.map(resource => (
                <tr key={resource.id}>
                    <td>{resource.name}</td>
                    <td>{resource.available}</td>
                    <td>
                        <input type="number" min="1" value={newAvailable[resource.id] || ''} onChange={(e) => handleAvailableChange(e, resource.id)} />
                        <button onClick={() => handleSubmit(resource.id)}>Submit</button>
                    </td>
                </tr>
                ))}
            </tbody>
        </table>
        </div>
    )
}