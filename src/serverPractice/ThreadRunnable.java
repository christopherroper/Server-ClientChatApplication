package serverPractice;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;

public class ThreadRunnable implements Runnable {
	private SocketChannel sc;
	private Server serverMember;
	public WebSocketResponse connection;
	public ThreadRunnable (SocketChannel socketChannel, Server serverPassed) {
		this.sc = socketChannel;
		this.serverMember = serverPassed;
	}
	@Override
	public void run() {
		HTTPRequest request = null;
		File pathToFile = null;
		String hash = null;
		try {
			request = new HTTPRequest(sc);
			hash = request.getResponseHash();
			pathToFile = request.getFile();

			if (hash==null) {
				new HTTPResponse(pathToFile,sc);
			}
			else {
				connection = new WebSocketResponse(hash,sc, serverMember);
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
			sc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
