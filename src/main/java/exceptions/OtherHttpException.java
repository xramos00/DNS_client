package exceptions;

/*
 * Author - Martin Biolek
 * Link - https://github.com/mbio16/clientDNS
 * */
public class OtherHttpException extends Exception {
	private static final long serialVersionUID = 1L;

	public OtherHttpException() {
		super("Unknown http exception");
	}
}
