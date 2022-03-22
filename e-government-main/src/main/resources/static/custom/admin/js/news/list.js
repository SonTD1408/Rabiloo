let tableNews;
$(document).ready(function () {
    let columnDefinitions = [
        {"data": "fieldName", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "subFieldName", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "title", "orderable": false, "defaultContent": "", "class": 'text-center'},
        {"data": "createdTime", "orderable": false, "defaultContent": "", "class": 'text-center'},
    ];

    if (isManageNews) {
        columnDefinitions.push(
            {"data": null, "orderable": false, "defaultContent": "", "class": 'text-center'},
        )
    }

    let getPageNews = function (requestData, renderFunction, link_api) {

        let params = {
            "page": (requestData.start / requestData.length) + 1,
            "size": requestData.length,
            "sortField": "id",
            "sortDir": "desc",
            "keyword": $("#search_news").val(),
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

    tableNews = $("#news_table").DataTable({
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
            getPageNews(data, callback, "/api/admin/news/getPage");
        },
        columnDefs: [
            {
                "render": function (data) {
                    return `<button type="button" data-toggle="modal" data-target="#modal_add_news" id="btn_detail_${data.id}" class="btn btn-sm btn-primary detail-news">Chi tiết</button>
                            <button style="margin-left: 10px" data-toggle="modal" id="btn_delete_${data.id}" data-target="#modal_delete" class="btn btn-sm btn-danger delete-news">Xóa</button>`;
                },
                "targets": isManageNews ? 4 : [],
            },
        ]
    });

    $(document).on("click", "#btn_search", function () {
        tableNews.ajax.reload();
    });

    $("#search_news").on("keypress", function (e){
        if (e.keyCode === 13) {
            tableNews.ajax.reload();
        }
    })

})