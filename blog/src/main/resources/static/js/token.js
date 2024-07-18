// static/js/token.js

function refreshAccessToken() {
    console.log('Refreshing Access Token');
    return new Promise((resolve, reject) => {
        fetch('/api/users/refreshToken', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({}) // Refresh Token은 보내지 않음
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Failed to refresh Access Token');
                }
            })
            .then(data => {
                if (data.accessToken) {
                    console.log('Access Token refreshed successfully');
                    localStorage.setItem('lastRefreshTime', new Date().getTime());
                    // 갱신된 토큰을 쿠키에 저장
                    document.cookie = `accessToken=${data.accessToken}; path=/; HttpOnly`;
                    resolve(true);
                } else {
                    console.log('Failed to refresh Access Token2');
                    resolve(false);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                resolve(false);
            });
    });
}

function initTokenRefresh() {
    console.log('initTokenRefresh');
    const lastRefreshTime = localStorage.getItem('lastRefreshTime');
    const currentTime = new Date().getTime();
    const timeElapsed = currentTime - lastRefreshTime;

    // 마지막 갱신 후 50분이 지났다면 즉시 갱신
    if (timeElapsed >= 50 * 60 * 1000) {
        refreshAccessToken();
    }

    // 주기적으로 Access Token 갱신
    setInterval(() => {
        const lastRefreshTime = localStorage.getItem('lastRefreshTime');
        const currentTime = new Date().getTime();
        const timeElapsed = currentTime - lastRefreshTime;

        // 50분이 지난 경우에만 갱신
        if (timeElapsed >= 50 * 60 * 1000) {
            refreshAccessToken();
        }
    }, 1 * 60 * 1000); // 매 1분마다 체크
}

function handleFormSubmit(event) {
    event.preventDefault(); // 폼 제출 기본 동작 방지
    refreshAccessToken().then(success => {
        if (success) {
            event.target.submit(); // 액세스 토큰이 성공적으로 갱신된 경우 폼 제출
        } else {
            console.error('Failed to refresh access token before form submission');
            alert('Failed to refresh access token. Please try again.');
        }
    });
}

console.log('Script loaded');
// 페이지 로드 시 초기화
window.onload = function() {
    initTokenRefresh();

    // 폼 제출 이벤트에 핸들러 추가
    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', handleFormSubmit);
    }
};
