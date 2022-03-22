$(document).ready(function (){
    let newVue = new Vue({
        el: '#car-body',
        data: {
            listCar: [],
            listType: [],
            listCompany: []
        },
        methods: {
            showAddNewForm(){
                $('.add-new').addClass('show-add-new');


            },
            padLeadingZeros(num,size){
                var s= num+"";
                while (s.length <size) s="0"+s;
                return s;
            },
            saveAddNewCar(){
                let self = this;
                var seatFormLiChecked = document.querySelectorAll('.seat-form-li.checked li');
                var emptySpot = self.listType[$('select[name="type"]').val()-1].type - seatFormLiChecked.length;
                let data={
                    companyId: $('select[name="companyName"]').val(),
                    typeId: $('select[name="type"]').val(),
                    emptySpot: emptySpot,
                    licensePlate: $('input[name="license-plate"]').val(),
                    from: $('input[name="from"]').val(),
                    to: $('input[name="to"]').val(),
                    cost: $('input[name="cost"]').val(),
                    date: $('input[name="date"]').val(),
                    seatBooked: []
                }
                for (let i=1;i<=self.listType[data.typeId-1].type;i++){
                    data.seatBooked[i]="false";
                }
                for(let i=0;i<seatFormLiChecked.length;i++){
                    let value = seatFormLiChecked[i].innerHTML;
                    data.seatBooked[parseInt(value)]="true"
                }
                $.ajax({
                    type: "POST",
                    url: "/api/admin/carManagement/addCar",
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    beforeSend: function (){
                        window.loader.show()
                    },
                    success:function (response){
                        window.loader.hide();
                        $('.add-new').removeClass('show-add-new');
                        window.alert.show("success", "Add car success!", 2000);
                    }
                })
            },
            getType(){
                let self = this;
                $.ajax({
                    type: "POST",
                    url: "/api/admin/carManagement/getType",
                    success: function (response){
                        self.listType = response;
                    }
                })
            },
            getCompany(){
                let self =this;
                $.ajax({
                    type: "POST",
                    url: "/api/admin/carManagement/getCompany",
                    success: function (response){
                        self.listCompany = response;
                    }
                })
            },
            indexLoad(){
                this.getType();
                this.getCompany();
            }
        },
        mounted() {
            const addNew = document.querySelector('.add-new');
            const addNewCar = document.querySelector('.add-new-car');
            const seatFormLi = document.querySelectorAll('.seat-form-li');
            function hideDetailPage(){
                addNew.classList.remove('show-add-new');
            }
            addNew.addEventListener('click',hideDetailPage);
            addNewCar.addEventListener('click',function (even){
                even.stopPropagation();
            });

            // checked select li
            for (let i =0; i< seatFormLi.length; i++){
                if (i==0){
                    seatFormLi[i].classList.add("checked")
                }else {
                    seatFormLi[i].addEventListener('click', function (even) {
                        seatFormLi[i].classList.toggle("checked")
                    })
                }
            }

            //load TYPE OF CAR
            this.indexLoad();
        }
    })
})