package exceptions;

/*
 * Author - Martin Biolek
 * Link - https://github.com/mbio16/clientDNS
 * */
public class NotValidIPException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotValidIPException() {
		super("Ip is not valid");
	}
}
