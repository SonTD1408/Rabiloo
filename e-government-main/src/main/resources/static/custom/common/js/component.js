$(document).ready(function () {

    window.loader = {
        show: function () {
            $('#loader').removeClass('d-none');
        },
        hide: function () {
            $('#loader').addClass('d-none');
        },
    }


    window.alert = {
        show: function (type, message, time) {
            if (type === "success") {
                $('#alert').removeClass('alert-danger');
                $('#alert').addClass('alert-success');
                $('#alert .success-icon').removeClass('d-none');
                $('#alert .error-icon').addClass('d-none');
            } else if (type === "error") {
                $('#alert').addClass('alert-danger');
                $('#alert').removeClass('alert-success');
                $('#alert .success-icon').addClass('d-none');
                $('#alert .error-icon').removeClass('d-none');
            }
            $("#message-alert").html(message);

            $("#alert").animate({
                top: "40px"
            }, "slow");

            setTimeout(function () {
                $("#alert").animate({
                    top: "-100px"
                }, "slow");
            }, time);
        }
    }
})


Vue.component("image-upload-component", {
    data() {
        return {
            hasImage: false,
        }
    },

    template: `<div style="cursor: pointer; width: 100px; height: 80px; border: 1px solid #ccc; position: relative;" id="upload-image">
					<input type="file" accept=".jpg, .png, .jpeg" class="input-image d-none"> 
					<i style="font-size: 40px;margin-top: 14px; margin-left: 30px;" class="fal fa-camera" v-bind:class="{hidden : hasImage}"></i>
					<span class="image-text-upload" style="font-size: 10px; display: flex; justify-content: center; margin-top: 8px" v-bind:class="{hidden : hasImage}">Upload image</span>
					<i style="position: absolute; top: 0; right: 0" class="fas fa-window-close" v-bind:class="{hidden : !hasImage}" v-on:click="removeImage($event)"></i>
					<img class="img-component" style="width: 100%; height: 100%; box-sizing: border-box;" alt="" src="" v-bind:class="{hidden : !hasImage}">
			   </div>`,

    mounted: function () {
        let self = this;

        $(".input-image").click(function (e) {
            e.stopPropagation();
        });

        $(this.$el).on("click", function () {
            $(this).find('input').trigger("click");
        })

        $(this.$el).find('input').on("change", function () {
            if (this.files.length === 0) {
                $(self.$el).find("img").attr("src", "");
                self.hasImage = false;
            } else {
                self.hasImage = true;
                self.displayImage(this.files[0]);
            }

        })


    },

    methods: {
        removeImage(e) {
            e.stopPropagation();
            this.hasImage = false;
            $(this.$el).find('img').get(0).src = "";
        },

        displayImage(image) {
            let fileReader = new FileReader();
            let $img = $(this.$el).find("img").get(0);

            fileReader.onload = function ($event) {
                $img.src = this.result;
            }

            fileReader.readAsDataURL(image);
        },

        getImageSelected() {
            if (this.hasImage) {
                return $(this.$el).find('input')[0].files[0];
            } else {
                return null;
            }
        },

        clear() {
            this.hasImage = false;
            $(this.$el).find('img').get(0).src = "";
        },
    }

})










