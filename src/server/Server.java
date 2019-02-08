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
	SocketChannel clientSC;
//	OutputStream os;
//	InputStream input;
//	ServerSocket serverSocket;
	ServerSocketChannel serverSC;
//	String [] word;
	public Selector channelSelector;
	HashMap<String, Room> roomList = new HashMap<String, Room>();
	
	public Server() throws BadRequestException, IOException {
		serverSC = ServerSocketChannel.open().bind(new InetSocketAddress(8080));
		channelSelector = Selector.open();
		serverSC.configureBlocking(false);
		serverSC.register(channelSelector, SelectionKey.OP_ACCEPT);
	}
	
	public void Serve () throws BadRequestException, IOException {
		while (true) {
			channelSelector.select();
			Set<SelectionKey> uniqueSelectionKeys = channelSelector.selectedKeys();
			Iterator<SelectionKey> keyIterator = uniqueSelectionKeys.iterator();
			while(keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();
				if(key.isAcceptable()) {
					keyIterator.remove();
					clientSC = serverSC.accept();
					ThreadRunnable newThreadRunnable = new ThreadRunnable(clientSC, this);
					Thread thread = new Thread(newThreadRunnable);
					thread.start();
					}
				}
			}
		}
	
	public Room getRoom(String roomName) {
		Room newRoom;
		if(roomList.containsKey(roomName)) {
			newRoom = roomList.get(roomName);
		}
		else {
			newRoom = new Room();
			roomList.put(roomName, newRoom);
		}
		return newRoom;
	}
}