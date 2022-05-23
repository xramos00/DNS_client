package exceptions;

/*
 * Author - Martin Biolek
 * Link - https://github.com/mbio16/clientDNS
 * */
public class HttpCodeException extends Exception {
	private static final long serialVersionUID = 1L;
	private int code;

	public HttpCodeException(int code) {
		super("" + code);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
