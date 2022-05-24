/*
 * Author - Patricia Ramosova
 * Link - https://github.com/xramos00/DNS_client
 * Based on work of Martin Biolek (https://github.com/mbio16/clientDNS)
 * */
package records;

import java.net.UnknownHostException;

import org.json.simple.JSONObject;

public class RecordAAAA extends RecordA {

	public RecordAAAA(byte[] rawMessage, int lenght, int startIndex) throws UnknownHostException {
		super(rawMessage, lenght, startIndex);
		KEY_ADDRESS = "Ipv6";
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getAsJson() {
		JSONObject object = new JSONObject();
		object.put(KEY_ADDRESS, ipAddressAsString);
		return object;
	}

}
