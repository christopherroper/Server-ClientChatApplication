package serverPractice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

public class HTTPRequest {
	private File pathToFile;
	private Scanner s;
	private String [] requestArray;
	private HashMap<String, String> requestMap = new HashMap<String, String>();
	private String handshakeResponse;
	private MessageDigest testMD;
	private String hashed = null;
	private String encoded = null;
	public HTTPRequest (SocketChannel sc) throws BadRequestException, NoSuchAlgorithmException {
		InputStream input = null;
		try {
			input = sc.socket().getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//throw new BadRequestException();
		}
		// constructs new InputStream to take from socket
		s = new Scanner (input); // creates new Scanner  to capture inputs
		if(s.hasNext()) {
		String[] word = s.nextLine().split(" "); // creates new string array split after each word
		//System.out.println(word[0] + " " + word[2]);
		if (word.length < 3) {
			System.out.println("Error: Invalid Length");
			throw new BadRequestException();
		}
		if (!(word[0].equals("GET")) || !(word[2].equals("HTTP/1.1"))) {
			System.out.println("Error: Invalid Argument");
			throw new BadRequestException();
		}
		
		String path = word[1]; // gets element 1 from string array for path
		pathToFile = new File("Resources/" + path); // creates new File at path
		}
		while(true) {
		String requestWeb = s.nextLine();
		if (requestWeb.equals("")) break;
		requestArray = requestWeb.split(": ");
		//System.out.println(requestArray[0] + " " + requestArray[1]);
		requestMap.put(requestArray[0],requestArray[1]);
		}

		if (requestMap.containsKey("Sec-WebSocket-Key")) {
			String append = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
			handshakeResponse = requestMap.get("Sec-WebSocket-Key") + append;
			//System.out.println(handshakeResponse);
			 testMD = MessageDigest.getInstance("SHA1");
			 byte[] result = testMD.digest(handshakeResponse.getBytes());
			 encoded = Base64.getEncoder().encodeToString(result);
		}
	}
	
	public File getFile() {
		return pathToFile;
	}
	public String getResponseHash() {
		return encoded;
	}
}
