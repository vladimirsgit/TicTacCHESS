window.onload = function () {
    const form = document.getElementById("login-form");

    form.addEventListener('submit', (e) => {
        e.preventDefault();

        const formData = new FormData(form);

        let formDataJson = {};

        formData.forEach((value, key) => {
            formDataJson[key] = value;
        })

        const requestOptions = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formDataJson)
        }

        fetch('/api/auth/login', requestOptions)
            .then((response) => {
                if(response.ok){
                    return response.text().then((message) => {
                        alert(message);
                        window.location.href  = '/';
                    });
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