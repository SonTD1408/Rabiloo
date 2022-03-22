let discussionPostVue;

$(document).ready(function () {
    let currentUserId = Number($("#current-user-id").val());
    let currentRoleUserId = Number($("#current-role-user-id").val());
    let postDetailId = Number($("#post-detail-id").val());
    let fieldId = Number($("#field-id").val());
    let plusViewTimeOut;

    $(document).on("click", ".marquee-post", function () {
        discussionPostVue.postId = Number($(this).attr("id").replace('post-latest-', ''));
        if (!discussionPostVue.postId) {
            return;
        }

        discussionPostVue.detailPost();

        discussionPostVue.changeToDetailPage();
    })

    discussionPostVue = new Vue({
        el: "#discussion-post",
        data: {
            currentUserId: currentUserId,
            prefixImage: "",

            listField: [],
            fieldId: 1,
            listDiscussionPost: [],

            size: 7,
            page: 1,
            keyword: "",
            orderBy: "lasted",

            totalElements: "",
            totalPages: 1,

            formatTime: "HH:mm DD/MM/YYYY",

            /* for detail post */
            postId: "",
            title: "",
            content: "",
            target: "",
            closingDeadline: "",
            conclude: "",
            numberView: "",
            listRoleIdCanComment: [],
            isCanComment: false,

            listComment: [],


            /* for comment and reply comment */
            image: null,

            /* for submit reply comment */
            commentId: "",
            replyCommentId: "",
            contentReplyComment: "",
            userTagId: "",

            /* for edit reply comment */
            isHaveUserTagEditReplyComment: false,
            contentEditReplyComment: "",

            /* scroll to element */
            commentIdScroll: "",
            replyCommentIdScroll: "",

            /* delete comment */
            deleteCommentId: "",
            deleteReplyCommentId: "",

        },
        watch: {
            fieldId(fieldId) {
                if (!fieldId || fieldId == "null") {
                    return;
                }
                this.getListDiscussionPost(1);
            },
            orderBy() {
                this.getListDiscussionPost(1);
                this.changeToListPage();
            },
            postId(value) {
                if (!value) return;

                this.detailPost();

                this.countTime();
            },
        },
        methods: {
            /* xem detail post 5s thì +1 view */
            countTime() {
                let self = this;

                plusViewTimeOut = setTimeout(function () {
                    $.ajax({
                        type: "POST",
                        url: "/api/discussion_post/plusView/" + self.postId,
                        success: function (response) {
                            if (response.status.code === 1000) {
                            }
                        }
                    })
                }, 5000);
            },

            getListField() {
                let self = this;
                $.ajax({
                    type: "GET",
                    url: "/api/field/getList",
                    success: function (response) {
                        if (response.status.code === 1000) {
                            self.listField = response.data;
                            if (self.listField.length > 0 && (!self.fieldId || self.fieldId == "null")) {
                                self.fieldId = self.listField[0].id;
                            }
                        }
                    }
                })
            },

            getListDiscussionPost(page) {
                let self = this;

                let params = {
                    "size": self.size,
                    "page": page,
                    "keyword": self.keyword,
                    "fieldId": self.fieldId,
                    "orderBy": self.orderBy,
                }

                $.ajax({
                    type: "GET",
                    url: "/api/discussion_post/getPage",
                    data: params,
                    success: function (response) {
                        self.listDiscussionPost = response.content.map(post => {
                            post.createdTime = moment(post.createdTime).format(self.formatTime);
                            return post;
                        });
                        self.totalElements = response.totalElements;
                        self.totalPages = response.totalPages;
                        self.size = response.size;

                        if (self.totalElements == 0) {
                            $('#pagination').twbsPagination('destroy');
                        } else {
                            let $pagination = $('#pagination');
                            $pagination.twbsPagination('destroy');
                            $pagination.twbsPagination($.extend({}, {
                                startPage: page,
                                totalPages: self.totalPages,
                                initiateStartPageClick: false,
                                onPageClick: function (event, page) {
                                    self.getListDiscussionPost(page);
                                }
                            }));

                        }

                    }
                })
            },

            detailPost() {
                let self = this;

                $.ajax({
                    type: "GET",
                    url: "/api/discussion_post/detail/" + self.postId,
                    success: function (response) {
                        if (response.status.code === 1000) {
                            let data = response.data;
                            self.title = data.title;
                            self.content = data.content;
                            self.target = data.target;
                            self.closingDeadline = moment(data.closingDeadline).format('DD/MM/YYYY');
                            self.conclude = data.conclude;
                            self.numberView = data.numberView;

                            self.listComment = data.listComment;
                            self.listRoleIdCanComment = data.listRole.map(function (role) {
                                return role.id;
                            })
                            self.isCanComment = self.listRoleIdCanComment.includes(currentRoleUserId);
                        }
                    }
                })
            },

            submitComment() {
                let self = this;

                let formData = new FormData();
                formData.append("postId", self.postId);
                formData.append("content", $("#input-comment").text().trim());

                if (self.image) {
                    formData.append("image", self.image);
                    formData.append("isHaveImage", true);
                } else {
                    formData.append("isHaveImage", false);
                }

                $.ajax({
                    type: "POST",
                    url: "/api/comment/submitComment",
                    contentType: false,
                    processData: false,
                    data: formData,
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        window.loader.hide();
                        let code = response.status.code;

                        if (code === 1000) {
                            window.alert.show("success", "Bình luận của bạn đã được gửi đi, vui lòng đợi phê duyệt", 2000);
                            self.handlerAfterSubmitComment();
                            self.detailPost();
                        } else if (code === 1400) {
                            window.alert.show("error", "Bạn cần đăng nhập để bình luận", 2000);
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }
                })
            },

            submitReplyComment() {
                let self = this;
                let formData = new FormData();
                formData.append("commentId", self.commentId);
                formData.append("content", self.contentReplyComment);

                if (self.image) {
                    formData.append("image", self.image);
                    formData.append("isHaveImage", true);
                } else {
                    formData.append("isHaveImage", false);
                }

                if (self.userTagId) {
                    formData.append("userTagId", self.userTagId);
                }

                $.ajax({
                    type: "POST",
                    url: "/api/reply_comment/submitReplyComment",
                    contentType: false,
                    processData: false,
                    data: formData,
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        window.loader.hide();
                        let code = response.status.code;

                        if (code === 1000) {
                            window.alert.show("success", "Bình luận của bạn đã được gửi đi, vui lòng đợi phê duyệt", 2000);
                            self.handlerAfterSubmitReplyComment();
                            self.detailPost();
                        } else if (code === 1400) {
                            window.alert.show("error", "Bạn cần đăng nhập để bình luận", 2000);
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }
                })
            },

            submitEditComment() {
                let self = this;

                let formData = new FormData();
                formData.append("commentId", self.commentId);
                formData.append("content", $("#input-edit-comment-" + self.commentId).text().trim());

                if (self.image) {
                    formData.append("image", self.image);
                }

                formData.append("haveImage", (!!self.image || !!$("#image-edit-comment-" + self.commentId).attr("src")));


                $.ajax({
                    type: "POST",
                    url: "/api/comment/submitComment",
                    contentType: false,
                    processData: false,
                    data: formData,
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        window.loader.hide();
                        let code = response.status.code;

                        if (code === 1000) {
                            window.alert.show("success", "Bình luận của bạn đã được gửi đi, vui lòng đợi phê duyệt", 2000);
                            self.handlerAfterSubmitEditComment();
                            self.detailPost();
                        } else if (code === 1400) {
                            window.alert.show("error", "Bạn cần đăng nhập để bình luận", 2000);
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }
                })
            },

            submitEditReplyComment() {
                let self = this;

                let formData = new FormData();
                formData.append("replyCommentId", self.replyCommentId);
                formData.append("content", self.contentEditReplyComment);
                formData.append("haveUserTagEditReplyComment", self.isHaveUserTagEditReplyComment);

                if (self.image) {
                    formData.append("image", self.image);
                }

                formData.append("haveImage", (!!self.image || !!$("#image-edit-reply-comment-" + self.replyCommentId).attr("src")));

                $.ajax({
                    type: "POST",
                    url: "/api/reply_comment/submitReplyComment",
                    contentType: false,
                    processData: false,
                    data: formData,
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        window.loader.hide();
                        let code = response.status.code;

                        if (code === 1000) {
                            window.alert.show("success", "Bình luận của bạn đã được gửi đi, vui lòng đợi phê duyệt", 2000);
                            self.handlerAfterSubmitEditReplyComment();
                            self.detailPost();
                        } else if (code === 1400) {
                            window.alert.show("error", "Bạn cần đăng nhập để bình luận", 2000);
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }
                })
            },

            changeToListPage() {
                this.postId = "";
                clearTimeout(plusViewTimeOut);
                $("#page-list").removeClass("hidden");
                $("#page-detail").addClass("hidden");
            },

            changeToDetailPage() {
                $("#page-list").addClass("hidden");
                $("#page-detail").removeClass("hidden");
            },

            /* choose image */
            chooseImageComment() {
                $("#wrap-icon-choose-image").addClass("hidden");
                $("#wrap-image-comment").removeClass("hidden");
            },

            chooseImageReplyComment() {
                $("#wrap-icon-choose-image-" + this.commentId).addClass("hidden");
                $("#wrap-image-reply-" + this.commentId).removeClass("hidden");
            },

            chooseImageEditComment() {
                $("#wrap-icon-choose-image-edit-comment-" + this.commentId).addClass("hidden");
                $("#wrap-image-edit-comment-" + this.commentId).removeClass("hidden");
            },

            chooseImageEditReplyComment() {
                $("#wrap-icon-choose-image-edit-reply-comment-" + this.replyCommentId).addClass("hidden");
                $("#wrap-image-edit-reply-comment-" + this.replyCommentId).removeClass("hidden");
            },

            /* remove image */
            removeImageComment() {
                this.image = null;
                $("#image-comment").attr("src", "");
                $("#wrap-icon-choose-image").removeClass("hidden");
                $("#wrap-image-comment").addClass("hidden");
            },

            removeImageReplyComment() {
                this.image = null;
                $("#image-reply-" + this.commentId).attr("src", "");
                $("#wrap-icon-choose-image-" + this.commentId).removeClass("hidden");
                $("#wrap-image-reply-" + this.commentId).addClass("hidden");
            },

            removeImageEditComment() {
                this.image = null;
                $("#image-edit-comment-" + this.commentId).attr("src", "");
                $("#wrap-icon-choose-image-edit-comment-" + this.commentId).removeClass("hidden");
                $("#wrap-image-edit-comment-" + this.commentId).addClass("hidden");
            },

            removeImageEditReplyComment() {
                this.image = null;
                $("#image-edit-reply-comment-" + this.replyCommentId).attr("src", "");
                $("#wrap-icon-choose-image-edit-reply-comment-" + this.replyCommentId).removeClass("hidden");
                $("#wrap-image-edit-reply-comment-" + this.replyCommentId).addClass("hidden");
            },

            handlerAfterSubmitComment() {
                $("#input-comment").html("");
                this.removeImageComment();
            },

            handlerAfterSubmitReplyComment() {
                this.removeImageReplyComment();
                this.contentReplyComment = "";
                this.userTagId = "";
                $(".input-reply").html("");
                this.commentId = "";
            },

            handlerAfterSubmitEditComment() {
                $("#input-edit-comment-" + this.commentId).html();
                $("#wrap-content-comment-" + this.commentId).removeClass("hidden");
                $("#wrap-edit-comment-" + this.commentId).addClass("hidden");
                this.removeImageEditComment();
                this.commentId = "";
            },

            handlerAfterSubmitEditReplyComment() {
                $("#input-edit-reply-comment-" + this.replyCommentId).html();
                $("#wrap-content-reply-comment-" + this.replyCommentId).removeClass("hidden");
                $("#wrap-edit-reply-comment-" + this.replyCommentId).addClass("hidden");
                this.removeImageEditReplyComment();
                this.replyCommentId = "";
            },

            activeField() {
                $(".field-item").each(function () {
                    $(this).removeClass("active");
                })
                $("#field-" + this.fieldId).addClass("active");
            },

            autoScrollElement() {
                let self = this;

                setTimeout(function () {
                    if (self.commentIdScroll && self.commentIdScroll != "null") {
                        if ($("#comment-" + self.commentIdScroll).length) {
                            $('html, body').animate({
                                scrollTop: $("#comment-" + self.commentIdScroll).offset().top - $(window).height() / 2
                            }, 300);
                            $("#wrap-content-comment-" + self.commentIdScroll).addClass("active");
                        } else {
                            window.alert.show("error", "Bình luận không tồn tại", 2000);
                        }
                    } else if (self.replyCommentIdScroll && self.replyCommentIdScroll != "null") {
                        if ($("#reply-comment-" + self.replyCommentIdScroll).length) {
                            $('html, body').animate({
                                scrollTop: $("#reply-comment-" + self.replyCommentIdScroll).offset().top - $(window).height() / 2
                            }, 300);
                            $("#wrap-content-reply-comment-" + self.replyCommentIdScroll).addClass("active");
                        } else {
                            window.alert.show("error", "Bình luận không tồn tại", 2000);
                        }
                    }
                }, 1000);
            }
        },
        mounted() {
            let self = this;

            self.getListField();

            self.fieldId = localStorage.getItem("fieldId");
            self.fieldId = fieldId ? fieldId : self.fieldId;

            self.postId = localStorage.getItem("postId");
            self.postId = postDetailId ? postDetailId : self.postId;

            self.commentIdScroll = localStorage.getItem("commentIdScroll");
            self.replyCommentIdScroll = localStorage.getItem("replyCommentIdScroll");

            if (self.fieldId && self.fieldId != "null") {
                setTimeout(function () {
                    self.activeField();
                }, 100)
            }

            localStorage.removeItem("postId");
            localStorage.removeItem("commentIdScroll");
            localStorage.removeItem("replyCommentIdScroll");
            localStorage.removeItem("fieldId");

            if (self.postId) {
                self.changeToDetailPage();
                if (self.commentIdScroll || self.replyCommentIdScroll) {
                    self.autoScrollElement();
                }
            } else {
                setTimeout(function () {
                    $('#pagination').twbsPagination({
                        totalPages: self.totalPages,
                        visiblePages: 5,
                        onPageClick: function (event, page) {
                            self.getListDiscussionPost(page);
                            self.changeToListPage();
                        }
                    });
                }, 200);
            }


            $(document).on("click", ".field-item", function () {
                self.changeToListPage();
                self.fieldId = Number($(this).attr("id").replace("field-", ""));
                self.activeField();
            })

            $("#search-discussion-post").on("keypress", function (e) {
                if (e.keyCode === 13) {
                    self.getListDiscussionPost(1);
                    self.changeToListPage();
                }
            })

            /* view detail discussion post */
            $(document).on("click", ".post-content", function () {
                self.postId = Number($(this).attr("id").replace("post_", ""));
                self.changeToDetailPage();
            })

            /* enter submit comment */
            $("#input-comment").on("keypress", function (e) {
                if (e.keyCode === 13) {
                    e.preventDefault();
                    self.submitComment();
                }
            })

            /* enter submit reply comment */
            $(document).on("keypress", ".input-reply", function (e) {
                if (e.keyCode === 13) {
                    self.commentId = Number($(this).attr("id").replace("input-reply-", ""));

                    let isHaveUserTag = $(this).find(".user-tag").length !== 0;
                    if (isHaveUserTag) {
                        self.contentReplyComment = $(this).find(".content-reply").text().trim();
                        self.userTagId = $(this).find(".user-tag").attr("id").replace("user-tag-", "");
                    } else {
                        self.contentReplyComment = $(this).text().trim();
                    }
                    e.preventDefault();
                    self.submitReplyComment();
                }
            })

            /* enter submit edit comment */
            $(document).on("keypress", ".input-edit-comment", function (e) {
                if (e.keyCode === 13) {
                    self.commentId = Number($(this).attr("id").replace("input-edit-comment-", ""));

                    e.preventDefault();
                    self.submitEditComment();
                }
            })

            /* enter submit edit reply comment */
            $(document).on("keypress", ".input-edit-reply-comment", function (e) {
                if (e.keyCode === 13) {
                    self.replyCommentId = Number($(this).attr("id").replace("input-edit-reply-comment-", ""));

                    self.isHaveUserTagEditReplyComment = $(this).find('span').text().trim() !== "";
                    self.contentEditReplyComment = $(this).clone()    //clone the element
                        .children() //select all the children
                        .remove()   //remove all the children
                        .end()  //again go back to selected element
                        .text().trim();

                    e.preventDefault();
                    self.submitEditReplyComment();
                }
            })


            /* choose image comment */
            $(document).on("click", "#btn-choose-image", function () {
                $("#input-choose-image").trigger("click");
            })

            /* choose image reply comment */
            $(document).on("click", ".btn-choose-image-reply-comment", function () {
                self.commentId = Number($(this).attr("id").replace("btn-choose-image-", ""));
                $("#input-choose-image-" + self.commentId).trigger("click");
            })

            /* choose image edit comment */
            $(document).on("click", ".btn-choose-image-edit-comment", function () {
                self.commentId = Number($(this).attr("id").replace("btn-choose-image-edit-comment-", ""));
                $("#input-choose-image-edit-comment-" + self.commentId).trigger("click");
            })

            /* choose image edit reply comment */
            $(document).on("click", ".btn-choose-image-edit-reply-comment", function () {
                self.replyCommentId = Number($(this).attr("id").replace("btn-choose-image-edit-reply-comment-", ""));
                $("#input-choose-image-edit-reply-comment-" + self.replyCommentId).trigger("click");
            })

            /*  change input choose image comment */
            $("#input-choose-image").on("change", function () {
                let image = this.files[0];
                if (image == undefined) {
                    return;
                }
                self.image = image;
                let reader = new FileReader();

                reader.onload = function () {
                    $("#image-comment").attr("src", this.result);
                };
                self.chooseImageComment();

                reader.readAsDataURL(image);
            })

            /* change input choose image reply comment */
            $(document).on("change", ".input-choose-image", function () {
                self.commentId = $(this).attr("id").replace("input-choose-image-", "");
                let image = this.files[0];
                if (image == undefined) {
                    return;
                }

                /* ẩn các image reply khác */
                $(".wrap-image-reply").each(function () {
                    $(this).addClass("hidden");
                })
                $(".wrap-image-reply img").each(function () {
                    $(this).attr("src", "");
                })

                self.image = image;
                let reader = new FileReader();

                reader.onload = function () {
                    $("#image-reply-" + self.commentId).attr("src", this.result);
                };
                self.chooseImageReplyComment();

                reader.readAsDataURL(image);
            })

            /* change input choose image edit comment */
            $(document).on("change", ".input-choose-image-edit-comment", function () {
                self.commentId = $(this).attr("id").replace("input-choose-image-edit-comment-", "");
                let image = this.files[0];
                if (image == undefined) {
                    return;
                }

                /* ẩn các image edit comment khác */
                $(".wrap-image-edit-comment").each(function () {
                    $(this).addClass("hidden");
                })
                $(".wrap-image-edit-comment img").each(function () {
                    $(this).attr("src", "");
                })

                self.image = image;
                let reader = new FileReader();

                reader.onload = function () {
                    $("#image-edit-comment-" + self.commentId).attr("src", this.result);
                };
                self.chooseImageEditComment();

                reader.readAsDataURL(image);
            })

            /* change input choose image edit reply comment */
            $(document).on("change", ".input-choose-image-edit-reply-comment", function () {
                self.replyCommentId = $(this).attr("id").replace("input-choose-image-edit-reply-comment-", "");
                let image = this.files[0];
                if (image == undefined) {
                    return;
                }

                /* ẩn các image edit comment khác */
                $(".wrap-image-edit-reply-comment").each(function () {
                    $(this).addClass("hidden");
                })
                $(".wrap-image-edit-reply-comment img").each(function () {
                    $(this).attr("src", "");
                })

                self.image = image;
                let reader = new FileReader();

                reader.onload = function () {
                    $("#image-edit-reply-comment-" + self.replyCommentId).attr("src", this.result);
                };
                self.chooseImageEditReplyComment();

                reader.readAsDataURL(image);
            })

            /* remove image comment */
            $("#btn-remove-image-comment").on("click", function () {
                self.removeImageComment();
            })

            /* remove image reply comment */
            $(document).on("click", ".wrap-btn-remove-image-reply-comment", function () {
                self.commentId = Number($(this).attr("id").replace("btn-remove-image-reply-", ""));
                self.removeImageReplyComment();
            })

            /* remove image edit comment */
            $(document).on("click", ".wrap-btn-remove-image-edit-comment", function () {
                self.commentId = Number($(this).attr("id").replace("btn-remove-image-edit-comment-", ""));
                self.removeImageEditComment();
            })

            /* remove image edit reply comment */
            $(document).on("click", ".wrap-btn-remove-image-edit-reply-comment", function () {
                self.replyCommentId = Number($(this).attr("id").replace("btn-remove-image-edit-reply-comment-", ""));
                self.removeImageEditReplyComment();
            })

            /* show large image */
            $(document).on("click", ".image-comment-small", function () {
                let srcImage = $(this).attr("src");
                $("#large-image").attr("src", srcImage);
                $("#show_image_popup").show();
                $("#overlay").show();
            })

            /* close large image */
            $(document).mouseup(function (e) {
                let imageLarge = $("#show_image_popup");
                if (!imageLarge.is(e.target) && imageLarge.has(e.target).length === 0) {
                    imageLarge.hide();
                    $("#overlay").hide();
                }
            });

            /* click btn reply comment */
            $(document).on("click", ".btn-reply-comment", function () {
                let tagUserIdAndCommentId = $(this).attr("id").replace("btn-reply-comment-", "");
                let tagUserId = Number(tagUserIdAndCommentId.split("-")[0]);
                let commentId = Number(tagUserIdAndCommentId.split("-")[1]);

                $('html, body').animate({
                    scrollTop: $("#input-reply-" + commentId).offset().top
                }, 300);
                $("#input-reply-" + commentId).focus();


                if (currentUserId == tagUserId) {
                    $("#input-reply-" + commentId).html("");
                    return;
                }

                $.ajax({
                    type: "GET",
                    url: "/api/user/getNameUser/" + tagUserId,
                    success: function (response) {
                        if (response.status.code === 1000) {
                            $("#input-reply-" + commentId).html(`<p><span class="user-tag" id="user-tag-${tagUserId}">${response.data}</span><span contenteditable="true" class="content-reply"></span></p>`);
                            self.$forceUpdate();
                        }
                    }
                }).done(function () {
                    $("#input-reply-" + commentId + " .content-reply").append("&nbsp;");
                })

            });

            /* click btn Trả lời */
            $(document).on("click", ".btn-reply", function () {
                let commentId = Number($(this).attr("id").replace("btn-reply-", ""));

                $('html, body').animate({
                    scrollTop: $("#input-reply-" + commentId).offset().top
                }, 300);
                $("#input-reply-" + commentId).focus();
            })

            /* click icon action comment */
            $(document).on("click", ".icon-action-comment", function () {
                let commentId = $(this).attr("id").replace("icon-action-comment-", "");
                $("#list-action-comment-" + commentId).toggleClass("hidden");
            })
            $(document).mouseup(function (e) {
                if ($(".icon-action-comment").is(e.target) || $(".icon-action-comment").has(e.target).length > 0) {
                    return;
                }

                let listActionComment = $(".list-action-comment");
                if (!listActionComment.is(e.target) && listActionComment.has(e.target).length === 0) {
                    listActionComment.addClass("hidden");
                }
            });

            /* click icon action reply comment */
            $(document).on("click", ".icon-action-reply-comment", function () {
                let replyCommentId = $(this).attr("id").replace("icon-action-reply-comment-", "");
                $("#list-action-reply-comment-" + replyCommentId).toggleClass("hidden");
            })
            $(document).mouseup(function (e) {
                if ($(".icon-action-reply-comment").is(e.target) || $(".icon-action-reply-comment").has(e.target).length > 0) {
                    return;
                }

                let listActionReplyComment = $(".list-action-reply-comment");
                if (!listActionReplyComment.is(e.target) && listActionReplyComment.has(e.target).length === 0) {
                    listActionReplyComment.addClass("hidden");
                }
            });


            /* click edit comment */
            $(document).on("click", ".edit-comment", function () {
                let commentId = $(this).attr("id").replace("edit-comment-", "");

                if ($("#image-comment-" + commentId).length > 0) {
                    let srcImage = $("#image-comment-" + commentId).attr("src");
                    $("#wrap-icon-choose-image-edit-comment-" + commentId).addClass("hidden");
                    $("#wrap-image-edit-comment-" + commentId).removeClass("hidden");
                    $("#image-edit-comment-" + commentId).attr("src", srcImage);
                }

                let contentComment = $("#content-comment-" + commentId).text();

                $("#wrap-content-comment-" + commentId).addClass("hidden");
                $("#wrap-edit-comment-" + commentId).removeClass("hidden");

                $("#input-edit-comment-" + commentId).text(contentComment);
                $("#input-edit-comment-" + commentId).focus();
            });

            /* click delete comment */
            $(document).on("click", ".delete-comment", function () {
                self.deleteCommentId = $(this).attr("id").replace("delete-comment-", "");
                $("#modal_delete_comment").modal("show");
            })

            /* cancel edit comment */
            $(document).on("click", ".cancel-edit-comment", function () {
                let commentId = $(this).attr("id").replace("cancel-edit-comment-", "");

                $("#wrap-content-comment-" + commentId).removeClass("hidden");
                $("#wrap-edit-comment-" + commentId).addClass("hidden");
            })

            /* click edit reply comment */
            $(document).on("click", ".edit-reply-comment", function () {
                let replyCommentId = $(this).attr("id").replace("edit-reply-comment-", "");

                if ($("#image-reply-comment-" + replyCommentId).length > 0) {
                    let srcImage = $("#image-reply-comment-" + replyCommentId).attr("src");
                    $("#wrap-icon-choose-image-edit-reply-comment-" + replyCommentId).addClass("hidden");
                    $("#wrap-image-edit-reply-comment-" + replyCommentId).removeClass("hidden");
                    $("#image-edit-reply-comment-" + replyCommentId).attr("src", srcImage);
                }

                let contentReplyComment = $("#content-reply-comment-" + replyCommentId).html();

                $("#wrap-content-reply-comment-" + replyCommentId).addClass("hidden");
                $("#wrap-edit-reply-comment-" + replyCommentId).removeClass("hidden");

                $("#input-edit-reply-comment-" + replyCommentId).html(contentReplyComment);
                $("#input-edit-reply-comment-" + replyCommentId).focus();
            });

            /* click delete reply comment */
            $(document).on("click", ".delete-reply-comment", function () {
                self.deleteReplyCommentId = $(this).attr("id").replace("delete-reply-comment-", "");
                $("#modal_delete_comment").modal("show");
            })

            /* cancel edit reply comment */
            $(document).on("click", ".cancel-edit-reply-comment", function () {
                let replyCommentId = $(this).attr("id").replace("cancel-edit-reply-comment-", "");

                $("#wrap-content-reply-comment-" + replyCommentId).removeClass("hidden");
                $("#wrap-edit-reply-comment-" + replyCommentId).addClass("hidden");
            })

            $('#modal_delete_comment').on('hidden.bs.modal', function () {
                self.deleteReplyCommentId = "";
                self.deleteCommentId = "";
            })

            /* submit delete comment or reply_comment */
            $(document).on("click", "#btn_submit_delete_comment", function () {
                let url = self.deleteCommentId ? "/api/comment/delete/" + self.deleteCommentId :
                    "/api/reply_comment/delete/" + self.deleteReplyCommentId;
                $.ajax({
                    type: "POST",
                    url: url,
                    beforeSend: function () {
                        window.loader.show();
                    },
                    success: function (response) {
                        window.loader.hide();
                        if (response.status.code === 1000) {
                            self.detailPost();
                            $("#modal_delete_comment").modal("hide");
                        } else {
                            window.alert.show("error", "Đã có lỗi xảy ra, vui lòng thử lại sau", 2000);
                        }
                    }
                })
            })

            /* remove background active comment, reply_comment */
            $(document).mouseup(function (e) {
                if ($(".wrap-content-comment.active").is(e.target) || $(".wrap-content-comment.active").has(e.target).length > 0) {
                    return;
                }

                if ($(".wrap-content-reply-comment.active").is(e.target) || $(".wrap-content-reply-comment.active").has(e.target).length > 0) {
                    return;
                }

                $(".wrap-content-comment").each(function () {
                    $(this).removeClass("active");
                })

                $(".wrap-content-reply-comment").each(function () {
                    $(this).removeClass("active");
                })
            });

            /* click heart comment */
            $(document).on("click", ".heart-react-comment", function () {
                let commentId = Number($(this).attr("id").replace("heart-comment-", ""));
                $.ajax({
                    type: "POST",
                    url: "/api/like/comment/" + commentId,
                    success: function (response) {
                        let code = response.status.code
                        if (code === 1000) {
                            self.detailPost();
                        }
                        if (code === 1400) {
                            window.alert.show("error", "Bạn cần đăng nhập để thực hiện", 2000);
                        }
                    }
                })
            })

            /* click heart reply comment */
            $(document).on("click", ".heart-react-reply-comment", function () {
                let replyCommentId = Number($(this).attr("id").replace("heart-reply-comment-", ""));
                $.ajax({
                    type: "POST",
                    url: "/api/like/replyComment/" + replyCommentId,
                    success: function (response) {
                        let code = response.status.code
                        if (code === 1000) {
                            self.detailPost();
                        }
                        if (code === 1400) {
                            window.alert.show("error", "Bạn cần đăng nhập để thực hiện", 2000);
                        }
                    }
                })
            })

        }
    })

    $(document).on({
        mouseenter: function () {
            $(this).parent().find(".wrap-reason-denied").removeClass("hidden");
        },
        mouseleave: function () {
            $(this).parent().find(".wrap-reason-denied").addClass("hidden");
        }
    }, ".label-reason");
})