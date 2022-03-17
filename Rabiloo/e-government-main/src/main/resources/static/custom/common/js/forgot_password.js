$(document).ready(function () {
    $("#get_code").on('click', function () {
        let email = $("#email").val();
        $("#get_code").prop("disabled", true);
        if (!validateEmail(email)) {
            return;
        } else {
            let formData = new FormData();
            formData.append("email", $("#email").val());
            $.ajax({
                type: "POST",
                url: "/api/user/forgotPassword",
                data: formData,
                contentType: false,
                processData: false,
                dataType: "json",
                success: function (response) {
                    $("#get_code").prop("disabled", false);
                    switch (response.status.code) {
                        case 1602:
                            window.alert.show("error", "Email không tồn tại, vui lòng thử lại", "2000");
                            break;
                        case 1000:
                            setTimeout(function () {
                                window.alert.show("success", "Đã gửi thông tin xác thực qua email", "2000");
                            }, 1200)
                            break;
                    }
                }
            })
        }
    });

    $(document).on("keyup", "#email", function () {
        validateEmail($("#email").val());
    })

    function validateEmail(email) {
        if (email == "") {
            $("#error-message-obligatory").removeClass("hidden");
            $("#error-message-format").addClass("hidden");
            return false;
        }

        let regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
        let isErrorFormat = !regex.test(email);
        if (isErrorFormat) {
            $("#error-message-obligatory").addClass("hidden");
            $("#error-message-format").removeClass("hidden");
            return false;
        }

        $("#error-message-obligatory").addClass("hidden");
        $("#error-message-format").addClass("hidden");
        return true;
    }
})