var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#info-hist").show();
    } else {
        $("#info-hist").hide();
    }
    $("#info").html("");
}

function connect() {
    var socket = new SockJS('/connect');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        stompClient.subscribe('/user/queue/info', function (response) {
            addInfo(response.body);
         });
        stompClient.subscribe('/user/queue/game-data', function (response) {
            let game = JSON.parse(response.body)
            refreshScreen(game)
            disableMove(game)
            checkWinner(game)

        });

        $( "#name" ).attr("disabled", false)
        $( "#send" ).attr("disabled", false)
    });
}

function refreshScreen(game) {
    $("#game-id").val(game.id)
    $("#game-player1").val(game.player1.name)
    $("#game-player2").val(game.player2.name)
    $("#game-currentPlayer").val(game.currentPlayer.name)
    $("#game-number").val(game.number)

    if(game.started) {
        $("#startGame").attr("disabled", true)
        $("#number").attr("disabled", true)
    }
}

function disableMove(game) {
    if( $("#name").val() !==  game.currentPlayer.name){
        $( "#move" ).attr("disabled", true)
    } else {
        $( "#move" ).attr("disabled", false)
    }
}

function checkWinner(game) {
    if(game.end) {
        alert("Game over! The winner is: " + game.currentPlayer.name )
        $("#info").empty()
        disconnect()
    }
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/new-game", {}, $("#name").val());
     $("#name").attr("disabled", true)
     $("#send").attr("disabled", true)
}

function addInfo(message) {
    $("#info").append("<tr><td>" + message + "</td></tr>");
}

function nextMove() {
    if( $('#addedValue').val() != ''){
        stompClient.send("/app/next-move", {}, JSON.stringify({'gameId': $("#game-id").val(), 'addedValue' : $('#addedValue').val() }));
    } else {
        stompClient.send("/app/next-move", {}, JSON.stringify({'gameId': $("#game-id").val() }));
    }
}

function startGame() {
    stompClient.send("/app/start-game-move", {}, JSON.stringify({'gameId': $("#game-id").val(), 'number': $("#number").val() }  ) );
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
    $( "#move" ).click(function() { nextMove(); });
    $( "#startGame" ).click(function() { startGame(); });
});