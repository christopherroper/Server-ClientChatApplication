package serverPractice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.util.HashMap;

public class HTTPResponse {
	OutputStream os;
	PrintWriter pw;

	public HTTPResponse (File pathToFile, SocketChannel sc) {
		
		try {
			os = sc.socket().getOutputStream();
		} catch (IOException e) {
			System.out.println("bad OutputStream");
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(os);
	
	if (!pathToFile.exists()) {
		pw.print("HTTP/1.1 404 NOT FOUND\r\n");
		pw.print("Content-Length: 0\r\n");
		pw.print("Content-Type: text/html\r\n");
		pw.print("\r\n");
		pw.flush();
		}
//	else if(HTTPRequest.requestMap.containsKey("Sec-WebSocket-Key")) {
//		pw.print(HTTPRequest.hashed);
//
//	}
	else {
			pw.print("HTTP/1.1 200 OK\r\n");
			pw.print("Content-Length: " + pathToFile.length() + "\r\n");
			pw.print("\r\n");				
			pw.flush();	
			
			FileInputStream htmlIn = null;
			try {
				htmlIn = new FileInputStream(pathToFile);
			} catch (FileNotFoundException e) {
				System.out.println("bad file path");
				e.printStackTrace();
			}
			byte[] buffer= new byte[1024];
			int bufferSize = 0;
			
			do {
				try {
					bufferSize = htmlIn.read(buffer);
				} catch (IOException e) {
					System.out.println("bad buffer?");
					e.printStackTrace();
				}
					try {
						os.write(buffer, 0, buffer.length);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					os.flush();
//					try {
//						Thread.sleep(50);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				} catch (IOException e) {
//					System.out.println("bad buffer?");
//					e.printStackTrace();
//				}
			} 
			while (bufferSize > 0);
			pw.flush();
			
			try {
				htmlIn.close();
			} catch (IOException e) {
				System.out.println("??");
				e.printStackTrace();
				System.exit(0);
			}
		}	
			pw.close();

			try {
				sc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}