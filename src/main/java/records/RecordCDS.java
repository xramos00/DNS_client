package records;

/*
 * Author - Patricia Ramosova
 * Link - https://github.com/xramos00/DNS_client
 * */
public class RecordCDS extends RecordDS {

    public RecordCDS(byte[] rawMessage, int lenght, int startIndex) {
        super(rawMessage, lenght, startIndex);
    }
}
