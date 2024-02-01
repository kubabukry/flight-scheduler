import React from "react";
import { useLocalState } from "../util/useLocalStorage";
import Header from "./Header";
import NavbarAdmin from "./NavbarAdmin";

export default function Operations(){
    const [jwt, setJwt] = useLocalState("", "jwt");
    const [operations, setOperations] = React.useState([]);
    const [newDuration, setNewDuration] = React.useState(0);
    const [message, setMessage] = React.useState('');
    const [showNotification, setShowNotification] = React.useState(false);

    React.useEffect(() => {
        fetchOperations();
    }, [jwt]);

    const fetchOperations = () => {
        fetch('http://localhost:8080/operation/all', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            }
        })
        .then(response => response.json())
        .then(json => {
            setOperations(json);
        });
    };


    const handleAvailableChange = (e, operationId) => {
        setNewDuration({...newDuration, [operationId]:  e.target.value});
    };
    
    const handleSubmit = (operationId) => {
        if (newDuration[operationId] < 1) {
            setShowNotification(true);
            setTimeout(() => setShowNotification(false), 30000);
        } else {
            fetch(`http://localhost:8080/operation/update/${operationId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            body: JSON.stringify({ duration: newDuration[operationId] })
            })
            .then(response => {
                if(response.ok){
                    fetchOperations();
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
            <table id="operations-list">
            <thead> 
                <tr>
                    <th>Name</th>
                    <th>Duration</th>
                    <th>Change Duration</th>
                </tr>
            </thead>
            <tbody>
            {operations.map(operation => (
                <tr key={operation.id}>
                    <td>{operation.name}</td>
                    <td>{operation.duration}</td>
                    <td>
                        <input type="number" min="1" value={newDuration[operation.id] || ''} onChange={(e) => handleAvailableChange(e, operation.id)} />
                        <button onClick={() => handleSubmit(operation.id)}>Submit</button>
                    </td>
                </tr>
                ))}
            </tbody>
        </table>
        </div>
    )
}