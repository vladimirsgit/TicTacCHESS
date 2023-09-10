import{sendFormToBackend} from './forms.js';

window.addEventListener('load', function () {
    const form = document.getElementById("login-form");
    sendFormToBackend(form, "/api/auth/login", "/");

});