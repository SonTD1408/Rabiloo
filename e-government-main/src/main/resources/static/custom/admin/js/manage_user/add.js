$(document).ready(function () {
    let formUser;

    let userVue = new Vue({
        el: "#modal_add_user",
        data: {
            id: "",
            roleId: "",
            listRole: [],
            name: "",
            email: "",
            gender: 1,
            phone: "",
            address: "",
            password: "",
            passwordConfirm: "",

            isManagePost: false,
            isManageCommentPost: false,
            isManageConclusionPost: false,
            isManageField: false,
            isManageReasonDeniedComment: false,
            isManageNews: false,

            isShowErrorRole: false,
            isShowErrorBirthday: false,
            isShowErrorTimeBirthday: false,

        },
        methods: {
            loadListRole() {
                let self = this;
                $.ajax({
                    type: "GET",
                    url: "/api/admin/role/getList",
                    success: function (response) {
                        if (response.status.code === 1000) {
                            self.listRole = response.data;
                        }
                    }
                })
            },
            save() {
                if (!$("#form-user").valid()) {
                    return;
                }

                if (!this.validateForm()) {
                    return;
                }

                let data = new FormData();
                data.append("name", this.name);
                data.append("email", this.email);
                data.append("password", this.password);
                data.append("birthday", $("#birthday").val());
                data.append("phone", this.phone);
                data.append("address", this.address);
                data.append("gender", this.gender);
                data.append("roleId", this.roleId);
                if (this.$refs['image-avatar'].getImageSelected()) {
                    data.append("avatar", this.$refs['image-avatar'].getImageSelected());
                }

                // roleId = 5: quy???n citizen
                if (this.roleId == 5) {
                    data.append("managePost", false);
                    data.append("manageCommentPost", false);
                    data.append("manageConclusionPost", false);
                    data.append("manageField", false);
                    data.append("manageReasonDeniedComment", false);
                    data.append("manageNews", false);
                } else {
                    data.append("managePost", this.isManagePost);
                    data.append("manageCommentPost", this.isManageCommentPost);
                    data.append("manageConclusionPost", this.isManageConclusionPost);
                    data.append("manageField", this.isManageField);
                    data.append("manageReasonDeniedComment", this.isManageReasonDeniedComment);
                    data.append("manageNews", this.isManageNews);
                }

                if (this.id) {
                    data.append("id", this.id);
                }

                $.ajax({
                    type: "POST",
                    url: "/api/admin/user/save",
                    processData: false,
                    contentType: false,
                    data: data,
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        window.loader.hide();
                        let code = response.status.code;
                        if (code === 1000) {
                            tableUser.ajax.reload();
                            window.alert.show("success", "L??u th??nh c??ng", 2000);
                            $('#modal_add_user').modal("hide");
                        } else if (code === 1600) {
                            window.alert.show("error", "Email ???? t???n t???i", 2000);
                        } else {
                            window.alert.show("error", "???? c?? l???i x???y ra, vui l??ng th??? l???i sau", 2000);
                        }
                    }
                })

            },
            detail() {
                let self = this;
                $.ajax({
                    type: "GET",
                    url: "/api/admin/user/detail/" + self.id,
                    success: function (response) {
                        if (response.status.code === 1000) {
                            let data = response.data;
                            let userEntity = data.userEntity;
                            let actionableEntity = data.actionableEntity;

                            self.name = userEntity.name;
                            self.email = userEntity.email;
                            self.phone = userEntity.phone;
                            self.gender = userEntity.gender;
                            self.address = userEntity.address;
                            if (userEntity.urlAvatar) {
                                self.$refs['image-avatar'].hasImage = true;
                                $(self.$refs['image-avatar'].$el).find('img').attr("src", userEntity.urlAvatar);
                            }

                            if (userEntity.birthday) {
                                $("#birthday").val(moment(userEntity.birthday).format('YYYY/MM/DD'));
                            }
                            if (userEntity.roles.length > 0) {
                                self.roleId = userEntity.roles[0].id;
                            }

                            if (actionableEntity) {
                                self.isManagePost = actionableEntity.managePost;
                                self.isManageCommentPost = actionableEntity.manageCommentPost;
                                self.isManageConclusionPost = actionableEntity.manageConclusionPost;
                                self.isManageField = actionableEntity.manageField;
                                self.isManageReasonDeniedComment = actionableEntity.manageReasonDeniedComment;
                                self.isManageNews = actionableEntity.manageNews;
                            }

                            $("#select_role").val();
                        }
                    }
                })
            },

            validateForm() {
                this.validateRole();
                this.validateBirthday();
                this.validateTimeBirthday();
                return !this.isShowErrorBirthday && !this.isShowErrorTimeBirthday && !this.isShowErrorRole;
            },

            validateBirthday() {
                this.isShowErrorBirthday = $("#birthday").val() == "";
            },
            validateTimeBirthday() {
                this.isShowErrorTimeBirthday = moment($("#birthday").val()) > moment(new Date());
            },

            validateRole() {
                this.isShowErrorRole = !this.roleId;
            },

            resetPopup() {
                this.id = "";
                this.name = "";
                this.email = "";
                this.password = "";
                this.passwordConfirm = "";
                this.phone = "";
                this.address = "";
                this.roleId = "";
                $("#birthday").val("");
                this.$refs['image-avatar'].hasImage = false;

                this.isManagePost = false;
                this.isManageCommentPost = false;
                this.isManageConclusionPost = false;
                this.isManageField = false;
                this.isManageReasonDeniedComment = false;
                this.isManageNews = false;

                this.isShowErrorBirthday = false;
                this.isShowErrorTimeBirthday = false;
                this.isShowErrorRole = false;
            }
        },
        mounted() {
            let self = this;
            self.loadListRole();

            configOneDateNotTime('birthday');
            $("#birthday").val("");

            $('#modal_add_user').on('hidden.bs.modal', function () {
                self.resetPopup();
                formUser.resetForm();
            })

            formUser = $("#form-user").validate({
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

            $.validator.addMethod("validateFormatEmail", function (value) {
                let regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
                return regex.test(value);
            }, "?????nh d???ng kh??ng ch??nh x??c");

            $.validator.addMethod("validateFormatPhone", function (value) {
                let regex = /^\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$/;
                return regex.test(value);
            }, "?????nh d???ng kh??ng ch??nh x??c");

        }
    })

    $(document).on("click", ".detail-user", function () {
        userVue.id = Number($(this).attr("id").replace("btn_detail_", ""));
        userVue.detail();
    })

    $(document).on("click", ".change-active-user", function () {
        if ($(this).hasClass("disabled")) {
            return;
        }
        $.ajax({
            type: "POST",
            url: "/api/admin/user/changeStatus/" + Number($(this).attr('id').replace('active-user-', '')),
            beforeSend: function () {
                window.loader.show();
            },
            success: function (response) {
                window.loader.hide();
                setTimeout(function () {
                    tableUser.ajax.reload();
                }, 2000);
                if (response.status.code === 1000) {
                    window.alert.show('success', "Thay ?????i tr???ng th??i ng?????i d??ng th??nh c??ng", 2000);
                } else {
                    window.alert.show("error", "???? c?? l???i x???y ra, vui l??ng th??? l???i sau", 2000);
                }
            }
        })
    });

})