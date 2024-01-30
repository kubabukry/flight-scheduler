import React from "react";

function useLocalState (initialValue, key) {
    const [value, setValue] = React.useState(() => {
        const storedValue = localStorage.getItem(key);

        return storedValue !== null ? JSON.parse(storedValue) : initialValue;
    });
    
    React.useEffect(() => {
        localStorage.setItem(key, JSON.stringify(value))
    }, [key, value]);

    return [value, setValue];
}

export {useLocalState}