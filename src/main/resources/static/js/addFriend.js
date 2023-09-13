window.addEventListener('load', () => {
    document.body.addEventListener('click', handleClick);
    document.body.addEventListener('mouseover', handleMouseOver);
    document.body.addEventListener('mouseout', handleMouseOut);

    function handleClick(e){
       changeButtonState(e, "addFriendBtn", "btn-primary", "btn-secondary", "Friend request sent", "requestSentBtn");
       changeButtonState(e, "cancelRequestBtn", "btn-warning", "btn-primary", "Add friend", "addFriendBtn");
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
})