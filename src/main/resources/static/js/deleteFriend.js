window.addEventListener('load', () => {
    let deleteFriendBtns = document.getElementsByClassName("delete-friend");

    for(let button of deleteFriendBtns){
        button.addEventListener('click', e => {
            e.preventDefault();

            fetch("/api/friends/delete", setUpRequestBody(button.id))
                .then(response => {
                    return response.text().then(text => {
                        alert(text);
                    })
                })
                .catch(err => {
                    console.error('Fetch error:', err)
                })

        })
    }

    function setUpRequestBody(btnId){
        let friendToDelete = "";

        for(let i = 15; i < btnId.length; i++){
            friendToDelete+=btnId.charAt(i);
        }
        const sendingData = {};
        sendingData["friend"] = friendToDelete;

        return {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(sendingData)
        }
    }
})