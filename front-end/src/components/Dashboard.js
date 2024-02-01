import React from 'react';
import AdminCreateUser from './AdminCreateUser';
import AdminUserList from './AdminUserList';
import Header from './Header';
import NavbarAdmin from './NavbarAdmin';

export default function Dashboard() {
    const [refreshKey, setRefreshKey] = React.useState(0);
    const [selectedUser, setSelectedUser] = React.useState(null);
    const [login, setLogin] = React.useState("");
    const [password, setPassword] = React.useState("");
    const [firstName, setFirstName] = React.useState("");
    const [lastName, setLastName] = React.useState("");
    const [role, setRole] = React.useState("");

    const handleSelectedUser = (user) => {
        setSelectedUser(user);
        setLogin(user.login);
        setPassword(user.password);
        setFirstName(user.firstName);
        setLastName(user.lastName);
        setRole(user.role);
    }

    const handleUserChange = () => {
        setRefreshKey(oldKey => oldKey + 1);
    }
    
    return (
        <div>
            <Header />
            <NavbarAdmin />
            <AdminUserList 
                key={refreshKey}
                onUserSelected={handleSelectedUser}
                setLogin={setLogin}
                setPassword={setPassword}
                setFirstName={setFirstName}
                setLastName={setLastName}
                setRole={setRole}
            />
            <AdminCreateUser 
                selectedUser={selectedUser}
                onUserChange={handleUserChange}
            />
        </div>
    );
};