$(document).ready(function () {
    $(".list-news").slimScroll({
        height: '300px',
        wheelStep : 10,
        touchScrollStep : 500
    });

    $(document).on("click", ".post", function () {
        let seo = $(this).attr("value");
        let postId = Number($(this).attr("id").replace("post-", ""));
        localStorage.setItem("postId", postId);
        window.location.href = "/discussion-post/" + seo;
    })

    $(document).on("click", ".news", function () {
        let seo = $(this).attr("value");
        window.location.href = "/news/" + seo;
    })
})