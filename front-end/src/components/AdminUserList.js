import React from "react";
import { useLocalState } from "../util/useLocalStorage";

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
                    dateCreated: user.dateCreated.substring(0, 19),
                    dateModified: user.dateModified.substring(0, 19)
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
        <table id="users-list">
            <thead>
                <tr>
                    <th>Login</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Date Created</th>
                    <th>Date Modified</th>
                    <th>Role</th>
                </tr>
            </thead>
            <tbody>
                {users.map(user => (
                    <tr key={user.id} onClick={() => handleRowClick(user)}>
                        <td>{user.login}</td>
                        <td>{user.firstName}</td>
                        <td>{user.lastName}</td>
                        <td>{user.dateCreated}</td>
                        <td>{user.dateModified}</td>
                        <td>{user.role}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
}
