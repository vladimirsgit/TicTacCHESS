window.onload = function () {
    const logoutButton = document.getElementById("logout-button");
    if(logoutButton)
        logoutButton.addEventListener('click', (e) => {

        e.preventDefault();

        const requestOptions = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        }

        fetch("/api/auth/logout", requestOptions)
            .then((response) => {
                if(response.ok){
                    return response.text().then((message) => {
                        alert(message);
                        window.location.href = '/';
                    })
                } else {
                    return response.text().then((errorMessage) => {
                        alert(errorMessage);
                })
            }
            }).catch((err) => {
            console.error('Fetch error:', err);
        })
    })

}