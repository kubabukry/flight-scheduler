import React, { useEffect, useState } from "react";
import { useLocalState }  from "../util/useLocalStorage";
import { jwtDecode } from 'jwt-decode';
import Header from "./Header";
import Navbar from "./Navbar";


export default function Schedules(){
    const [resource, setResource] = useState('');
    const [jwt, setJwt] = useLocalState("", "jwt");
    const [resourceId, setResourceId] = useState('');
    const [tasks, setTasks] = useState([]);
    const [taskCount, setTaskCount] = useState(0);
    const [showNotification, setShowNotification] = useState(false);
    const [prevTaskCount, setPrevTaskCount] = useState(0);
    const [isFirstLoad, setIsFirstLoad] = useState(true);

    const decodedToken = jwtDecode(jwt);
    const login = decodedToken.sub;
    
    useEffect(() => {
        if (taskCount !== 0) {
            setShowNotification(true);
            setTimeout(() => setShowNotification(false), 30000); // Hide after 30 seconds
        }
    }, [taskCount]);

    useEffect(() => {
        fetch(`http://localhost:8080/resource/person/${login}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            }
        })
        .then(response => response.json())
        .then(data => {
            setResource(data);
            setResourceId(data.id);
        })
        .catch(error => console.error('Error:', error));
    }, [jwt, login]);

    useEffect(() => {
        const fetchTasks = () => {
        if(resourceId !== ''){
            fetch(`http://localhost:8080/task/resource/${resourceId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}`
                }
            })
            .then(response => response.json())
            .then(json => {
                const updatedTasks = json.map(task => {
                    return {
                        ...task,
                        deadline: task.deadline.substring(0, 16).replace('T', ' '),
                        start: task.start.substring(0, 16).replace('T', ' '),
                        finish: task.finish.substring(0, 16).replace('T', ' ')
                    };
                });
                return updatedTasks;
            })
            .then(data => {
                const newTaskCount = data.length - prevTaskCount;
                if (newTaskCount > 0 && !isFirstLoad) {
                    setTaskCount(newTaskCount); // Update the task count with the difference
                }
                setPrevTaskCount(data.length); // Update the previous task count
                setTasks(data);
                setIsFirstLoad(false);
            })
            .catch((error) => {
                console.error('Error:', error);
            });
        }
    }
    
        // Fetch tasks immediately
        fetchTasks();
    
        // Then fetch tasks every minute
        const intervalId = setInterval(fetchTasks, 60000); // 60000 ms = 1 minute
    
        // Clear interval on component unmount
        return () => clearInterval(intervalId);
    }, [jwt, resourceId, prevTaskCount, isFirstLoad]);



    return (
        <div>
            <Header />
            <Navbar />
            <h3>Resource: {resource.name}</h3>
            <h3>Available: {resource.available}</h3>
            {showNotification && <div className="notification">New tasks added: {taskCount}</div>}
            <table id="tasks-list">
                <thead>
                    <tr>
                        <th>Deadline</th>
                        <th>Flight Number</th>
                        <th>Resource</th>
                        <th>Operation</th>
                        <th>Start</th>
                        <th>Finish</th>
                        <th>Priority</th>
                    </tr>
                </thead>
                <tbody>
                    {tasks.map(task => (
                        <tr key={task.id}>
                            <td>{task.deadline}</td>
                            <td>{task.flightNumber}</td>
                            <td>{task.resourceName}</td>
                            <td>{task.operationName}</td>
                            <td>{task.start}</td>
                            <td>{task.finish}</td>
                            <td>{task.priority}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};