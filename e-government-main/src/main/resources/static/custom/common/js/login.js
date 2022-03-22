$(document).ready(function () {

    let loginVue = new Vue({
        el: '#loginbox',
        data: {
            username: "",
            password: "",
        },
        methods: {
            login() {
                let formLogin = {
                    username: this.username,
                    password: this.password,
                }
                $.ajax({
                    type: "POST",
                    url: "/login",
                    data: formLogin,
                    success: function (response) {
                        let urlRedirect = JSON.parse(response);
                        window.location.href = urlRedirect;
                    },
                    error: function () {
                        window.alert.show("error", "Email hoặc mật khẩu không đúng!", 2000);
                    }
                })
            },
            resetFormLogin() {
                this.username = "";
                this.password = "";
            }
        },
        mounted() {
            let self = this;
            self.resetFormLogin();
            $(document).on('keyup', function (e) {
                if (e.keyCode === 13) {
                    if (self.username !== "" && self.password != "") {
                        self.login();
                    }
                }
            })
        }
    })



});

