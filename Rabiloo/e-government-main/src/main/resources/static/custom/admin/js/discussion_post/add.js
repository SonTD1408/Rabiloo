let discussionPostVue;
$(document).ready(function () {
    let formDiscussionPost;

    discussionPostVue = new Vue({
        el: "#modal_add_discussion_post",
        data: {
            id: "",
            listRole: [],
            listField: [],
            fieldId: "",
            title: "",
            target: "",
            conclude: "",
            isApproved: true,
            isCreatedFromUser: false,
            nameCreator: null,
            nameApprover: null,
            nameRoleCreator: "",
            nameRoleApprover: "",

            isShowErrorContent: false,
            isShowErrorField: false,
            isShowErrorClosingDeadline: false,
            isShowErrorTimeClosingDeadline: false,
            isShowErrorRoleComment: false,
        },
        methods: {
            loadListField() {
                let self = this;
                $.ajax({
                    type: "GET",
                    url: "/api/admin/field/getList",
                    success: function (response) {
                        if (response.status.code === 1000) {
                            self.listField = response.data;
                        }
                    }
                })
            },
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
                let self = this;

                if (!$("#form-discussion-post").valid()) {
                    return;
                }

                if (!this.validateForm()) {
                    return;
                }

                let data = {
                    fieldId: self.fieldId,
                    title: self.title,
                    content: CKEDITOR.instances["textarea-content-post"].getData(),
                    target: self.target,
                    closingDeadline: $("#closing-deadline").val(),
                    isApproved: this.isApproved,
                }

                if (!this.isCreatedFromUser) {
                    data.listRoleId = $("#select_role").val().toString();
                }

                if (this.id) {
                    data.id = this.id;
                }

                $.ajax({
                    type: "POST",
                    url: "/api/admin/discussion_post/save",
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        window.loader.hide();
                        if (response.status.code === 1000) {
                            if (window.location.pathname.split("/")[2] === "discussion_post") {
                                tableDiscussionPost.ajax.reload();
                            }
                            window.alert.show("success", "Lưu bài viết thành công", 2000);
                            $('#modal_add_discussion_post').modal("hide");
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }
                })

            },
            detail() {
                let self = this;
                $.ajax({
                    type: "GET",
                    url: "/api/admin/discussion_post/detail/" + self.id,
                    success: function (response) {
                        if (response.status.code === 1000) {
                            let data = response.data;
                            self.fieldId = data.fieldId;
                            self.title = data.title;
                            self.target = data.target;
                            self.nameCreator = data.nameCreator;
                            self.nameApprover = data.nameApprover;
                            self.nameRoleCreator = data.nameRoleCreator;
                            self.nameRoleApprover = data.nameRoleApprover;
                            if (data.concludeParse) {
                                self.conclude = data.concludeParse;
                            }
                            self.isApproved = data.approved;
                            self.isCreatedFromUser = data.createdFromUser;
                            CKEDITOR.instances["textarea-content-post"].setData(data.content);
                            if (data.closingDeadline) {
                                $("#closing-deadline").val(moment(data.closingDeadline).format('YYYY/MM/DD'));
                            }
                            let listRoleId = data.listRole.map(function (role) {
                                return role.id;
                            });
                            $("#select_role").val(listRoleId).trigger("change");
                        }
                    }
                })
            },
            deleteDiscussionPost() {
                let self = this;
                $.ajax({
                    type: "POST",
                    url: "/api/admin/discussion_post/delete/" + self.id,
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        window.loader.hide();
                        if (response.status.code === 1000) {
                            tableDiscussionPost.ajax.reload();
                            window.alert.show("success", "Xóa bài viết thành công", 2000);
                            $("#modal_delete").modal("hide");
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }
                })
            },
            validateForm() {
                this.validateContent();
                this.validateFiled();
                this.validateClosingDeadline();
                this.validateTimeClosingDeadline();
                this.validateRole();
                return !this.isShowErrorContent && !this.isShowErrorField && !this.isShowErrorClosingDeadline && !this.isShowErrorTimeClosingDeadline
                    && !this.isShowErrorRoleComment;
            },
            validateContent() {
                this.isShowErrorContent = CKEDITOR.instances["textarea-content-post"].getData().trim() == "";
            },
            validateFiled() {
                this.isShowErrorField = !this.fieldId;
            },
            validateClosingDeadline() {
                this.isShowErrorClosingDeadline = $("#closing-deadline").val() == "";
            },
            validateTimeClosingDeadline() {
                this.isShowErrorTimeClosingDeadline = moment($("#closing-deadline").val()) < moment(new Date());
            },
            validateRole() {
                if (this.isCreatedFromUser) {
                    this.isShowErrorRoleComment = false;
                    return;
                }
                this.isShowErrorRoleComment = $("#select_role").val().toString() == "";
            },
            resetPopup() {
                let self = this;
                this.id = "";
                this.fieldId = "";
                this.title = "";
                this.target = "";
                this.conclude = "";
                this.isApproved = true;
                this.isCreatedFromUser = false;
                this.nameCreator = null;
                this.nameApprover = null;
                this.nameRoleCreator = "";
                this.nameRoleApprover = "";
                CKEDITOR.instances["textarea-content-post"].setData("");
                $("#closing-deadline").val(moment(new Date().addDays(30)).format('YYYY/MM/DD'));
                $("#select_role").val("").trigger("change");

                this.isShowErrorField = false;
                this.isShowErrorClosingDeadline = false;
                this.isShowErrorTimeClosingDeadline = false;
                this.isShowErrorRoleComment = false;
                setTimeout(function () {
                    self.isShowErrorContent = false;
                }, 100);

                formDiscussionPost.resetForm();
            }
        },
        mounted() {
            let self = this;
            $("#closing-deadline").val(moment(new Date().addDays(30)).format('YYYY/MM/DD'));
            self.loadListField();
            self.loadListRole();

            $("#select_role").select2({
                placeholder: '',
            });

            configOneDateNotTime('closing-deadline');

            $('#modal_add_discussion_post').on('hidden.bs.modal', function () {
                self.resetPopup();
            })

            $('#modal_delete').on('hidden.bs.modal', function () {
                self.id = "";
            })

            formDiscussionPost = $("#form-discussion-post").validate({
                errorElement: "p",
                errorClass: "error-message",
                errorPlacement: function (error, element) {
                    error.insertAfter(element);
                },
                ignore: [],
                rules: {
                    title: {
                        required: true,
                        maxlength: 200,
                    },
                    target: {
                        required: true,
                        maxlength: 200,
                    },
                },
                messages: {
                    title: {
                        required: "Trường này là bắt buộc",
                        maxlength: "Tối đa 200 ký tự",
                    },
                    target: {
                        required: "Trường này là bắt buộc",
                        maxlength: "Tối đa 200 ký tự",
                    },
                }
            });
        }
    })

    $(document).on("click", ".detail-discussion-post", function () {
        discussionPostVue.id = Number($(this).attr("id").replace("btn_detail_", ""));
        discussionPostVue.detail();
    })

    $(document).on("click", ".delete-discussion-post", function () {
        discussionPostVue.id = Number($(this).attr("id").replace("btn_delete_", ""));
    })

    $(document).on("click", "#btn_submit_delete", function () {
        discussionPostVue.deleteDiscussionPost();
    })

    let contentDiscussionPost = CKEDITOR.replace('textarea-content-post', {
        language: 'vi',
        height: 200,
        removePlugins: 'elementspath'
    });
    CKEDITOR.config.toolbar = [
        ['Styles', 'Format'],
        ['Bold', 'Italic', 'Underline', 'StrikeThrough', '-', 'Undo', 'Redo', '-', 'Cut', 'Copy', 'Paste', 'Find', 'Replace', '-', 'Outdent', 'Indent', '-', 'Print'],
        ['NumberedList', 'BulletedList', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'],
        ['Image', 'Table', '-', 'Link', 'Flash', 'Smiley', 'TextColor', 'BGColor', 'Source']
    ];

    contentDiscussionPost.on('change', function() {
        discussionPostVue.validateContent();
    });
})