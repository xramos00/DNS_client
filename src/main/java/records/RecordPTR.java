package records;
/*
 * Author - Martin Biolek
 * Link - https://github.com/mbio16/clientDNS
 * */
public class RecordPTR extends RecordCNAME {

	public RecordPTR(byte[] rawMessage, int lenght, int startIndex) {
		super(rawMessage, lenght, startIndex);
	}

}
