import React, { useEffect, useState } from "react";
import { useLocalState }  from "../util/useLocalStorage";
import Header from "./Header";

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
        fetch(`http://localhost:8080/flight/all`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            }
        })
        .then(response => response.json())
        .then(json => {
            const updatedFlights = json.map(flight => {
                return {
                    ...flight,
                    firstSeen: flight.firstSeen.substring(0, 16).replace('T', ' '),
                    plannedArrival: flight.plannedArrival.substring(0, 16).replace('T', ' '),
                    plannedDeparture: flight.plannedDeparture.substring(0, 16).replace('T', ' ')
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
    
        // Fetch tasks immediately
        fetchFlights();
    
        // Then fetch tasks every minute
        const intervalId = setInterval(fetchFlights, 60000); // 60000 ms = 1 minute
    
        // Clear interval on component unmount
        return () => clearInterval(intervalId);
    }, [jwt]);

    return(
        <div>
            <Header />
            <h3>Incoming Flights</h3>
            {showNotification && <div className="notification">New tasks added: {flightCount}</div>}
            <table id="flights-list">
                <thead>
                    <tr>
                        <th>Flight Number</th>
                        <th>Destination</th>
                        <th>Arrived</th>
                        <th>Planned Arrival</th>
                        <th>Planned Departure</th>
                    </tr>
                </thead>
                <tbody>
                    {flights.map(flight => (
                        <tr key={flight.flightNumber}>
                            <td>{flight.flightNumber}</td>
                            <td>{flight.destination}</td>
                            <td>{flight.firstSeen}</td>
                            <td>{flight.plannedArrival}</td>
                            <td>{flight.plannedDeparture}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )
};
