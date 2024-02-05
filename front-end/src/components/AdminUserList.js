import React from "react";
import { useLocalState } from "../util/useLocalStorage";
import "../styles/Schedules.css"

export default function AdminUserList({ onUserSelected, setLogin, setPassword, setFirstName, setLastName, setRole }){
    const [jwt, setJwt] = useLocalState("", "jwt");
    const [users, setUsers] = React.useState([]);
    const [selectedUser, setSelectedUser] = React.useState(null);

    React.useEffect(() => {
        fetch('http://localhost:8080/person/all', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            }
        })
        .then(response => response.json())
        .then(json => {
            const updatedUsers = json.map(user => {
                return {
                    ...user,
                    dateCreated: user.dateCreated.substring(0, 16).replace('T', ' '),
                    dateModified: user.dateModified.substring(0, 16).replace('T', ' ')
                };
            });
            setUsers(updatedUsers);
        });
    }, [jwt]);

    const handleRowClick = (user) => {
        setSelectedUser(user)
        onUserSelected(user);
        setLogin(user.login || "");
        setPassword(user.password || "");
        setFirstName(user.firstName || "");
        setLastName(user.lastName || "");
        setRole(user.role || "");
    }

    return (
        <table id="users-list" className="tasks-table">
        <thead className="tasks-table-header">
            <tr>
                <th className="tasks-table-header-cell">Login</th>
                <th className="tasks-table-header-cell">First Name</th>
                <th className="tasks-table-header-cell">Last Name</th>
                <th className="tasks-table-header-cell">Date Created</th>
                <th className="tasks-table-header-cell">Date Modified</th>
                <th className="tasks-table-header-cell">Role</th>
            </tr>
        </thead>
        <tbody>
            {users.map(user => (
                <tr key={user.id} onClick={() => handleRowClick(user)} className="tasks-table-row">
                    <td className="tasks-table-cell">{user.login}</td>
                    <td className="tasks-table-cell">{user.firstName}</td>
                    <td className="tasks-table-cell">{user.lastName}</td>
                    <td className="tasks-table-cell">{user.dateCreated}</td>
                    <td className="tasks-table-cell">{user.dateModified}</td>
                    <td className="tasks-table-cell">{user.role}</td>
                </tr>
            ))}
        </tbody>
    </table>
    );
}
