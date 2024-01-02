import React from 'react';
import AdminCreateUser from './AdminCreateUser';
import AdminUserList from './AdminUserList';

export default function Dashboard() {
    
    return (
        <div>
            <AdminUserList />
            <AdminCreateUser />
        </div>
    );
};