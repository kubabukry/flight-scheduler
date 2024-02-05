import React from "react";
import { useLocalState } from "../util/useLocalStorage";
import Header from "./Header";
import NavbarAdmin from "./NavbarAdmin";
import "../styles/ChangeTable.css"

export default function Resources(){
    const [jwt, setJwt] = useLocalState("", "jwt");
    const [resources, setResources] = React.useState([]);
    const [newAvailable, setNewAvailable] = React.useState(0);
    const [message, setMessage] = React.useState('');
    const [showNotification, setShowNotification] = React.useState(false);
    const [selectedResource, setSelectedResource] = React.useState(null);

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

    
    const handleSubmitAvailable = (resourceId) => {
        if (!newAvailable || isNaN(newAvailable) || newAvailable < 1) {
            setShowNotification(true);
            setTimeout(() => setShowNotification(false), 30000);
        } else {
            fetch(`http://localhost:8080/resource/update/${selectedResource.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            body: JSON.stringify({ available: newAvailable })
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
                <div className="change-table-container">
                        <table id="resources-list" className="change-table">
                            <thead className="change-table-header">
                                <tr>
                                    <th className="change-table-header-cell">Name</th>
                                    <th className="change-table-header-cell">Available</th>
                                </tr>
                            </thead>
                            <tbody>
                                {resources.map(resource => (
                                    <tr key={resource.id} 
                                        className="change-table-row"
                                        onClick={() => setSelectedResource(resource)}
                                    >
                                        <td className="change-table-cell">{resource.name}</td>
                                        <td className="change-table-cell">{resource.available}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                </div>
                <div className="change-container">
                        <div className="change-elements">
                            <div className="change-input-title">
                            <div className="change-title">
                                Selected Resource: {selectedResource ? selectedResource.name : 'None'}
                            </div>
                            <input className="change-input"
                                id="num" 
                                type="number" 
                                value={newAvailable}
                                onChange={e => setNewAvailable(e.target.value)}
                            />
                            </div>
                            <button className="change-button" type="button" onClick={handleSubmitAvailable}>Submit</button>
                            {showNotification && <div className="notification-resources">Provided incorrect data</div>}
                        </div>
                </div>
        </div>
    )
}