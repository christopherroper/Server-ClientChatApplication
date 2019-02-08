package serverPractice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Set;

public class WebSocketResponse {
	private DataInputStream webSocketInput; //used in multiple
	private SocketChannel memberSC;
	private Server serverMember;
	private Pipe newPipe;
	//only care about channels when website sends us something or client sends something (echo server)
	//while socket(open)
	//string message = receivemessage(if from websocket), room.post
	//else read from room, sendmessage(message)

	//**need to know which source of data sent stuff first

	// 1. change server to server socket
	// 2. modify connection so it stores socket channel instead of socket

	//do this in Server
	//convert server socket into server socket channel
	//call bind method to bind socket to port
	//Selector sel = selector.open();
	//serversocketChannel ssc;
	//ssc.register(sel,Selectorkey.op_accept)
	//ssc.configureBlocking(false) 
	//now ready to wait.
	//sel.select(); (wait for at least 1 event)
	//set<selectorkey> = sel.selectedkeys()
	//sel.selectedkeys() returns list/set of all things that have happened
	//iterator<selectedkey> it = keys.iterator();
	//loop while (it.hasnext())
	//selectionkey key = it.next();
	// key is a description of what happened, like an event
	// if(key.isacceptable())... it.remove()
	// ssc.accept() // gives back socket channel
	//new connection (sc.socket())
	//listen for "join room"
	//need a selector to listen for:
	//wait for messages from client (socket channel) OR message from the room (pipe)

	//key //socket ready to read/ hook it up with ssc. ssc.register(sel.selectionkey,opaccept)
	//now tell selector to unregister channel. Selector, don't inform when socket is ready to read. key.cancel()
	//then set channel to block. myChannel.configureBlocking(true);
	//use socket as byte
	//myChannel.socket().inputStream().read() to read bytes
	//once done reading, re register
	//first myChannel.configureblocking(false) // now not allowed to call read which would block
	//selector.selectnow(); //who knows what this does
	//then can reregister; myChannel.register(seelctor, selectionkey.opaccept)

	//add if statement, put stuff into blocking mode
	//Pipe newPipe - allows communication between room and connection
	//pipe.open()
	//write into: sink
	//write into: source
	//if you get a message from the room, read the message out of the pipe
	//send a message to the pipe
	//to notify when there are bytes in the pipe: register pipe with selector (wake me up if pipe gets some bytes)
	//ObjectInputStream (take an object and turn it into a string of bytes, or vice versa)
	//ObjectOutputStream
	//myClass implements Serializable
	//public synchronized void postMessage
	public WebSocketResponse(String hash, SocketChannel sc, Server serverPassed) throws NoSuchAlgorithmException, BadRequestException, IOException, ClassNotFoundException{
		this.serverMember = serverPassed;
		this.memberSC = sc;
		handshake(hash,sc); //
		newPipe = Pipe.open();
		String roomRequested = readMessage(); //reads request to join room
		System.out.println(roomRequested); //test to print room request
		String[] roomRequest = roomRequested.split(" "); //splits room request from join/room name
		String roomName = roomRequest[1]; //set
		//String username = roomRequest[2];
		Room newRoom = serverPassed.getRoom(roomName);
		newRoom.addUser(this);
		//newRoom.sendUsers(this);
		Selector sel = Selector.open();
		newPipe.source().configureBlocking(false);
		sc.configureBlocking(false);
		sc.register(sel, SelectionKey.OP_READ);
		newPipe.source().register(sel, SelectionKey.OP_READ);

		while (!sc.socket().isClosed()) {
			sel.select();
			Set<SelectionKey> keys = sel.selectedKeys();
			Iterator<SelectionKey> it = keys.iterator();
			while(it.hasNext()) {
				SelectionKey key = it.next();
				it.remove();
				if(key.isReadable()) {
					if(key.channel() == sc) {
						key.cancel();
						sc.configureBlocking(true);
						String messageIn = readMessage();
						Messages newMessage = new Messages(messageIn);
						System.out.println(messageIn);
						newRoom.postMessage(newMessage);
						sc.configureBlocking(false);
						sel.selectNow();
						sc.register(sel, SelectionKey.OP_READ);
					}
					if (key.channel() == newPipe.source()) {
						key.cancel();
						sc.keyFor(sel).cancel();
						sc.configureBlocking(true);
						newPipe.source().configureBlocking(true);
						ObjectInputStream oPipeStream = new ObjectInputStream(Channels.newInputStream(newPipe.source()));
						Messages pipeMessage = (Messages) oPipeStream.readObject(); 
						String JSONmessage = pipeMessage.toJSON();
						sendMessage(JSONmessage);
						newPipe.source().configureBlocking(false);
						sel.selectNow();
						newPipe.source().register(sel, SelectionKey.OP_READ);
						sc.configureBlocking(false);
						sel.selectNow();
						sc.register(sel, SelectionKey.OP_READ);
					}
				}
			}	
		}
		//newRoom.removeUser(this);
	}
	public void handshake(String hashString, SocketChannel sc) {
		OutputStream osHandshake = null;
		try {
			osHandshake = sc.socket().getOutputStream();
		} catch (IOException e) {
			System.out.println("bad OutputStream");
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(osHandshake);
		pw.print("HTTP/1.1 101 Switching Protocols\r\n");
		pw.print("Upgrade: websocket\r\n");
		pw.print("Connection: Upgrade\r\n");	
		pw.print("Sec-WebSocket-Accept: " + hashString + " \r\n");
		pw.print("\r\n");
		pw.flush();	
	}

	public String readMessage() throws IOException {
		webSocketInput = new DataInputStream (memberSC.socket().getInputStream());
		long messageLengthBytes=0;
		byte[] messageHeader = new byte[2];
		byte [] messagePackage;
		String decodedMessageString = null;
		int opCode;

		webSocketInput.readFully(messageHeader);
		byte headerByte0 = messageHeader[0];
		byte headerByte1 = messageHeader[1];	
		if((headerByte0>>4  &0xF) != 8) {
			System.out.println("header error");
			System.exit(0);
		}
		if((headerByte0 &0x0F) != 1) {
			System.out.println("not message");
			System.exit(0);
			opCode = 0; //close
		}
		else {opCode = 1;}

		if((headerByte1>>7 &0x01) == 1) {
			System.out.println("join message: ");
			if ((headerByte1 &0x7F) < 126) {
				messageLengthBytes = (headerByte1 &0x7F);
				messagePackage = new byte[0];
			}
			else if ((headerByte1 &0x7F) == 126) {
				messagePackage = new byte[2];
				webSocketInput.readFully(messagePackage);
				messageLengthBytes = new BigInteger(messagePackage).intValue();
			}
			else {
				messagePackage = new byte[8];
				messageLengthBytes = webSocketInput.readLong();
			}
			byte[] maskKey = new byte[4];
			webSocketInput.readFully(maskKey);
			byte [] actualMessage = new byte [(int) (messageLengthBytes)];
			webSocketInput.readFully(actualMessage);
			byte [] decodedMessage = new byte[(int) (messageLengthBytes)];
			for (int i = 0; i < actualMessage.length; i++) {
				decodedMessage[i] = (actualMessage[i] ^= maskKey[i % 4]);
			}
			decodedMessageString = new String(decodedMessage);
		}	
		return (decodedMessageString);
	}

	public void sendMessage(String messageString) throws IOException {
		System.out.println("printing received message: " + messageString);
		DataOutputStream outputWeb = new DataOutputStream(memberSC.socket().getOutputStream());
		byte[] messageBytes;
		byte[] responseHeader = new byte[2];
		responseHeader[0]= (byte) 0x81;
		responseHeader[1]= (byte) messageString.length();
		if (messageString.length()<126) {
			messageBytes = new byte[0];
		}
		else if(messageString.length() == 126){
			messageBytes = new byte[2];
		}
		else messageBytes = new byte[8];
		outputWeb.write(responseHeader);
		outputWeb.write(messageBytes);
		outputWeb.writeBytes(messageString);
		outputWeb.flush();
		System.out.println("sending to website:" + messageString);
	}
	public void writeToPipe(Messages message) throws IOException {
		ObjectOutputStream pipeObjOS = new ObjectOutputStream(Channels.newOutputStream(newPipe.sink()));
		pipeObjOS.writeObject(message);
	}
	public void writeUsersToPipe(String users) throws IOException {
		ObjectOutputStream pipeObjOS = new ObjectOutputStream(Channels.newOutputStream(newPipe.sink()));
		pipeObjOS.writeObject(users);
	}
}