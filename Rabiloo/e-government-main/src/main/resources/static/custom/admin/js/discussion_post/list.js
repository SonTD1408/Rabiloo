let tableDiscussionPost;
$(document).ready(function () {

    let columnDefinitions = [
        {"data": "title", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "target", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "createdTime", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": null, "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "closingDeadline", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": null, "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": null, "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "conclude", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "approved", "orderable": false, "defaultContent": "", "class": 'text-center'},
    ];

    if (isManagePost) {
        columnDefinitions.push(
            {"data": null, "orderable": false, "defaultContent": "", "class": 'text-center'},
        );
    }

    let getPageDiscussionPost = function (requestData, renderFunction, link_api) {

        let params = {
            "page": (requestData.start / requestData.length) + 1,
            "size": requestData.length,
            "sortField": "createdTime",
            "sortDir": "desc",
            "keyword": $("#search_discussion_post").val(),
        };
        window.loader.show();
        jQuery.get(link_api, params, function (response) {
            let content = {
                "draw": requestData.draw,
                "recordsTotal": response.totalElements,
                "recordsFiltered": response.totalElements,
                "data": response.content
            };
            renderFunction(content);
            window.loader.hide();
        });
    };

    tableDiscussionPost = $("#discussion_post_table").DataTable({
        "language": {
            "url": "/libs/new_data_table/js/vie.json"
        },
        "lengthMenu": [
            [10, 20, 50],
            [10, 20, 50]
        ],

        "searching": false,
        rowId: 'id',
        "ordering": true,
        "pagingType": "full_numbers",
        "serverSide": true,
        "columns": columnDefinitions,
        "ajax": function (data, callback) {
            getPageDiscussionPost(data, callback, "/api/admin/discussion_post/getPage");
        },
        columnDefs: [
            {
                "render": function (data) {
                    return `<div>${data.nameCreator} (${data.nameRoleCreator})</div>`;
                },
                "targets": 3
            },
            {
                "render": function (closingDeadline) {
                    if (closingDeadline != null) {
                        return `<span style="font-weight: bold">${closingDeadline}</span>`;
                    } else {
                        return "";
                    }
                },
                "targets": 4
            },
            {
                "render": function (data) {
                    let listRole = data.listRole;
                    let result = `<ul style="list-style: disc; text-align: left; padding-left: 12px;">`;
                    listRole.forEach(function (role){
                        result += `<li>${role.name}</li>`;
                    })
                    result += `</ul>`;

                    return result;
                },
                "targets": 5
            },
            {
                "render": function (data) {
                    return `<a href="/admin/discussion_post/${data.seo}" id="detail_comment_post_${data.id}" class="btn-show-content">Chi tiết</a>`;
                },
                "targets": 6
            },
            {
                "render": function (isApproved) {
                    if (isApproved) {
                        return `<div style="background-color: blue; padding: 4px 0px; border-radius: 4px; color: white">Đã được duyệt</div>`
                    } else {
                        return `<div style="background-color: yellow; padding: 4px 0px; border-radius: 4px">Chưa được duyệt</div>`
                    }
                },
                "targets": 8
            },
            {
                "render": function (data) {
                    let textBtn = data.approved ? 'Chi tiết' : 'Phê duyệt';
                    return `<button type="button" data-toggle="modal" data-target="#modal_add_discussion_post" id="btn_detail_${data.id}" class="btn btn-sm btn-primary detail-discussion-post">${textBtn}</button>
                            <button style="margin-left: 10px" data-toggle="modal" id="btn_delete_${data.id}" data-target="#modal_delete" class="btn btn-sm btn-danger delete-discussion-post">Xóa</button>`;
                },
                "targets": isManagePost ? 9 : []
            },
        ],

    });

    $(document).on("click", "#btn_search", function () {
        tableDiscussionPost.ajax.reload();
    });

    $("#search_discussion_post").on("keypress", function (e){
        if (e.keyCode === 13) {
            tableDiscussionPost.ajax.reload();
        }
    })

})