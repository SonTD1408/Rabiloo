$(document).ready(function () {

    window.loader = {
        show: function () {
            $('#loader').removeClass('d-none');
        },
        hide: function () {
            $('#loader').addClass('d-none');
        },
    }


    window.alert = {
        show: function (type, message, time) {
            if (type === "success") {
                $('#alert').removeClass('alert-danger');
                $('#alert').addClass('alert-success');
                $('#alert .success-icon').removeClass('d-none');
                $('#alert .error-icon').addClass('d-none');
            } else if (type === "error") {
                $('#alert').addClass('alert-danger');
                $('#alert').removeClass('alert-success');
                $('#alert .success-icon').addClass('d-none');
                $('#alert .error-icon').removeClass('d-none');
            }
            $("#message-alert").html(message);

            $("#alert").animate({
                top: "40px"
            }, "slow");

            setTimeout(function () {
                $("#alert").animate({
                    top: "-100px"
                }, "slow");
            }, time);
        }
    }
})
