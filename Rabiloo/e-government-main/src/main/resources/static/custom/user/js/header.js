$(document).ready(function () {

    $(".list-info-user").hide();
    $(document).on("click", "#btn-user", function () {
        $(".list-info-user").toggle();
    })
    $(document).mouseup(function (e) {
        if ($("#btn-user").is(e.target) || $("#btn-user").has(e.target).length > 0) {
            return;
        }

        let wrapInfoUser = $(".list-info-user");
        if (!wrapInfoUser.is(e.target) && wrapInfoUser.has(e.target).length === 0) {
            wrapInfoUser.hide();
        }
    });

    $("#popup-notify").hide();
    $(document).on("click", "#btn-notify", function () {
        $(".popup-notify").toggle();

        if (!$("#popup-notify").is(":hidden")) {
            popupNotifyUserVue.getListNotificationUser();
        }

        $("#btn-select-all-notify").addClass("active");
        $("#btn-select-not-read-notify").removeClass("active");

        if (popupNotifyUserVue.numberNotification == 0) {
            return;
        } else {
            popupNotifyUserVue.setAllNotificationToWatched();
        }
    })
    $(document).mouseup(function (e) {
        if ($("#btn-notify").is(e.target) || $("#btn-notify").has(e.target).length > 0) {
            return;
        }

        let popupNotifyUser = $(".popup-notify");
        if (!popupNotifyUser.is(e.target) && popupNotifyUser.has(e.target).length === 0) {
            popupNotifyUser.hide();
        }
    });


    /* PopupNotifyVue */
    let popupNotifyUserVue = new Vue({
        el: "#wrap-notify",
        data: {
            listNotification: [],
            notificationId: "",

            numberNotification: "",
        },
        methods: {
            getListNotificationUser() {
                let self = this;

                $.ajax({
                    type: "GET",
                    url: "/api/notification/getListNotification",
                    success: function (response) {
                        if (response.status.code === 1000) {
                            self.listNotification = response.data;
                        }
                    }
                })
            },

            getListNotificationUserNotRead() {
                this.listNotification = this.listNotification.filter(function (notification) {
                    return !notification.read;
                })
            },

            getNumberNotificationHavenWatch() {
                let self = this;
                $.ajax({
                    type: "GET",
                    url: "/api/notification/getNumberNotificationHavenWatch",
                    success: function (response) {
                        self.numberNotification = response.data;
                        if (self.numberNotification > 0) {
                            $(".wrap-number-notify").removeClass("hidden");
                        }
                    }
                })
            },

            setAllNotificationToWatched() {
                let self = this;
                $(".wrap-number-notify").addClass("hidden");
                self.numberNotification = 0;

                $.ajax({
                    type: "GET",
                    url: "/api/notification/setAllNotificationToWatched",
                    success: function (response) {
                    }
                })
            },

            detailNotification() {
                let self = this;
                $.ajax({
                    type: "GET",
                    url: "/api/notification/detail/" + self.notificationId,
                    success: function (response) {
                        if (response.status.code === 1000) {
                            $("#popup-notify").hide();
                            let dataForDetailPost = response.data;
                            let postId = dataForDetailPost.postId;
                            let fieldId = dataForDetailPost.fieldId;
                            let commentId = dataForDetailPost.commentId;
                            let replyCommentId = dataForDetailPost.replyCommentId;

                            if (window.location.pathname.split("/")[1] === "discussion-post") {
                                discussionPostVue.commentIdScroll = commentId;
                                discussionPostVue.replyCommentIdScroll = replyCommentId;
                                discussionPostVue.postId = postId;
                                discussionPostVue.fieldId = fieldId;

                                discussionPostVue.activeField();
                                discussionPostVue.changeToDetailPage();
                                discussionPostVue.detailPost();
                                discussionPostVue.autoScrollElement();
                            } else {
                                localStorage.setItem("commentIdScroll", commentId);
                                localStorage.setItem("replyCommentIdScroll", replyCommentId);
                                localStorage.setItem("postId", postId);
                                localStorage.setItem("fieldId", fieldId);

                                window.location.href = "/discussion-post";
                            }
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }
                })
            }
        },
        mounted() {
            let self = this;

            self.getNumberNotificationHavenWatch();

            $(document).on("click", ".notify-item", function () {
                self.notificationId = Number($(this).attr("id").replace("notification_", ""));
                self.detailNotification();
            })

            $(document).on("click", ".btn-select-type-notify", function () {
                if ($(this).hasClass("active")) {
                    return;
                }

                let idElement = $(this).attr("id");
                if (idElement === "btn-select-all-notify") {
                    self.getListNotificationUser();
                } else {
                    self.getListNotificationUserNotRead();
                }

                $(".btn-select-type-notify").each(function () {
                    $(this).removeClass("active");
                })
                $(this).addClass("active");

            })
        },
    })


})