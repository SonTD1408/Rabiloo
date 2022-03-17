$(document).ready(function () {
    let formNews;

    let newsVue = new Vue({
        el: "#modal_add_news",
        data: {
            id: "",
            listField: [],
            fieldId: "",
            listSubField: [],
            subFieldId: "",
            title: "",

            isShowErrorField: false,
            isShowErrorSubField: false,
            isShowErrorContent: false,
            isShowErrorImage: false,
        },
        watch: {
            fieldId: function (value) {
                if (!!value) {
                    this.loadListSubFiled();
                }
            }
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
            loadListSubFiled() {
                let self = this;
                $.ajax({
                    type: "GET",
                    url: "/api/admin/sub_field/getList/" + self.fieldId,
                    success: function (response) {
                        if (response.status.code === 1000) {
                            self.listSubField = response.data;
                        }
                    }
                })
            },
            save() {
                let self = this;
                if (!$("#form-news").valid()) {
                    return;
                }

                if (!this.validateForm()) {
                    return;
                }

                let data = new FormData();
                data.append("subFieldId", this.subFieldId);
                data.append("title", this.title);
                data.append("content", CKEDITOR.instances["textarea-content-news"].getData());


                if (self.$refs['image-intro'].getImageSelected()) {
                    data.append("image", self.$refs['image-intro'].getImageSelected());
                }

                if (this.id) {
                    data.append("id", this.id);
                }

                $.ajax({
                    type: "POST",
                    url: "/api/admin/news/save",
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
                            tableNews.ajax.reload();
                            window.alert.show("success", "Lưu thành công", 2000);
                            $('#modal_add_news').modal("hide");
                        } else if (code === 1603) {
                            window.alert.show("error", "Tiêu đề tức này đã tồn tại", 2000);
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
                    url: "/api/admin/news/detail/" + self.id,
                    success: function (response) {
                        if (response.status.code === 1000) {
                            let data = response.data;
                            self.fieldId = data.fieldId;
                            setTimeout(function () {
                                self.subFieldId = data.subFieldId;
                            }, 100)
                            self.title = data.title;
                            CKEDITOR.instances["textarea-content-news"].setData(data.content);

                            if (data.urlImage) {
                                self.$refs['image-intro'].hasImage = true;
                                $(self.$refs['image-intro'].$el).find('img').attr("src", data.urlImage);
                            }
                        }
                    }
                })
            },

            deleteField() {
                let self = this;
                $.ajax({
                    type: "POST",
                    url: "/api/admin/news/delete/" + self.id,
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        window.loader.hide();
                        if (response.status.code === 1000) {
                            tableNews.ajax.reload();
                            window.alert.show("success", "Xóa tin tức thành công", 2000);
                            $("#modal_delete").modal("hide");
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }
                })
            },

            validateForm() {
                this.validateImage();
                this.validateContent();
                this.validateFiled();
                this.validateSubField();
                return !this.isShowErrorContent && !this.isShowErrorField && !this.isShowErrorSubField && !this.isShowErrorImage;
            },
            validateImage() {
                this.isShowErrorImage = !$("#image-intro img").attr("src");
            },
            validateContent() {
                this.isShowErrorContent = CKEDITOR.instances["textarea-content-news"].getData().trim() == "";
            },
            validateFiled() {
                this.isShowErrorField = !this.fieldId;
            },
            validateSubField() {
                if (!this.fieldId) {
                    return;
                }
                this.isShowErrorSubField = !this.subFieldId;
            },

            resetPopup() {
                let self = this;
                this.id = "";
                this.fieldId = "";
                this.subFieldId = "";
                this.title = "";
                CKEDITOR.instances["textarea-content-news"].setData("");
                this.isShowErrorField = false;
                this.isShowErrorImage = false;
                this.$refs['image-intro'].hasImage = false;
                setTimeout(function () {
                    self.isShowErrorContent = false;
                }, 100);
                formNews.resetForm();
            }
        },
        mounted() {
            let self = this;

            self.loadListField();

            $('#modal_add_news').on('hidden.bs.modal', function () {
                self.resetPopup();
            })

            $('#modal_delete').on('hidden.bs.modal', function () {
                self.id = "";
            })

            formNews = $("#form-news").validate({
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
                },
                messages: {
                    title: {
                        required: "Trường này là bắt buộc",
                        maxlength: "Tối đa 200 ký tự",
                    },
                }

            });


        }
    })

    $(document).on("click", ".detail-news", function () {
        newsVue.id = Number($(this).attr("id").replace("btn_detail_", ""));
        newsVue.detail();
    })

    $(document).on("click", ".delete-news", function () {
        newsVue.id = Number($(this).attr("id").replace("btn_delete_", ""));
    })

    $(document).on("click", "#btn_submit_delete", function () {
        newsVue.deleteField();
    })

    let contentNews = CKEDITOR.replace('textarea-content-news', {
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

    contentNews.on('change', function () {
        newsVue.validateContent();
    });


})