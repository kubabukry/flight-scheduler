import React from 'react';
import { useLocalState } from '../util/useLocalStorage';

export default function Dashboard() {
    const [jwt, setJwt] = useLocalState("", "jwt");
    return (
        <>
            <h1>Hello world</h1>
            <div>JWT value: {jwt}</div>
        </>
    );
};