$(document).ready(function () {
    var stompClient = null;

    function connect() {
        var socket = new SockJS('/newNotification');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame) {
            stompClient.subscribe('/notification/admin/newNotification', function(message){
                refreshMessages(JSON.parse(JSON.parse(message.body).content));
            });
        });
    }

    function disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
    }

    function sendMessage() {
        $container = $('.media-list');
        $container[0].scrollTop = $container[0].scrollHeight;
        var message = $("#messageText").val();
        var author = $.cookie("realtime-chat-nickname");

        stompClient.send("/app/newMessage", {}, JSON.stringify({ 'text': message, 'author': author}));

        $("#messageText").val("")
        $container.animate({ scrollTop: $container[0].scrollHeight }, "slow");

    }




})