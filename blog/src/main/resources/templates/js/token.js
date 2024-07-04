<script>

    function refreshAccessToken() {

        console.log('refresh Access Token');

        // 서버로 refresh Token 요청
        fetch('/refreshToken', {
        method: 'POST',
        headers: {'Content-Type': 'application/json',},
        body: JSON.stringify() // 요청 본문을 빈 JSON 객체
        })
        // Handle the response
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Failed to refresh Access Token');
            }
        })
        // Handle the JSON response
        .then(data => {
            if (data.accessToken) { // accessToken이 포함된 응답을 받으면
                console.log('Access Token refreshed successdully');
                localStorage.setItem('lastRefreshTime', Date.now());
            } else {
                console.log('Failed to refresh Access Token2');
            }
        })
    // Handle the error
        .catch(error => {
            console.error('Error:', error);
        });
}

    function initTokenRefresh() {

        console.log('init Token Refresh');

        const lastRefreshTime = localStorage.getItem('last Refresh Time');
        const currentTime = new Date().getTime();
        const timeElapsed = currentTime - lastRefreshTime;
        const ACCESS_TOKEN_EXPIRE_COUNT = 5 * 60 * 1000; // 5분

        // 마지막 갱신 후 5분이 지났다면 즉시 갱신
        if (timeElapsed > ACCESS_TOKEN_EXPIRE_COUNT) {
            refreshAccessToken();
        }

        // 주기적으로 AccessToken 갱신
        setInterval(() => {
                const lastRefreshTime = localStorage.getItem('lastRefreshTime');
                const currentTime = new Date().getTime();
                const timeElapsed = currentTime - lastRefreshTime;

                // 5분이 지난 경우 갱신
                if (timeElapsed > ACCESS_TOKEN_EXPIRE_COUNT) {
                    refreshAccessToken();
                }
            }, 1 * 60 * 1000); // 1분마다 체크

}

    console.log('Script loaded');
    // 페이지 로그 시 초기화
    window.onload = initTokenRefresh;
</script>