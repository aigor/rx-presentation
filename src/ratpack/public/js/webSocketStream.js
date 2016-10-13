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
        if(event.data === undefined){
            setTimeout(startStreaming, 200);
            return;
        }

        writeResponse(event.data);
    };

    webSocket.onmessage = function(event){
        writeResponse(event.data);
    };

    webSocket.onclose = function(event){
        console.log("WebSocket connection closed");
        setTimeout(openSocket, 1000);

    };

    webSocket.onerror = function (error) {
        console.log("Error happened: " + error);
    };
}

function closeSocket(){
    webSocket.close();
}

function writeResponse(respText){
    console.log("Received: " + respText);
    var resp = JSON.parse(respText);
    if (resp.type != undefined) {
        if (resp.type == "tweet"){
            $("#tweets-stream").prepend($("<li>").append(
                $("<blockquote>").append(
                    $("<p>").text(resp.text)
                ).append(
                    $("<a>").text("by @" + resp.author)
                )
            ));

        } else if (resp.type == "tweetStatisticsEvent") {
            $("#received-tweets").text(resp.receivedTweets)
        } else if (resp.type == "userWithTweet") {
            $("#popular-authors").prepend($("<li>").append(
                            $("<div>").attr("style", "float: left;")
                            .append(
                                $("<img>").attr("src", resp.profile.profile_image_url)
                            ))
                            .append(
                                $("<div>").attr("style", "float: left; margin-left: 1em;")
                                    .append($("<p>").attr("style", "font-weight: bold;").text(resp.profile.name + " (@" + resp.profile.screen_name + ")"))
                                    .append($("<p>").attr("style", "white-space: pre-wrap;")
                                        .text("Followers: " + resp.profile.followers_count
                                            + "\nFriends: " + resp.profile.friends_count
                                            + "\nTweets: " + resp.profile.statuses_count))
                            )
                            .append($("<div>").attr("style", "float: none; clear: both;"))
                            .append($("<hr/>"))
            );
        }
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