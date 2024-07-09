async function checkAuth() {
    try {
        const response = await fetch('/api/users/check-auth', {
            method: 'GET',
            credentials: 'include', // include cookies in the request
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (response.ok) {
            const data = await response.json();
            return data.username;
        } else {
            return null;
        }
    } catch (error) {
        console.error('Error checking auth:', error);
        return null;
    }
}
