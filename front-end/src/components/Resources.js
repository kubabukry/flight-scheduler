import React from "react";
import { useLocalState } from "../util/useLocalStorage";
import Header from "./Header";
import NavbarAdmin from "./NavbarAdmin";
import "../styles/Schedules.css"
import "../styles/Admin.css"

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
        const newAvailableValue = newAvailable[resourceId];
        if (!newAvailableValue || isNaN(newAvailableValue) || newAvailableValue < 1) {
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


    return (
        <div>
            <Header />
            <NavbarAdmin />
            {showNotification && <div className="notification-resources">Wrong resource number provided</div>}
            <table id="resources-list" className="tasks-table">
                <thead className="tasks-table-header">
                    <tr>
                        <th className="tasks-table-header-cell">Name</th>
                        <th className="tasks-table-header-cell">Available</th>
                        <th className="tasks-table-header-cell">Change Available</th>
                    </tr>
                </thead>
                <tbody>
                    {resources.map(resource => (
                        <tr key={resource.id} className="tasks-table-row">
                            <td className="resource-table-cell">{resource.name}</td>
                            <td className="resource-table-cell">{resource.available}</td>
                            <td className="resource-table-cell">
                                <div className="input-button-container">
                                    <input type="number" min="1" value={newAvailable[resource.id] || ''} onChange={(e) => handleAvailableChange(e, resource.id)} />
                                    <button onClick={() => handleSubmit(resource.id)}>Submit</button>
                                </div>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

/* <table className="change-table">
                        <thead className="change-table-header">
                            <tr>
                                <th className="change-table-header-cell">Name</th>
                                <th className="change-table-header-cell">Available</th>
                            </tr>
                        </thead>
                        <tbody>
                            {resources.map(resource => (
                                <tr key={resource.id} className="change-table-row">
                                    <td className="change-table-cell">{resource.name}</td>
                                    <td className="change-table-cell">{resource.available}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table> */