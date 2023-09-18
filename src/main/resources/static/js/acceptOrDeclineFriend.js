window.addEventListener('load', () => {
    document.body.addEventListener('click', handleFriendship);

    function handleFriendship(e){
        doFriendshipAction(e, "acceptFriendBtn");
        doFriendshipAction(e, "declineFriendBtn");
        //changeButtons(e, "acceptFriendBtn", "btn-success", "btn-info", "You are friends", "declineFriendBtn");
        //changeButtons(e, "declineFriendBtn", "btn-danger", "btn-info", "You have declined their friendship request", "acceptFriendBtn");
    }

    function changeButtons(e, buttonId, classToRemove, classToAdd, innerText, buttonToRemoveId){
        if(e.target && e.target.id === buttonId){
            let buttonToChange = document.getElementById(buttonId);
            buttonToChange.classList.remove(classToRemove);
            buttonToChange.classList.add(classToAdd);
            buttonToChange.innerText = innerText;
            buttonToChange.removeAttribute("id");
            let buttonToRemove = document.getElementById(buttonToRemoveId);
            buttonToRemove.remove();
        }
    }
    function doFriendshipAction(e, buttonId){
        if(e.target && e.target.id === buttonId){
            let action = "accept";
            if(buttonId === "declineFriendBtn"){
                action = "decline"
            }

            fetch(`/api/friends/${action}`, setUpRequestBody())
                .then(response => {
                    if(response.ok){
                        return response.text().then(text => {
                            if(action === "decline"){
                                changeButtons(e, "declineFriendBtn", "btn-danger", "btn-info", "You have declined their friendship request", "acceptFriendBtn");
                            } else if(action === "accept"){
                                changeButtons(e, "acceptFriendBtn", "btn-success", "btn-info", "You are friends", "declineFriendBtn");
                            }
                            alert(text);
                        })
                    } else {
                        return response.text().then(text => {
                            alert(text);
                        })
                    }
                })
                .catch(err => {
                    console.error('Fetch error:', err);
                })

        }
    }
    function setUpRequestBody(){
        let requesterUsername = document.getElementById("username").innerText;
        let sendingData = {};
        sendingData["requesterUsername"] = requesterUsername;

        return {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(sendingData)
        };
    }
})