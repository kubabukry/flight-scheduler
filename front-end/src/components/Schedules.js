import React, { useEffect, useState } from "react";
import { useLocalState }  from "../util/useLocalStorage";
import { jwtDecode } from 'jwt-decode';
import Header from "./Header";
import Navbar from "./Navbar";
import '../styles/Schedules.css';


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

                    let deadline = new Date(task.deadline);
                    let start = new Date(task.start);
                    let finish = new Date(task.finish);
            
                    deadline.setHours(deadline.getHours() + 1);
                    start.setHours(start.getHours() + 1);
                    finish.setHours(finish.getHours() + 1);
            
                    return {
                        ...task,
                        deadline: deadline.toISOString().substring(0, 16).replace('T', ' '),
                        start: start.toISOString().substring(0, 16).replace('T', ' '),
                        finish: finish.toISOString().substring(0, 16).replace('T', ' ')
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
    
        fetchTasks();
    
        const intervalId = setInterval(fetchTasks, 60000); // 60000 ms = 1 minute
    
        return () => clearInterval(intervalId);
    }, [jwt, resourceId, prevTaskCount, isFirstLoad]);



    return (
        <div className="schedules-container">
            <Header />
            <Navbar />
            <div className="resource-container">
                <h3>Resource: {resource.name}</h3>
                <h3>Available: {resource.available}</h3>
                {showNotification && <div className="notification">New tasks added: {taskCount}</div>}
            </div>
            <table id="tasks-list" className="tasks-table">
                <thead className="tasks-table-header">
                    <tr className="tasks-table-row">
                        <th className="tasks-table-header-cell">Deadline</th>
                        <th className="tasks-table-header-cell">Flight Number</th>
                        <th className="tasks-table-header-cell">Resource</th>
                        <th className="tasks-table-header-cell">Operation</th>
                        <th className="tasks-table-header-cell">Start</th>
                        <th className="tasks-table-header-cell">Finish</th>
                        <th className="tasks-table-header-cell">Priority</th>
                    </tr>
                </thead>
                <tbody className="tasks-table-body">
                    {tasks.map(task => (
                        <tr key={task.id} className="tasks-table-row">
                            <td className="tasks-table-cell">{task.deadline}</td>
                            <td className="tasks-table-cell">{task.flightNumber}</td>
                            <td className="tasks-table-cell">{task.resourceName}</td>
                            <td className="tasks-table-cell">{task.operationName}</td>
                            <td className="tasks-table-cell">{task.start}</td>
                            <td className="tasks-table-cell">{task.finish}</td>
                            <td className="tasks-table-cell">{task.priority}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
</div>
    );
};