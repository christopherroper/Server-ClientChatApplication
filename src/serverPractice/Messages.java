package serverPractice;

import java.io.Serializable;
import java.security.Timestamp;
import java.sql.Date;

public class Messages implements Serializable {
	private String username;
	private String message;
	private Timestamp timestamp;
	private Date date;
	public Messages (String fullMessagePassed) {
		String[] fullMessage = fullMessagePassed.split(" ",2);
		username = fullMessage[0];
		message = fullMessage[1];
	}
	public String toJSON () {
		String toJSON = "{\"user\":\""+ username + "\", \"message\":\"" + message + "\"}";
		return toJSON;
	}
}
