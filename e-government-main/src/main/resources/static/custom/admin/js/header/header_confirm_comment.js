let modalConfirmComment;

$(document).ready(function () {

    modalConfirmComment = new Vue({
        el: "#modal_confirm_comment",
        data: {
            statusComment: "WAITING",

            numberDaysDelete: 3,
            listReasonDenied: [],
            reasonDeniedId: "",

            post: {},
            comment: {},
            replyComment: {},

            isShowErrorContentReasonOther: false,
        },
        watch: {
            reasonDeniedId(value) {
                if (value == 0) {
                    setTimeout(function () {
                        $("#content-reason-other").focus();
                    }, 10);
                }
            }
        },
        methods: {
            getListReasonDenied() {
                let self = this;

                $.ajax({
                    type: "GET",
                    url: "/api/admin/reason_denied_comment/getList",
                    success: function (response) {
                        if (response.status.code === 1000) {
                            self.listReasonDenied = response.data;
                        }
                    }
                })
            },

            detailModalConfirmComment(dataNotificationResponse) {
                this.post = dataNotificationResponse.post;
                this.comment = dataNotificationResponse.comment;
                this.replyComment = dataNotificationResponse.replyComment;
            },

            confirm() {
                let self = this;
                if (self.statusComment === "WAITING") {
                    return;
                }

                let url;
                if (self.replyComment) {
                    url = "/api/admin/replyComment/confirmReplyComment/" + self.replyComment.id;
                } else {
                    url = "/api/admin/comment/confirmComment/" + self.comment.id;
                }

                let data = {
                    status: self.statusComment,
                }
                if (self.statusComment === 'DENIED') {
                    data.reasonDeniedId = self.reasonDeniedId;
                    data.numberDaysDelete = self.numberDaysDelete;
                    if (self.reasonDeniedId == 0) {
                        if (!self.validateContentReasonOther()) {
                            return;
                        }
                        data.contentReasonDeniedOther = $("#content-reason-other").text();
                    }
                }

                $.ajax({
                    type: "POST",
                    url: url,
                    data: JSON.stringify(data),
                    contentType: "application/json",
                    success: function (response) {
                        if (response.status.code === 1000) {
                            window.alert.show("success", "Xác nhận thành công", 2000);
                            $("#modal_confirm_comment").modal("hide");
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }
                })
            },

            validateContentReasonOther() {
                this.isShowErrorContentReasonOther = !$("#content-reason-other").text();
                return !this.isShowErrorContentReasonOther;
            },

            resetPopup() {
                this.statusComment = "WAITING";
                this.numberDaysDelete = 3;
                this.reasonDeniedId = "";
                this.isShowErrorContentReasonOther = false;
            },
        },

        mounted() {
            let self = this;

            self.getListReasonDenied();

            $(document).on("hidden.bs.modal", "#modal_confirm_comment", function () {
                self.resetPopup();
            })
        }
    })
})