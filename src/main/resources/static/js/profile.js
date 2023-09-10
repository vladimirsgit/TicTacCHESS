// window.addEventListener('load', function () {
//     const profileButton = document.getElementById("profile-button");
//
//     if(profileButton)
//         profileButton.addEventListener('click', (e) => {
//             e.preventDefault();
//
//             const requestOptions = {
//                 method: 'GET',
//                 headers: {
//                     'Content-Type': 'application/json'
//                 }
//             }
//
//             fetch("/profile", requestOptions)
//                 .then((response) => {
//                     if(response.ok){
//                         return response.text().then((message) => {
//                             window.location.href = `/profile/${message}`;
//                         })
//                     } else {
//                         return response.text().then((errorMessage) => {
//                             alert(errorMessage);
//                         })
//                     }
//                 }).catch((err) => {
//                 console.error('Fetch error:', err);
//             })
//         })
// });