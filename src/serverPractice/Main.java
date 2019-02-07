package serverPractice;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {
public Server f;
	public static void main(String[] args) throws Exception {
		Server f = new Server();
		try {
			f.Serve();
		} catch (BadRequestException e) {
			e.printStackTrace();
			System.out.println("Error: Bad Request");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IOException");
		}
	}
}
