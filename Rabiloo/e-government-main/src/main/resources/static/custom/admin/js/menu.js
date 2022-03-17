$(document).ready(function () {
    $('[data-toggle="tooltip"]').tooltip();

    //scroll item active
    if ($(".active-menu").length > 0) {
        $('.left-menu').animate({
            scrollTop: $(".active-menu").offset().top
        }, 1000);
    }

    $(".control-block-menu").on('click', function () {
        let parentClass = $(this).parent().attr('class').replace("block-menu ", "");
        let icon = $(this).find('.block-icon');
        if (icon.hasClass("fa-angle-double-down")) {
            icon.removeClass("fa-angle-double-down");
            icon.addClass("fa-angle-double-right");
            $("." + parentClass + " .menu").addClass("d-none");
        } else {
            icon.addClass("fa-angle-double-down");
            icon.removeClass("fa-angle-double-right");
            $("." + parentClass + " .menu").removeClass("d-none");
        }
    });


    let toggle = true;

    let toogleLeftMenu = function () {
        if (toggle) {
            hideItem();
        } else {
            showItem();
        }
        toggle = !toggle;
    };

    $("#icon_menu").click(function () {
        toogleLeftMenu();
    });

    let hideItem = function () {
        $("#icon_menu .fa-arrow-left").addClass("d-none");
        $("#icon_menu .fa-arrow-right").removeClass("d-none");
        $(".left-menu").addClass('left-menu-not-active');
        $(".block-menu span").addClass('d-none');
        $(".block-icon, .block-icon-sub-menu").addClass("d-none");
        $("#icon_menu").addClass('icon-hide');
        //header
        $(".header-menu").removeClass("header-mini");
        //content
        $(".page-content").addClass("page-content-big");
    }

    let showItem = function () {
        $("#icon_menu .fa-arrow-left").removeClass("d-none");
        $("#icon_menu .fa-arrow-right").addClass("d-none");
        $(".left-menu").removeClass('left-menu-not-active');
        $(".block-menu span").removeClass('d-none');
        $(".block-icon, .block-icon-sub-menu").removeClass("d-none");
        $("#icon_menu").removeClass('icon-hide');
        //header
        $(".header-menu").addClass("header-mini");
        //content
        $(".page-content").removeClass("page-content-big");
    }


})