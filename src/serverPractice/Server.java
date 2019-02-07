package serverPractice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;

public class Server {
	//map from string to room
	SocketChannel socketChannel;
	OutputStream os;
	InputStream input;
	ServerSocket serverSocket;
	ServerSocketChannel serverSocketChannel;
	Scanner s;
	String [] word;
	public Selector select;
	int threadCount;
	HashMap<String, Room> roomList = new HashMap<String, Room>();
	
	public Server() throws BadRequestException, IOException {
		serverSocketChannel = null;
		serverSocketChannel = ServerSocketChannel.open().bind(new InetSocketAddress(8080));
		select = Selector.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(select, SelectionKey.OP_ACCEPT);
	}
	
	public void Serve () throws BadRequestException, IOException {
		while (true) {
		select.select();
		Set<SelectionKey> keys = select.selectedKeys();
		Iterator<SelectionKey> it = keys.iterator();
			while(it.hasNext()) {
				SelectionKey key = it.next();
				if(key.isAcceptable()) {
					it.remove();
				socketChannel = serverSocketChannel.accept();
				ThreadRunnable t = new ThreadRunnable(socketChannel,this);
				Thread thread = new Thread(t);
				thread.start();
					}
				}
			}
		}
	
	public Room getRoom(String roomName) {
		Room newRoom;
		if (roomList.containsKey(roomName)) {
			newRoom = roomList.get(roomName);
		}
		else {newRoom = new Room();
		roomList.put(roomName, newRoom);
		}
		return newRoom;
	}
}
//add sendMessageO)