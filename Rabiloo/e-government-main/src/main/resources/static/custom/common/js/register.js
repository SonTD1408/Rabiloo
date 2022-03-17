$(document).ready(function () {
    let validateFormSignUp;

    let signUpVue = new Vue({
        el: '#signupbox',
        data: {
            name: "",
            password: "",
            passwordConfirm: "",
            email: "",
            phone: "",
            gender: 1,
            address: "",

            isShowErrorBirthday: false,
            isShowErrorTimeBirthday: false,
        },
        methods: {
            validateForm() {
                this.validateBirthday();
                this.validateTimeBirthday();
                return !this.isShowErrorBirthday && !this.isShowErrorTimeBirthday;
            },
            validateBirthday() {
                this.isShowErrorBirthday = $("#birthday").val() == "";
            },
            validateTimeBirthday() {
                this.isShowErrorTimeBirthday = moment($("#birthday").val()) > moment(new Date());
            },
            prepareSignUp() {
                let data = new FormData();
                data.append("name", this.name);
                data.append("email", this.email);
                data.append("password", this.password);
                data.append("birthday", $("#birthday").val());
                data.append("phone", this.phone);
                data.append("address", this.address);
                data.append("gender", this.gender);
                data.append("roleId", 5);

                data.append("managePost", false);
                data.append("manageCommentPost", false);
                data.append("manageConclusionPost", false);
                data.append("manageField", false);
                data.append("manageReasonDeniedComment", false);
                data.append("manageNews", false);

                return data;
            },

            signUp() {
                let self = this;
                if (!$('#signupform').valid()) {
                    return;
                }

                if (!this.validateForm()) {
                    return;
                }

                $.ajax({
                    type: "POST",
                    url: "/api/admin/user/save",
                    processData: false,
                    contentType: false,
                    data: self.prepareSignUp(),
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        window.loader.hide();
                        let code = response.status.code;
                        if (code === 1000) {
                            window.alert.show("success", "Đăng ký thành công", 2000);
                        } else if (code === 1600) {
                            window.alert.show("error", "Email đã tồn tại", 2000);
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }
                })
            },
            resetPopup() {
                this.name = "";
                this.password = "";
                this.passwordConfirm = "";
                this.email = "";
                this.gender = 1;
                this.address = "";
                this.phone = "";
                this.isShowErrorBirthday = false;
                this.isShowErrorTimeBirthday = false;
                $("#birthday").val("");

                this.isManagePost = false;
                this.isManageCommentPost = false;
                this.isManageConclusionPost = false;
                this.isManageField = false;
                this.isManageReasonDeniedComment = false;
                this.isManageNews = false;
            },
        },
        mounted() {
            let self = this;
            self.resetPopup();

            configOneDateNotTime('birthday');
            $("#birthday").val("");

            validateFormSignUp = $('#signupform').validate({
                errorElement: "p",
                errorClass: "error-message",
                errorPlacement: function (error, element) {
                    error.insertAfter(element);
                },
                ignore: [],
                rules: {
                    name: {
                        required: true,
                        maxlength: 50,
                    },
                    email: {
                        required: true,
                        maxlength: 100,
                        validateFormatEmail: true,
                    },
                    phone: {
                        required: true,
                        validateFormatPhone: true,
                    },
                    password: {
                        required: true,
                        minlength: 8,
                        maxlength: 32,
                    },
                    passwordConfirm: {
                        required: true,
                        equalTo: "#password"
                    },
                    address: {
                        required: true,
                        maxlength: 200,
                    },
                },
                messages: {
                    name: {
                        required: "Trường này là bắt buộc",
                        maxlength: "Tối đa 50 ký tự",
                    },
                    email: {
                        required: "Trường này là bắt buộc",
                        maxlength: "Tối đa 100 ký tự",
                    },
                    phone: {
                        required: "Trường này là bắt buộc",
                    },
                    password: {
                        required: "Trường này là bắt buộc",
                        minlength: "Mật khẩu từ 8 đến 32 ký tự",
                        maxlength: "Mật khẩu từ 8 đến 32 ký tự",
                    },
                    passwordConfirm: {
                        required: "Trường này là bắt buộc",
                        equalTo: "Mật khẩu và mật khẩu xác nhận không khớp",
                    },
                    address: {
                        required: "Trường này là bắt buộc",
                        maxlength: "Tối đa 200 ký từ",
                    },
                }
            });

            $.validator.addMethod("validateFormatPhone", function (value, element) {
                let phone_regex = /((09|03|07|08|05)+([0-9]{8})\b)/g;
                return phone_regex.test(value);
            }, "Định dạng không chính xác");

            $.validator.addMethod("validateFormatEmail", function (value, element) {
                let email_regex = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
                return email_regex.test(value);
            }, "Định dạng không chính xác");
        }

    });
})