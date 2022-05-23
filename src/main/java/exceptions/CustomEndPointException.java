package exceptions;

/*
 * Author - Martin Biolek
 * Link - https://github.com/mbio16/clientDNS
 * */
public class CustomEndPointException extends Exception {

	private static final long serialVersionUID = 1L;

	public CustomEndPointException() {
		super("End point is not valid");
	}
}
