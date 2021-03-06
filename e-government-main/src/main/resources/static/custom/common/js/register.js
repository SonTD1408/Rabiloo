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
                            window.alert.show("success", "????ng k?? th??nh c??ng", 2000);
                        } else if (code === 1600) {
                            window.alert.show("error", "Email ???? t???n t???i", 2000);
                        } else {
                            window.alert.show("error", "???? c?? l???i x???y ra, vui l??ng th??? l???i sau", 2000);
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
                        required: "Tr?????ng n??y l?? b???t bu???c",
                        maxlength: "T???i ??a 50 k?? t???",
                    },
                    email: {
                        required: "Tr?????ng n??y l?? b???t bu???c",
                        maxlength: "T???i ??a 100 k?? t???",
                    },
                    phone: {
                        required: "Tr?????ng n??y l?? b???t bu???c",
                    },
                    password: {
                        required: "Tr?????ng n??y l?? b???t bu???c",
                        minlength: "M???t kh???u t??? 8 ?????n 32 k?? t???",
                        maxlength: "M???t kh???u t??? 8 ?????n 32 k?? t???",
                    },
                    passwordConfirm: {
                        required: "Tr?????ng n??y l?? b???t bu???c",
                        equalTo: "M???t kh???u v?? m???t kh???u x??c nh???n kh??ng kh???p",
                    },
                    address: {
                        required: "Tr?????ng n??y l?? b???t bu???c",
                        maxlength: "T???i ??a 200 k?? t???",
                    },
                }
            });

            $.validator.addMethod("validateFormatPhone", function (value, element) {
                let phone_regex = /((09|03|07|08|05)+([0-9]{8})\b)/g;
                return phone_regex.test(value);
            }, "?????nh d???ng kh??ng ch??nh x??c");

            $.validator.addMethod("validateFormatEmail", function (value, element) {
                let email_regex = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
                return email_regex.test(value);
            }, "?????nh d???ng kh??ng ch??nh x??c");
        }

    });
})