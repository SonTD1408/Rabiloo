$(document).ready(function () {
    let formDiscussionPost;
    let currentUserId = Number($("#current-user-id").val());

    let modalAddDiscussionPost = new Vue({
        el: "#modal_add_discussion_post",
        data: {
            listField: [],
            fieldId: "",
            title: "",
            target: "",

            isShowErrorField: false,
            isShowErrorContent: false,
            isShowErrorClosingDeadline: false,
            isShowErrorTimeClosingDeadline: false,

            isCanCreatePost: "",
        },
        methods: {
            checkCanCreatePost() {
                let self = this;
                $.ajax({
                    type: "POST",
                    url: "/api/user/checkCanCreatePost/" + currentUserId,
                    success: function (response) {
                        if (response.status.code === 1000) {
                            self.isCanCreatePost = response.data;
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
                    content: CKEDITOR.instances["textarea-content"].getData(),
                    target: self.target,
                    canCreatePost: self.isCanCreatePost,
                }

                if (self.isCanCreatePost) {
                    data.closingDeadline = $("#closing-deadline").val();
                }

                $.ajax({
                    type: "POST",
                    url: "/api/discussion_post/create",
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        window.loader.hide();
                        let code = response.status.code;
                        if (code === 1000) {
                            window.alert.show("success", self.isCanCreatePost ? "Tạo bài thảo luận thành công" : "Yêu cầu của bạn đã được gửi đi", 2000);
                            $('#modal_add_discussion_post').modal("hide");
                        } else if (code === 1604) {
                            window.alert.show("error", "Tiêu đề bài thảo luận đã tồn tại", 2000);
                        } else if (code === 1400) {
                            window.alert.show("error", "Vui lòng đăng nhập trước khi thực hiện", 2000);
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }
                })
            },

            validateForm() {
                this.validateContent();
                this.validateFiled();
                return !this.isShowErrorContent && !this.isShowErrorField;
            },
            validateContent() {
                this.isShowErrorContent = CKEDITOR.instances["textarea-content"].getData().trim() == "";
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
            resetPopup() {
                let self = this;
                this.id = "",
                this.fieldId = "";
                this.title = "";
                this.target = "";
                CKEDITOR.instances["textarea-content"].setData("");
                $("#closing-deadline").val(moment(new Date().addDays(30)).format('YYYY/MM/DD'));
                this.isShowErrorField = false;
                setTimeout(function () {
                    self.isShowErrorContent = false;
                }, 100);

                formDiscussionPost.resetForm();
            }
        },
        mounted() {
            let self = this;
            $("#closing-deadline").val(moment(new Date().addDays(30)).format('YYYY/MM/DD'));

            setTimeout(function () {
                self.listField = discussionPostVue.listField;
            }, 200);

            setTimeout(function () {
                self.checkCanCreatePost();
            }, 200);

            $(document).on("click", "#btn-create-discussion-post", function () {
                if (!currentUserId) {
                    window.alert.show("error", "Vui lòng đăng nhập trước khi thực hiện", 2000);
                    return;
                }
                $('#modal_add_discussion_post').modal("show");
            })

            $('#modal_add_discussion_post').on('hidden.bs.modal', function () {
                self.resetPopup();
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

            let contentDiscussionPost = CKEDITOR.replace('textarea-content', {
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

            contentDiscussionPost.on('change', function () {
                modalAddDiscussionPost.validateContent();
            });

        }
    })
})