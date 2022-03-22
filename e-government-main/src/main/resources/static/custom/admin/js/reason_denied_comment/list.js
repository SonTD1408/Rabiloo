let tableReasonDenied;
$(document).ready(function () {
    let columnDefinitions = [
        {"data": "content", "orderable": false, "defaultContent": "", "class": 'text-center'},
    ];

    if (isManageReasonDeniedComment) {
        columnDefinitions.push(
            {"data": null, "orderable": false, "defaultContent": "", "class": 'text-center'},
        )
    }

    let getPageReasonDenied = function (requestData, renderFunction, link_api) {

        let params = {
            "page": (requestData.start / requestData.length) + 1,
            "size": requestData.length,
            "sortField": "id",
            "sortDir": "asc",
            "keyword": $("#search_reason_denied").val(),
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

    tableReasonDenied = $("#reason_denied_table").DataTable({
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
            getPageReasonDenied(data, callback, "/api/admin/reason_denied_comment/getPage");
        },
        columnDefs: [
            {
                "render": function (data) {
                    return `<button type="button" data-toggle="modal" data-target="#modal_add_reason_denied" id="btn_detail_${data.id}" class="btn btn-sm btn-primary detail-reason_denied">Chi tiết</button>
                            <button style="margin-left: 10px" data-toggle="modal" id="btn_delete_${data.id}" data-target="#modal_delete" class="btn btn-sm btn-danger delete-reason_denied">Xóa</button>`;
                },
                "targets": isManageReasonDeniedComment ? 1 : [],
            },
        ]
    });

    $(document).on("click", "#btn_search", function () {
        tableReasonDenied.ajax.reload();
    });

    $("#search_reason_denied").on("keypress", function (e) {
        if (e.keyCode === 13) {
            tableReasonDenied.ajax.reload();
        }
    })

})