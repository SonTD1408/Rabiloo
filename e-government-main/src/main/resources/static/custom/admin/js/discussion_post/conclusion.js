$(document).ready(function () {
    let postId = Number($("#post_id").val());

    let conclusionPostVue = new Vue({
        el: "#modal_conclusion",
        data: {
            isShowErrorConclusion: false,
        },
        methods: {
            saveConclusion() {
                if (this.isShowErrorConclusion) {
                    return;
                }

                let data = {
                    "postId": postId,
                    "conclusion": CKEDITOR.instances["textarea-conclusion"].getData().trim()
                }

                $.ajax({
                    type: "POST",
                    url: "/api/discussion_post/saveConclusion",
                    data: JSON.stringify(data),
                    contentType: "application/json",
                    success: function (response) {
                        if (response.status.code === 1000) {
                            window.alert.show("success", "Đã lưu kết luận bài viết", 2000);
                            $("#modal_conclusion").modal("hide");

                            setTimeout(function () {
                                window.location.reload();
                            }, 2000);
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }

                })
            },

            validateConclusion() {
                this.isShowErrorConclusion = CKEDITOR.instances["textarea-conclusion"].getData().trim() == "";
            },

        }
    })

    let conclusionPost = CKEDITOR.replace('textarea-conclusion', {
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
    conclusionPost.on('change', function() {
        conclusionPostVue.validateConclusion();
    });
})