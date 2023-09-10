import{sendFormToBackend} from './forms.js';
window.onload = function(){
    const form = document.getElementById("registration_form");
    sendFormToBackend(form, "/api/auth/register");
}