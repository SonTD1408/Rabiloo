var configOneDate = function (idElement) {
    $("#" + idElement).daterangepicker({
        timePicker: true,
        singleDatePicker: true,
        autoApply: true,
        showDropdowns: true,
        "timePicker24Hour": true,
        locale: {
            format: 'YYYY/MM/DD HH:mm',
            cancelLabel: 'キャンセル',
            applyLabel: 'OK'
        }
    });
};

var configOneDateNotTime = function (idElement) {
    $("#" + idElement).daterangepicker({
        timePicker: false,
        singleDatePicker: true,
        autoApply: true,
        "timePicker24Hour": true,
        locale: {
            format: 'YYYY/MM/DD',
            cancelLabel: 'Đóng',
            applyLabel: 'OK'
        }
    });
};


var validateTimeOnHideDatePicker = function (idStartTimeElement, idEndTimeElement, callbackValidateFunction) {
    $("#" + idStartTimeElement + ",#" + idEndTimeElement).on('hide.daterangepicker', function () {
        callbackValidateFunction();
    });
};

var setStartDateForDatePicker = function (idElement) {
    var el = $("#" + idElement);
    var time = el.val();
    if (!time.includes("無期限")) {
        el.data('daterangepicker').setStartDate(el.val());
    }
};