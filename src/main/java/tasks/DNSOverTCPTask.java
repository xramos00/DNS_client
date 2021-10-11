package tasks;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import enums.APPLICATION_PROTOCOL;
import enums.Q_COUNT;
import enums.TRANSPORT_PROTOCOL;
import exceptions.CouldNotUseHoldConnectionException;
import exceptions.InterfaceDoesNotHaveIPAddressException;
import exceptions.NotValidDomainNameException;
import exceptions.NotValidIPException;
import exceptions.TimeoutException;
import javafx.application.Platform;
import models.TCPConnection;
import tasks.runnables.RequestResultsUpdateRunnable;

public class DNSOverTCPTask extends DNSTaskBase {

	public DNSOverTCPTask(boolean recursion, boolean dnssec, boolean rrRecords, boolean holdConnection, String domain, Q_COUNT[] types,
						  TRANSPORT_PROTOCOL transport_protocol, APPLICATION_PROTOCOL application_protocol,
						  String resolverIP, NetworkInterface netInterface, boolean caFlag)
			throws IOException, NotValidIPException, NotValidDomainNameException
	{
		super(recursion,dnssec,caFlag,rrRecords,domain,types,transport_protocol,application_protocol, resolverIP, netInterface);
		if (!holdConnection)
		{
			if (DNSTaskBase.getTcp()!=null)
			{
				DNSTaskBase.getTcp().closeAll();
			}
		}
	}

	@Override
	protected void sendData() throws TimeoutException{
		try {
			// start measuring time
			setStartTime(System.nanoTime());
			// setup TCP connection to DNS server
			if (DNSTaskBase.getTcp() == null) {
				DNSTaskBase.setTcp(new TCPConnection(getIp()));
			}
			// send data and store them by method setRecieveReply
			setRecieveReply(DNSTaskBase.getTcp().send(getMessageAsBytes(), getIp(), isCloseConnection(), getInterfaceToSend()));
			setWasSend(true);
			// stop measuring time
			setStopTime(System.nanoTime());
			// calculate duration of whole DNS request including setup of TCP connection
			setDuration(calculateDuration());
			setMessagesSent(1);

		} catch (IOException | TimeoutException | CouldNotUseHoldConnectionException |
				InterfaceDoesNotHaveIPAddressException e) {
			throw new TimeoutException();
		}
	}

	@Override
	protected void updateProgressUI()
	{
	}

	@Override
	protected void updateResultUI() {
		// updating UI with DNS request results
		Platform.runLater(new RequestResultsUpdateRunnable(this));
	}
}
