/*
 * Author - Patricia Ramosova
 * Link - https://github.com/xramos00/DNS_client
 * Based on work of Martin Biolek (https://github.com/mbio16/clientDNS)
 * */
package records;

public class RecordPTR extends RecordCNAME {

	public RecordPTR(byte[] rawMessage, int lenght, int startIndex) {
		super(rawMessage, lenght, startIndex);
	}

}
