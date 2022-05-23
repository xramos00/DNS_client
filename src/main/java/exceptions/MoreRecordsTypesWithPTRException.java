package exceptions;

/*
 * Author - Martin Biolek
 * Link - https://github.com/mbio16/clientDNS
 * */
public class MoreRecordsTypesWithPTRException extends Exception {

	private static final long serialVersionUID = 1L;

	public MoreRecordsTypesWithPTRException() {
		super("It is not possible to ask qustion with PTR record and other type record");
	}
}
