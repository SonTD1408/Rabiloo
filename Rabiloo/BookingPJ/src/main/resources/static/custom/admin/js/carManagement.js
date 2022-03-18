let newVue = new Vue({
    el: '#car-body',
    data: {
        listCar: []
    },
    methods: {
        showAddNewForm(){
            console.log("hehe")
            $('.add-new').addClass('show-add-new');
        }
    },
    mounted() {
        const addNew = document.querySelector('.addNew');
        const addNewCar = document.querySelector('.add-new-car');
        function hideDetailPage(){
            addNew.classList.remove('show-add-new');
        }
        addNew.addEventListener('click',hideDetailPage);
        addNewCar.addEventListener('click',function (even){
            even.stopPropagation();
        })
    }
})