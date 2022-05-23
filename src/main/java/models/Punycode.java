package models;
/*
 * Author - Martin Biolek
 * Link - https://github.com/mbio16/clientDNS
 * */
import java.net.IDN;

public class Punycode {

	public static String fromPunnycode(String domainName) {
		return IDN.toUnicode(domainName);
	}

	public static String toPunycode(String domainName) {
		return IDN.toASCII(domainName);
	}

}
