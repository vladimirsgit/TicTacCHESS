window.addEventListener('load', () => {
    document.body.addEventListener('click', handleClick);
    document.body.addEventListener('mouseover', handleMouseOver);
    document.body.addEventListener('mouseout', handleMouseOut);

    function handleClick(e){
       doFriendshipAction(e, "addFriendBtn");
       doFriendshipAction(e, "cancelRequestBtn");
    }
    function handleMouseOver(e){
        changeButtonState(e, "requestSentBtn", "btn-secondary", "btn-warning", "Cancel friend request", "cancelRequestBtn");
    }
    function handleMouseOut(e){
        changeButtonState(e, "cancelRequestBtn", "btn-warning", "btn-secondary", "Friend request sent", "requestSentBtn");
    }

    function changeButtonState(e, buttonId, classToRemove, classToAdd, innerText, newButtonId){
        if(e.target && e.target.id === buttonId){
            let button = document.getElementById(buttonId);
            button.classList.remove(classToRemove);
            button.classList.add(classToAdd);
            button.innerText = innerText;
            button.id = newButtonId;
        }
    }
    function doFriendshipAction(e, buttonId){
        if(e.target && e.target.id === buttonId){
            let action = "addFriend";
            if(buttonId === "cancelRequestBtn"){
                action = "cancelRequest"
            }
            fetch(`/api/friends/${action}`, setUpRequestBody())
                .then(response => {
                    if(response.ok){
                        return response.text().then(text => {
                            if(action === "addFriend"){
                                changeButtonState(e, "addFriendBtn", "btn-primary", "btn-secondary", "Friend request sent", "requestSentBtn");
                            } else if(action === "cancelRequest"){
                                changeButtonState(e, "cancelRequestBtn", "btn-warning", "btn-primary", "Add friend", "addFriendBtn");
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
        let recipientUsername = document.getElementById("username").innerText;
        let sendingData = {};
        sendingData["recipientUsername"] = recipientUsername;

        return {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(sendingData)
        };
    }
})