"use strict"

let body = document.getElementsByTagName ('body')[0];
let username = document.getElementById("username");
let chatMessage = document.getElementById('message');
let mySocket;
let loginPage = document.getElementById('login');
loginPage.style.visibility = 'visible';
let chatPage = document.getElementById('chatRoom');
chatPage.style.visibility = 'hidden';
let userList =[];
let sendMessageButton = document.getElementById('send');
let userText;
let users = document.getElementById('users1');
let timestamp;
let date;

// let userListFunction = function(){
// // let userSpace = document.createElement('l1');
// users.innerHTML = 'Users: ';
//   for(let i =0; i<userList.length; i++){
//     if (userList[i]==userList[i+1]) {
//       userList.pop(i);
//     }
//   }
//   for(let j=0; j<userList.length; j++){
//     userText = document.createTextNode(userList[j]);
//     users.appendChild(userText);
//     let newBreak=document.createElement('br');
//     users.appendChild(newBreak);
//   }
//   //userSpace.appendChild(users);
//   // body.appendChild(users);
// }
let onMessage = function(event) {

  console.log(event);
  let newMessageSpace = document.createElement('p')
  let messageJSON = JSON.parse(event.data);
  //userList.push(messageJSON.user);
  let outputText = document.createTextNode(messageJSON.user + ': ' + messageJSON.message + '\t' + date);
  let messageHistory = document.getElementById ('messages');
  let users = document.getElementById ('users1');
//userListFunction();
console.log(messageJSON);
console.log(outputText);
  newMessageSpace.appendChild(outputText);
  messageHistory.appendChild(newMessageSpace);
  window.scrollTo(0,document.body.scrollHeight);
}

let onLoad = function(){
  username.focus();
  username.select();
}

let onOpen = function(event){
  console.log(event);
  console.log('connection established')
  let room = document.getElementById("room");
  let message = "join " + room.value + " " + username.value;
  mySocket.send(message);
  loginPage.style.visibility = 'hidden';
  chatPage.style.visibility = 'visible';
  chatMessage.focus();
  chatMessage.select();

  let chatRoomText = document.createTextNode(room.value);
  let roomSpace = document.getElementById("roomSelected");
  roomSpace.appendChild(chatRoomText);
}

let joinButton = function() {
  let pfail = document.getElementById ('fail');

  window.scrollTo(0,document.body.scrollHeight);
// if (username.value == ''){
//   pfail.innerHTML = 'please enter a valid username';
//   }
// else {
  console.log(location.host);
  let url = "ws://" + location.host;
  mySocket = new WebSocket(url);
  // let message = "join " + room.value;
  mySocket.onopen = onOpen;
  mySocket.onmessage = onMessage;
  //}
}

let sendButton = function(event) {
  event.preventDefault();
  console.log("submitted");
  timestamp = Date.now();
  date = Date(Date.parse(timestamp));
  mySocket.onmessage = onMessage;
  let newMessage = username.value + ' ' + chatMessage.value;
  mySocket.send(newMessage);
  console.log (newMessage);

  chatMessage.value = "";
  chatMessage.focus();
  chatMessage.select();
  return false;
}

window.onload = onLoad;
loginButton.addEventListener ("click", joinButton);
