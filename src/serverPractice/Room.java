package serverPractice;

import java.io.IOException;
import java.nio.channels.Pipe;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class Room {
	public WebSocketResponse connection;
	ArrayList <Messages> messageList;
	ArrayList <WebSocketResponse> connectionsList;
	public Room() {
		connectionsList = new ArrayList <WebSocketResponse>();
		messageList = new ArrayList <Messages>();
	}
	public synchronized void postMessage(Messages message) throws IOException {
		messageList.add(message);
		for (WebSocketResponse a : connectionsList) {
		a.writeToPipe(message);
		}
	}
	public void addUser(WebSocketResponse connection) throws IOException {
		connectionsList.add(connection);
		for (Messages m: messageList) {
			connection.writeToPipe(m);
		}
	}
	public void removeUser(WebSocketResponse connection) {
		connectionsList.remove(connection);
	}
}
