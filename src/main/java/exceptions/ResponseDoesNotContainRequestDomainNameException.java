package exceptions;

/*
 * Author - Martin Biolek
 * Link - https://github.com/mbio16/clientDNS
 * */
public class ResponseDoesNotContainRequestDomainNameException extends Exception {

	private static final long serialVersionUID = 1L;

	public ResponseDoesNotContainRequestDomainNameException() {
		super("Response is not for the request");
	}
}
