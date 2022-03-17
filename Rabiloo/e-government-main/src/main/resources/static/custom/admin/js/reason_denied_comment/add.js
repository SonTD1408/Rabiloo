$(document).ready(function () {
    let formReasonDenied;

    let reasonDeniedVue = new Vue({
        el: "#modal_add_reason_denied",
        data: {
            id: "",
            content: "",
        },
        methods: {
            save() {
                let self = this;

                if (!$("#form-reason-denied").valid()) {
                    return;
                }

                let data = {
                    content: this.content,
                }

                if (this.id) {
                    data.id = this.id;
                }

                $.ajax({
                    type: "POST",
                    url: "/api/admin/reason_denied_comment/save",
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        if (response.status.code === 1000) {
                            window.loader.hide();
                            tableReasonDenied.ajax.reload();
                            window.alert.show("success", "Lưu thành công", 2000);
                            $('#modal_add_reason_denied').modal("hide");
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
                    url: "/api/admin/reason_denied_comment/detail/" + self.id,
                    success: function (response) {
                        if (response.status.code === 1000) {
                            let data = response.data;
                            self.content = data.content;
                        }
                    }
                })
            },

            deleteReasonDenied() {
                let self = this;
                $.ajax({
                    type: "POST",
                    url: "/api/admin/reason_denied_comment/delete/" + self.id,
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        window.loader.hide();
                        if (response.status.code === 1000) {
                            tableReasonDenied.ajax.reload();
                            window.alert.show("success", "Xóa lý do thành công", 2000);
                            $("#modal_delete").modal("hide");
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }
                })
            },

            resetPopup() {
                this.id = "";
                this.content = "";
            }
        },
        mounted() {
            let self = this;

            $('#modal_add_reason_denied').on('hidden.bs.modal', function () {
                self.resetPopup();
                formReasonDenied.resetForm();
            })

            $('#modal_delete').on('hidden.bs.modal', function () {
                self.id = "";
            })

            formReasonDenied = $("#form-reason-denied").validate({
                errorElement: "p",
                errorClass: "error-message",
                errorPlacement: function (error, element) {
                    error.insertAfter(element);
                },
                ignore: [],
                rules: {
                    content: {
                        required: true,
                        maxlength: 200,
                    },
                },
                messages: {
                    content: {
                        required: "Trường này là bắt buộc",
                        maxlength: "Tối đa 200 ký tự",
                    },
                }

            });


        }
    })

    $(document).on("click", ".detail-reason_denied", function () {
        reasonDeniedVue.id = Number($(this).attr("id").replace("btn_detail_", ""));
        reasonDeniedVue.detail();
    })

    $(document).on("click", ".delete-reason_denied", function () {
        reasonDeniedVue.id = Number($(this).attr("id").replace("btn_delete_", ""));
    })

    $(document).on("click", "#btn_submit_delete", function () {
        reasonDeniedVue.deleteReasonDenied();
    })


})