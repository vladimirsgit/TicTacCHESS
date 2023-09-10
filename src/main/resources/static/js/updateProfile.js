import{sendFormToBackend} from './forms.js';
window.addEventListener('load', () => {
    const form = document.getElementById("update-profile-form");
    sendFormToBackend(form, "/user/updateProfile", "/profile");

})