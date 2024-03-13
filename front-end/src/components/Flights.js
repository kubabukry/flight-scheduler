import React, { useEffect, useState } from "react";
import { useLocalState }  from "../util/useLocalStorage";
import Header from "./Header";
import Navbar from "./Navbar";
import '../styles/Flights.css';

export default function Flights(){
    const [jwt, setJwt] = useLocalState("", "jwt");
    const [flights, setFlights] = useState([]);
    const [prevFlightCount, setPrevFlightCount] = useState(0);
    const [flightCount, setFlightCount] = useState(0);
    const [isFirstLoad, setIsFirstLoad] = useState(true);
    const [showNotification, setShowNotification] = useState(false);

    useEffect(() => {
        if (flightCount !== 0) {
            setShowNotification(true);
            setTimeout(() => setShowNotification(false), 30000); // Hide after 30 seconds
        }
    }, [flightCount]);

    useEffect(() => {
        const fetchFlights = () => {
        fetch(`http://localhost:8080/flights`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            }
        })
        .then(response => response.json())
        .then(json => {
            const updatedFlights = json.map(flight => {
                
                let firstSeen = new Date(flight.firstSeen);
                let plannedArrival = new Date(flight.plannedArrival);
                let plannedDeparture = new Date(flight.plannedDeparture);
    
                
                firstSeen.setHours(firstSeen.getHours() + 1);
                plannedArrival.setHours(plannedArrival.getHours() + 1);
                plannedDeparture.setHours(plannedDeparture.getHours() + 1);
    
                return {
                    ...flight,
                    firstSeen: firstSeen.toISOString().substring(0, 16).replace('T', ' '),
                    plannedArrival: plannedArrival.toISOString().substring(0, 16).replace('T', ' '),
                    plannedDeparture: plannedDeparture.toISOString().substring(0, 16).replace('T', ' ')
                };
            });
            return updatedFlights;
        })
        .then(data => {
            const newFlightCount = data.length - prevFlightCount;
            if (newFlightCount > 0 && !isFirstLoad) {
                setFlightCount(newFlightCount); // Update the flight count with the difference
            }
            setPrevFlightCount(data.length); // Update the previous flight count
            setFlights(data);
            setIsFirstLoad(false); // Set the first load flag to false after the first load
        })
        .catch((error) => {
            console.error('Error:', error);
        });
    }
    
        fetchFlights();
    
        const intervalId = setInterval(fetchFlights, 60000); // 60000 ms = 1 minute
    
        return () => clearInterval(intervalId);
    }, [jwt]);

    return(
        <div className="flights-container">
    <Header />
    <Navbar />
    <div className="resource-container">
        <h3 className="flights-heading">Incoming Flights</h3>
        {showNotification && <div className="notification">New tasks added: {flightCount}</div>}
    </div>
    <table id="flights-list" className="flights-table">
        <thead className="flights-table-header">
            <tr className="flights-table-row">
                <th className="flights-table-header-cell">Flight Number</th>
                <th className="flights-table-header-cell">Destination</th>
                <th className="flights-table-header-cell">Arrived</th>
                <th className="flights-table-header-cell">Planned Arrival</th>
                <th className="flights-table-header-cell">Planned Departure</th>
            </tr>
        </thead>
        <tbody className="flights-table-body">
            {flights.map(flight => (
                <tr key={flight.flightNumber} className="flights-table-row">
                    <td className="flights-table-cell">{flight.flightNumber}</td>
                    <td className="flights-table-cell">{flight.destination}</td>
                    <td className="flights-table-cell">{flight.firstSeen}</td>
                    <td className="flights-table-cell">{flight.plannedArrival}</td>
                    <td className="flights-table-cell">{flight.plannedDeparture}</td>
                </tr>
            ))}
        </tbody>
    </table>
</div>
    )
};
