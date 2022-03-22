$(document).ready(function () {
    $(document).on("click", ".plus-font-size", function () {
        let sizeCurrent = Number($(".content-news").css("font-size").replace("px", ""));
        $(".content-news").css("font-size", sizeCurrent + 1 + "px");
    })

    $(document).on("click", ".minus-font-size", function () {
        let sizeCurrent = Number($(".content-news").css("font-size").replace("px", ""));
        if (sizeCurrent == 0) {
            return;
        }
        $(".content-news").css("font-size", sizeCurrent - 1 + "px");
    })

    $(document).on("click", ".wrap-icon-print", function () {
        // let href = "/api/news/downloadNews/" + 1
        // window.open(href, '_blank');

        let preHtml = "<html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'><head><meta charset='utf-8'<title>Export HTML to Doc</title></head>";
        let postHtml = "</body></html>";
        let html = preHtml + document.getElementById("full-news").innerHTML + postHtml;

        let blob = new Blob(['\ufeff', html], {
            type: 'application/msword'
        })

        let url = 'data:application/vnd.ms-word;charset=utf-8,' + encodeURIComponent(html);
        let fileName = "news.doc";

        let downloadLink = document.createElement("a");
        document.body.append(downloadLink);
        if (navigator.msSaveOrOpenBlob) {
            navigator.msSaveOrOpenBlob(blob, fileName);
        } else {
            downloadLink.href = url;
            downloadLink.download = fileName;
            downloadLink.click();
        }

        document.body.removeChild(downloadLink);
    })

    let weather = new Vue({
        el: "#weather",
        data: {
            citySelected: "Hà Nội",
            temperature: 0,
            listCity: [],
            listCityAfterFilter: [],
            keywordSearchCity: "",
        },
        watch: {
            citySelected() {
                this.getTemperature();
            },
            keywordSearchCity() {
                this.searchCity();
            },
        },
        methods: {
            getListCity() {
                let self = this;
                $.ajax({
                    type: "GET",
                    url: "/api/city/list",
                    success: function (response) {
                        if (response.status.code === 1000) {
                            self.listCity = response.data;
                            self.listCityAfterFilter = self.listCity;
                        }
                    }
                })
            },
            searchCity() {
                this.listCityAfterFilter = this.listCity.filter(city => city.name.includes(this.keywordSearchCity));
            },
            getTemperature() {
                let self = this;
                $.ajax({
                    type: "GET",
                    url: "https://api.openweathermap.org/data/2.5/weather?q=" + self.citySelected + "&appid=1676f1145d3e35c12e69c4dcd47b4ab8",
                    success: function (response) {
                        self.temperature = Math.round(response.main.temp - 273.15);
                    },
                    error: function (err) {
                        self.temperature = 0;
                    }
                })
            },
            resetKeywordSearch() {
                this.keywordSearchCity = "";
            },
        },
        mounted() {
            let self = this;
            self.getListCity();
            self.getTemperature();

            $(".list-city").slimScroll({
                height: '330px',
                wheelStep: 10,
                touchScrollStep: 500
            });

            $(document).on("click", ".drop-down-list-city", function () {
                self.resetKeywordSearch();
                $(".wrap-list-city").toggleClass("hidden");
            })

            $(document).on("click", ".city", function () {
                self.citySelected = $(this).attr("value");
                $(".wrap-list-city").toggleClass("hidden");
            })

        }
    })


})