let tableUser;
$(document).ready(function () {
    let typeActionableFilter = "all";

    let columnDefinitions = [
        {"data": "name", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "email", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "birthday", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "gender", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "phone", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "address", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "roles", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "actionableEntity", "orderable": false, "defaultContent": "", "class": 'text-left'},
        {"data": null, "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": null, "orderable": false, "defaultContent": "", "class": 'text-center'},
    ];

    let getPageUser = function (requestData, renderFunction, link_api) {

        let params = {
            "page": (requestData.start / requestData.length) + 1,
            "size": requestData.length,
            "sortField": "createdTime",
            "sortDir": "desc",
            "keyword": $("#search_user").val(),
            "typeActionableFilter": typeActionableFilter,
        };
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

    tableUser = $("#user_table").DataTable({
        "language": {
            "url": "/libs/new_data_table/js/vie.json"
        },
        "lengthMenu": [
            [10, 20, 50],
            [10, 20, 50]
        ],

        "searching": false,
        rowId: 'id',
        "ordering": false,
        "pagingType": "full_numbers",
        "serverSide": true,
        "columns": columnDefinitions,
        "ajax": function (data, callback) {
            getPageUser(data, callback, "/api/admin/user/getPage");
        },
        columnDefs: [
            {
                "render": function (birthday) {
                    if (!birthday) {
                        return "";
                    }
                    return moment(birthday).format("DD//MM/YYYY");
                },
                "targets": 2
            },
            {
                "render": function (gender) {
                    if (gender == 1) {
                        return "Nam";
                    } else if (gender == 2) {
                        return "Nữ";
                    } else if (gender == 9) {
                        return "Khác";
                    } else {
                        return "";
                    }
                },
                "targets": 3
            },
            {
                "render": function (roles) {
                    return `<div>${roles[0].name}</div>`;
                },
                "targets": 6
            },
            {
                "render": function (actionableEntity) {
                    let result = `<ul style="list-style: disc">`;
                    if (actionableEntity.managePost) {
                        result += `<li>Thêm, sửa, xóa bài viết</li>`;
                    }
                    if (actionableEntity.manageCommentPost) {
                        result += `<li>Phê duyệt bình luận bài viết</li>`;
                    }
                    if (actionableEntity.manageConclusionPost) {
                        result += `<li>Kết luận bài viết</li>`;
                    }
                    if (actionableEntity.manageField) {
                        result += `<li>Quản lý lĩnh vực</li>`;
                    }
                    if (actionableEntity.manageReasonDeniedComment) {
                        result += `<li>Quản lý lý do từ chối bình luận</li>`;
                    }
                    if (actionableEntity.manageNews) {
                        result += `<li>Quản lý tin tức</li>`;
                    }

                    result += `</ul>`;

                    return result;
                },
                "targets": 7
            },
            {
                "render": function (data) {
                    let roleUser = data.roles[0].name;
                    let classDisabled = roleUser === 'GOVERNMENT' ? 'disabled' : '';

                    if (data.status === 1) {
                        return `<div class="wrap-switch"> <label class="switch">
										  <input ${classDisabled} id="active-user-${data.id}" class="change-active-user ${classDisabled}" type="checkbox" checked>
										  <span class="slider round"></span>
										</label> </div>`;
                    } else {
                        return `<div class="wrap-switch"> <label class="switch">
										  <input ${classDisabled} id="active-user-${data.id}" class="change-active-user ${classDisabled}" type="checkbox">
										  <span class="slider round"></span>
										</label> </div>`;
                    }
                },
                "targets": 8
            },
            {
                "render": function (data) {
                    return `<button type="button" data-toggle="modal" data-target="#modal_add_user" id="btn_detail_${data.id}" class="btn btn-sm btn-primary detail-user">Chi tiết</button>`;
                },
                "targets": 9
            },
        ]
    });

    $(document).on("click", "#btn_search", function () {
        tableUser.ajax.reload();
    });

    $("#search_user").on("keypress", function (e) {
        if (e.keyCode === 13) {
            tableUser.ajax.reload();
        }
    })

    $(document).on("change", "#actionable", function () {
        typeActionableFilter = $(this).val();
        tableUser.ajax.reload();
    })

})