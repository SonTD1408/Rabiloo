$(document).ready(function () {

    $(document).on("click", "#get_password", function () {
        if (!$("#set_password").valid()) {
            return;
        }

        let formData = new FormData();
        formData.append("password", $("#password").val());
        formData.append("token", $("#token").val());


        $.ajax({
            type: "POST",
            url: "/api/user/setPassword",
            data: formData,
            contentType: false,
            processData: false,
            dataType: "json",
            success: function (response) {
                switch (response.status.code) {
                    case 4:
                        window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        break;
                    case 1000:
                        window.alert.show("success", "Đổi mật khẩu thành công", "2000");
                        setTimeout(function () {
                            window.location.href = '/login';
                        }, 2000);
                        break;
                }
            }
        })
    })

    let validateFormSetPassword = $("#set_password").validate({
        errorElement: "p",
        errorClass: "error-message",
        errorPlacement: function (error, element) {
            error.insertAfter(element);
        },
        ignore: [],
        rules: {
            password: {
                required: true,
                minlength: 8,
                maxlength: 32,
            },
            confirm_password: {
                required: true,
                equalTo: "#password"
            },
            code: {
                required: true,
            }
        },
        messages: {
            password: {
                required: "Trường này là bắt buộc",
                minlength: "Mật khẩu từ 8 đến 32 ký tự",
                maxlength: "Mật khẩu từ 8 đến 32 ký tự",
            },
            confirm_password: {
                required: "Trường này là bắt buộc",
                equalTo: "Mật khẩu và mật khẩu xác nhận không khớp",
            },
        },
    });
})