package exceptions;

/*
 * Author - Martin Biolek
 * Link - https://github.com/mbio16/clientDNS
 * */
public class DnsServerIpIsNotValidException extends Exception {

	private static final long serialVersionUID = 1L;

	public DnsServerIpIsNotValidException() {
		// TODO Auto-generated constructor stub
		super("Ip address is not valid for DNS server");
	}

}
