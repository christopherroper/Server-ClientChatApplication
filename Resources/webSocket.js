"use strict"
let url = "ws://" + location.host;
var connection = new WebSocket(url);
let body = document.getElementsByTagName ('body')[0];
let messageReceived;
connection.onopen = function () {
  console.log(this);
connection.send("join: room1");
}
connection.onerror = function() {
  console.log("SHIT");
}

connection.onmessage = function(event) {
  console.log(event)
  console.log("you did it");
  setTimeout(echo, 5000);
  messageReceived = document.createTextNode(event.data);
  body.appendChild(paragraph);
  paragraph.appendChild(messageReceived);
}
let list;
let paragraph = document.createElement('p');

let echo = function () {
  connection.send("echo is working");
}
