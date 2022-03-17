const closeBtn = document.querySelector(".modal-close");
const loginBtn  = document.querySelector(".header-login-button");
const modalLogin = document.querySelector(".login-form");
const loginFormModal = document.querySelector(".login-form-modal")

function openFormLogin(){
    modalLogin.classList.add("login-form-open")
}

function closeFormLogin(){
    modalLogin.classList.remove("login-form-open")
}

loginBtn.addEventListener('click', openFormLogin)
closeBtn.addEventListener('click', closeFormLogin)
modalLogin.addEventListener('click', closeFormLogin)
loginFormModal.addEventListener('click', function(even){
    even.stopPropagation()
})