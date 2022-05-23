package records;

/*
 * Author - Patricia Ramosova
 * Link - https://github.com/xramos00/DNS_client
 * */
public class RecordCDNSKEY extends RecordDNSKEY {
    public RecordCDNSKEY(byte[] rawMessage, int lenght, int startIndex) {
        super(rawMessage, lenght, startIndex);
    }
}
