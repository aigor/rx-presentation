var webSocket;
var messages = document.getElementById("messages");
var webSocketUrl = wsUrl("ws");

function wsUrl(s) {
    var l = window.location;
    return ((l.protocol === "https:") ? "wss://" : "ws://") + l.host + l.pathname + s;
}

function webSocketReady(){
    return webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED;
}

function openSocket(){
    // Ensures only one connection is open at a time
    if(webSocketReady()){
        writeResponse("WebSocket is already opened.");
        return;
    }
    // Create a new instance of the websocket
    webSocket = new WebSocket(webSocketUrl);

    /**
     * Binds functions to the listeners for the websocket.
     */
    webSocket.onopen = function(event){
        // For reasons I can't determine, onopen gets called twice
        // and the first time event.data is undefined.
        // Leave a comment if you know the answer.
        if(event.data === undefined)
            return;

        writeResponse(event.data);
    };

    webSocket.onmessage = function(event){
        writeResponse(event.data);
    };

    webSocket.onclose = function(event){
        console.log("WebSocket connection closed");
    };
}

function closeSocket(){
    webSocket.close();
}

function writeResponse(respText){
    console.log("Received: " + respText);
    var resp = JSON.parse(respText);
    if (resp.type != undefined && resp.type == "tweet"){
        $("#tweets-stream").prepend($("<li>").append(
            $("<blockquote>").append(
                $("<p>").text(resp.text)
            ).append(
                $("<a>").text("by @" + resp.author)
            )
        ));
    }
}

function startStreaming(){
    var searchText = $("input:text").val()
    if (searchText != undefined && searchText != "" && webSocketReady()){
        console.log("Sending request for tweets by keys: " + searchText)
        webSocket.send(JSON.stringify({q: searchText}));
    }
}

$( document ).ready(function() {
    console.log( "Page is ready, opening WebSocket with url: " + webSocketUrl);
    openSocket();
});