window.addEventListener('load', () => {
    document.body.addEventListener('click', handleFriendship);

    function handleFriendship(e){
        changeButtons(e, "acceptFriendBtn", "btn-success", "btn-info", "You are friends", "declineFriendBtn");
        changeButtons(e, "declineFriendBtn", "btn-danger", "btn-info", "You have declined their friendship request", "acceptFriendBtn");
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
})