let tableField;
$(document).ready(function () {
    let columnDefinitions = [
        {"data": "name", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": null, "orderable": false, "defaultContent": "", "class": 'text-center'},
    ];

    if (isManageField) {
        columnDefinitions.push(
            {"data": null, "orderable": false, "defaultContent": "", "class": 'text-center'},
        )
    }

    let getPageField = function (requestData, renderFunction, link_api) {

        let params = {
            "page": (requestData.start / requestData.length) + 1,
            "size": requestData.length,
            "sortField": "id",
            "sortDir": "asc",
            "keyword": $("#search_field").val(),
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

    tableField = $("#field_table").DataTable({
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
            getPageField(data, callback, "/api/admin/field/getPage");
        },
        columnDefs: [
            {
                "render": function (data) {
                    let listSubField = data.listSubField;
                    let result = `<ul>`;
                    listSubField.forEach(function (subField){
                        result += `<li>${subField.name}</li>`;
                    })
                    result += `</ul>`;

                    return result;
                },
                "targets": 1,
            },
            {
                "render": function (data) {
                    return `<button type="button" data-toggle="modal" data-target="#modal_add_field" id="btn_detail_${data.id}" class="btn btn-sm btn-primary detail-field">Chi tiết</button>
                            <button style="margin-left: 10px" data-toggle="modal" id="btn_delete_${data.id}" data-target="#modal_delete" class="btn btn-sm btn-danger delete-field">Xóa</button>`;
                },
                "targets": isManageField ? 2 : [],
            },
        ]
    });

    $(document).on("click", "#btn_search", function () {
        tableField.ajax.reload();
    });

    $("#search_field").on("keypress", function (e){
        if (e.keyCode === 13) {
            tableField.ajax.reload();
        }
    })

})