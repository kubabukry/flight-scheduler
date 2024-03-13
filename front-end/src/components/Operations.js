import React  from "react";
import { useLocalState } from "../util/useLocalStorage";
import Header from "./Header";
import NavbarAdmin from "./NavbarAdmin";
import "../styles/ChangeTable.css"

export default function Operations(){
    const [jwt, setJwt] = useLocalState("", "jwt");
    const [operations, setOperations] = React.useState([]);
    const [newDuration, setNewDuration] = React.useState(0);
    const [message, setMessage] = React.useState('');
    const [showNotification, setShowNotification] = React.useState(false);
    const [selectedOperation, setSelectedOperation] = React.useState(null);


    React.useEffect(() => {
        fetchOperations();
    }, [jwt]);

    const fetchOperations = () => {
        fetch('http://localhost:8080/operations', {
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
    
    const handleSubmitDuration = () => {
        if (!newDuration || isNaN(newDuration) || newDuration < 1) {
            setShowNotification(true);
            setTimeout(() => setShowNotification(false), 6000);
        } else {
            fetch(`http://localhost:8080/operations/${selectedOperation.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            body: JSON.stringify({ duration: newDuration })
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

    return (
            <div>
                <Header />
                <NavbarAdmin />
                <div className="change-table-container">
                    <table id="operations-list" className="change-table">
                        <thead className="change-table-header">
                            <tr>
                                <th className="change-table-header-cell">Operation Name</th>
                                <th className="change-table-header-cell">Duration</th>
                            </tr>
                        </thead>
                        <tbody>
                            {operations.map(operation => (
                                <tr 
                                    key={operation.id} 
                                    className="change-table-row"
                                    onClick={() => setSelectedOperation(operation)}
                                >
                                    <td className="change-table-cell">{operation.name}</td>
                                    <td className="change-table-cell">{operation.duration}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
                <div className="change-container">
                    <div className="change-elements">
                        <div className="change-input-title">
                        <div className="change-title">
                            Selected Operation: {selectedOperation ? selectedOperation.name : 'None'}
                        </div>
                        <input className="change-input"
                            id="num" 
                            type="number" 
                            value={newDuration}
                            onChange={e => setNewDuration(e.target.value)}
                        />
                        </div>
                        <button className="change-button" type="button" onClick={handleSubmitDuration}>Submit</button>
                        {showNotification && <div className="notification-operations">Provided incorrect data</div>}
                    </div>
                </div>
            </div>
    );
}