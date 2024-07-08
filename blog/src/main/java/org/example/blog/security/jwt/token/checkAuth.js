async function checkAuth() {
    try {
        const response = await fetch('/api/check-auth', {
            method: 'GET',
            credentials: 'include', // include cookies in the request
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (response.ok) {
            const username = await response.json();
            return username;
        } else {
            return null;
        }
    } catch (error) {
        console.error('Error checking auth:', error);
        return null;
    }
}

window.onload = onLoad;
