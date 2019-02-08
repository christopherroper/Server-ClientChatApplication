package serverPractice;

@SuppressWarnings("serial")
public class BadRequestException extends Exception{
	
	public BadRequestException() {
		super();
	}
	public BadRequestException(String string) {
		super (string);
	}
	public BadRequestException(Throwable e) {
		super (e);
	}
}
