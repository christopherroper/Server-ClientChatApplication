package serverPractice;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;

public class ThreadRunnable implements Runnable {
	private SocketChannel clientSC;
	private Server myServer;
	
	public ThreadRunnable (SocketChannel socketChannelPassed, Server serverPassed) {
		clientSC = socketChannelPassed;
		myServer = serverPassed;
	}
	
	@Override
	public void run() {
		HTTPRequest request;
		File pathToFile;
		String hash;
		WebSocketResponse connection;
		try {
			request = new HTTPRequest(clientSC);
			hash = request.getResponseHash();
			pathToFile = request.getFile();

			if (hash == null) {
				new HTTPResponse(pathToFile, clientSC);
			}
			else {
				new WebSocketResponse(hash, clientSC, myServer);
			}
		} catch (BadRequestException e) {
			System.out.println("bad request");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			clientSC.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
