$(document).ready(function () {
    let formField;

    let fieldVue = new Vue({
        el: "#modal_add_field",
        data: {
            id: "",
            name: "",

            listSubField: [],

        },
        methods: {
            addSubField() {
                let subField = {
                    name: "",
                };
                this.listSubField.push(subField);
            },
            save() {
                let self = this;

                if (!$("#form-field").valid()) {
                    return;
                }

                let formData = new FormData();
                formData.append("name", this.name);

                if (this.id) {
                    formData.append("id", this.id);
                }

                self.listSubField.forEach(function(subField, i){
                    if(subField.id){
                        formData.append("listSubField["+i+"].id", subField.id);
                    }
                    formData.append("listSubField["+i+"].name", subField.name);
                })

                $.ajax({
                    type: "POST",
                    url: "/api/admin/field/save",
                    data: formData,
                    processData: false,
                    contentType: false,
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        if (response.status.code === 1000) {
                            window.loader.hide();
                            tableField.ajax.reload();
                            window.alert.show("success", "Lưu thành công", 2000);
                            $('#modal_add_field').modal("hide");
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
                    url: "/api/admin/field/detail/" + self.id,
                    success: function (response) {
                        if (response.status.code === 1000) {
                            let data = response.data;
                            self.name = data.name;
                            self.listSubField = data.listSubField;
                        }
                    }
                })
            },

            deleteField() {
                let self = this;
                $.ajax({
                    type: "POST",
                    url: "/api/admin/field/delete/" + self.id,
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        window.loader.hide();
                        if (response.status.code === 1000) {
                            tableField.ajax.reload();
                            window.alert.show("success", "Xóa lĩnh vực thành công", 2000);
                            $("#modal_delete").modal("hide");
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }
                })
            },

            resetPopup() {
                this.id = "";
                this.name = "";
                this.listSubField = [];
                this.addSubField();
            }
        },
        mounted() {
            let self = this;
            self.addSubField();

            $(document).on("click", ".delete-sub-field", function(){
                let indexDelete = $(this).attr('id').replace('delete-sub-field-', '');
                self.listSubField.splice(indexDelete, 1);
            })

            $('#modal_add_field').on('hidden.bs.modal', function () {
                self.resetPopup();
                formField.resetForm();
            })

            $('#modal_delete').on('hidden.bs.modal', function () {
                self.id = "";
            })

            formField = $("#form-field").validate({
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
                },
                messages: {
                    name: {
                        required: "Trường này là bắt buộc",
                        maxlength: "Tối đa 50 ký tự",
                    },
                }

            });


        }
    })

    $(document).on("click", ".detail-field", function () {
        fieldVue.id = Number($(this).attr("id").replace("btn_detail_", ""));
        fieldVue.detail();
    })

    $(document).on("click", ".delete-field", function () {
        fieldVue.id = Number($(this).attr("id").replace("btn_delete_", ""));
    })

    $(document).on("click", "#btn_submit_delete", function () {
        fieldVue.deleteField();
    })


})