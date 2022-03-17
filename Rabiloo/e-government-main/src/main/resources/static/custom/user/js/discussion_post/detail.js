$(document).ready(function () {
    $(document).on("click", ".btn-back", function () {
        if (!window.location.pathname.split("/")[2]) {
            $("#page-list").removeClass("hidden");
            $("#page-detail").addClass("hidden");
            discussionPostVue.getListDiscussionPost(1);
            discussionPostVue.changeToListPage();
        } else {
            window.location.href = "/discussion-post";
        }
    })

    $(document).on("click", "#btn-comment", function () {
        $(".write-comment").toggleClass("hidden");
        if (!$(".write-comment").hasClass("hidden")) {
            $("#input-comment").focus();
        }
    })


})