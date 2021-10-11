package tasks;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Logger;

import exceptions.*;
import javafx.beans.binding.StringBinding;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.simple.JSONObject;

import enums.APPLICATION_PROTOCOL;
import enums.IP_PROTOCOL;
import enums.Q_COUNT;
import enums.RESPONSE_MDNS_TYPE;
import enums.TRANSPORT_PROTOCOL;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeItem;
import models.DomainConvert;
import models.Header;
import models.MessageParser;
import models.Request;
import models.Response;
import models.TCPConnection;
import models.UInt16;

public abstract class DNSTaskBase extends Task<Void> {
	
	private Header header;
	private ArrayList<Request> requests;
	private TRANSPORT_PROTOCOL transport_protocol;
	private APPLICATION_PROTOCOL application_protocol;
	private byte[] messageAsBytes;
	private int byteSizeQuery;
	private int byteSizeResponse;
	private String domainAsString;
	private InetAddress ip;
	private String resolver;
	private int size;
	private Socket socket;
	private byte[] recieveReply;
	private boolean rrRecords;
	private TreeItem<String> root;
	private long startTime;
	private long stopTime;
	private boolean dnssec;
	private static TCPConnection tcp = null;
	private IP_PROTOCOL ipProtocol;
	private RESPONSE_MDNS_TYPE mdnsType;
	private boolean mdnsDnssecSignatures;
	private boolean closeConnection;
	private Q_COUNT[] qcountTypes;
	private String httpRequest;
	private JSONObject httpResponse;
	private CloseableHttpClient httpClient;
	private int byteSizeResponseDoHDecompresed;
	private NetworkInterface interfaceToSend;
	private boolean wasSend;
	private ProgressBar progressBar;
	public static final int MAX_MESSAGES_SENT = 3;
	public static final int TIME_OUT_MILLIS = 2000;
	public static final int MAX_UDP_SIZE = 1232;
	public static final String IPv4_MDNS = "224.0.0.251";
	public static final String IPv6_MDNS = "ff02::fb";
	public static final int MDNS_PORT = 5353;
	public static final int DNS_PORT = 53;
	public static final String KEY_HEAD = "Head";
	public static final String KEY_QUERY = "Questions";
	public static final String KEY_REQUEST = "Request";
	public static final String KEY_ADDITIONAL_RECORDS = "Aditional records";
	public static final String KEY_LENGHT = "Lenght";
	public static final String[] httpRequestParamsName = new String[] { "name", "type", "do", "cd" };
	public static Logger LOGGER = Logger.getLogger(DomainConvert.class.getName());

	// properties connected to GUI
	private SimpleIntegerProperty messagesSentProperty;
	private SimpleDoubleProperty durationProperty;
	private ObjectProperty<TreeItem<String>> requestProperty;
	private ObjectProperty<TreeItem<String>> responseProperty;
	private SimpleIntegerProperty querySizeProperty = new SimpleIntegerProperty();
	private SimpleIntegerProperty responseSizeProperty = new SimpleIntegerProperty();

	public int getQuerySizeProperty()
	{
		return querySizeProperty.get();
	}

	public SimpleIntegerProperty querySizeProperty()
	{
		return querySizeProperty;
	}

	public void setQuerySizeProperty(int querySizeProperty)
	{
		this.querySizeProperty.set(querySizeProperty);
	}

	public int getResponseSizeProperty()
	{
		return responseSizeProperty.get();
	}

	public SimpleIntegerProperty responseSizeProperty()
	{
		return responseSizeProperty;
	}

	public void setResponseSizeProperty(int responseSizeProperty)
	{
		this.responseSizeProperty.set(responseSizeProperty);
	}

	public int getByteSizeResponse()
	{
		return byteSizeResponse;
	}

	public void setByteSizeResponse(int byteSizeResponse)
	{
		this.byteSizeResponse = byteSizeResponse;
	}

	// variables used to store values before updating GUIS
	private int messagesSent;
	private double duration;
	private TreeItem<String> request;
	private TreeItem<String> response;

	public int getMessagesSent() {
		return messagesSent;
	}

	public void setMessagesSent(int messagesSent) {
		this.messagesSent = messagesSent;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public TreeItem<String> getRequest() {
		return request;
	}

	public void setRequest(TreeItem<String> request) {
		this.request = request;
	}

	public TreeItem<String> getResponse() {
		return response;
	}

	public void setResponse(TreeItem<String> response) {
		this.response = response;
	}
	
	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public ArrayList<Request> getRequests() {
		return requests;
	}

	public void setRequests(ArrayList<Request> requests) {
		this.requests = requests;
	}

	public TRANSPORT_PROTOCOL getTransport_protocol() {
		return transport_protocol;
	}

	public void setTransport_protocol(TRANSPORT_PROTOCOL transport_protocol) {
		this.transport_protocol = transport_protocol;
	}

	public APPLICATION_PROTOCOL getApplication_protocol() {
		return application_protocol;
	}

	public void setApplication_protocol(APPLICATION_PROTOCOL application_protocol) {
		this.application_protocol = application_protocol;
	}

	public byte[] getMessageAsBytes() {
		return messageAsBytes;
	}

	public void setMessageAsBytes(byte[] messageAsBytes) {
		this.messageAsBytes = messageAsBytes;
	}

	public int getByteSizeQuery() {
		return byteSizeQuery;
	}

	public void setByteSizeQuery(int byteSizeQuery) {
		this.byteSizeQuery = byteSizeQuery;
	}

	public String getDomainAsString() {
		return domainAsString;
	}

	public void setDomainAsString(String domainAsString) {
		this.domainAsString = domainAsString;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public String getResolver() {
		return resolver;
	}

	public void setResolver(String resolver) {
		this.resolver = resolver;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getMessagesSentProperty() {
		return messagesSentProperty.get();
	}

	public void setMessagesSentProperty(int messagesSentProperty) {
		this.messagesSentProperty.set(messagesSentProperty);
	}
	
	public SimpleIntegerProperty messagesSentPropertyProperty()
	{
		return messagesSentProperty;
	}
	
	public double getDurationProperty() {
		return durationProperty.get();
	}

	public void setDurationProperty(double durationProperty) {
		this.durationProperty.set(durationProperty);
	}
	
	public SimpleDoubleProperty durationPropertyProperty()
	{
		return durationProperty;
	}
	
	public TreeItem<String> getRequestProperty() {
		return requestProperty.get();
	}

	public void setRequestProperty(TreeItem<String> requestProperty) {
		this.requestProperty.set(requestProperty);
	}
	
	public ObjectProperty<TreeItem<String>> requestPropertyProperty()
	{
		return requestProperty;
	}
	
	public TreeItem<String> getResponseProperty() {
		return responseProperty.get();
	}

	public void setResponseProperty(TreeItem<String> responseProperty) {
		this.responseProperty.set(responseProperty);
	}
	
	public ObjectProperty<TreeItem<String>> responsePropertyProperty()
	{
		return responseProperty;
	}

	public byte[] getRecieveReply() {
		return recieveReply;
	}

	public void setRecieveReply(byte[] recieveReply) {
		this.recieveReply = recieveReply;
		byteSizeResponse = this.recieveReply.length;
	}

	public boolean isRrRecords() {
		return rrRecords;
	}

	public void setRrRecords(boolean rrRecords) {
		this.rrRecords = rrRecords;
	}

	public TreeItem<String> getRoot() {
		return root;
	}

	public void setRoot(TreeItem<String> root) {
		this.root = root;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getStopTime() {
		return stopTime;
	}

	public void setStopTime(long stopTime) {
		this.stopTime = stopTime;
	}

	public boolean isDnssec() {
		return dnssec;
	}

	public void setDnssec(boolean dnssec) {
		this.dnssec = dnssec;
	}

	public static TCPConnection getTcp() {
		return DNSTaskBase.tcp;
	}

	public static void setTcp(TCPConnection tcp) {
		DNSTaskBase.tcp = tcp;
	}

	public IP_PROTOCOL getIpProtocol() {
		return ipProtocol;
	}

	public void setIpProtocol(IP_PROTOCOL ipProtocol) {
		this.ipProtocol = ipProtocol;
	}

	public RESPONSE_MDNS_TYPE getMdnsType() {
		return mdnsType;
	}

	public void setMdnsType(RESPONSE_MDNS_TYPE mdnsType) {
		this.mdnsType = mdnsType;
	}

	public boolean isMdnsDnssecSignatures() {
		return mdnsDnssecSignatures;
	}

	public void setMdnsDnssecSignatures(boolean mdnsDnssecSignatures) {
		this.mdnsDnssecSignatures = mdnsDnssecSignatures;
	}

	public boolean isCloseConnection() {
		return closeConnection;
	}

	public void setCloseConnection(boolean closeConnection) {
		this.closeConnection = closeConnection;
	}

	public Q_COUNT[] getQcountTypes() {
		return qcountTypes;
	}

	public void setQcountTypes(Q_COUNT[] qcountTypes) {
		this.qcountTypes = qcountTypes;
	}

	public String getHttpRequest() {
		return httpRequest;
	}

	public void setHttpRequest(String httpRequest) {
		this.httpRequest = httpRequest;
	}

	public JSONObject getHttpResponse() {
		return httpResponse;
	}

	public void setHttpResponse(JSONObject httpResponse) {
		this.httpResponse = httpResponse;
	}

	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public int getByteSizeResponseDoHDecompresed() {
		return byteSizeResponseDoHDecompresed;
	}

	public void setByteSizeResponseDoHDecompresed(int byteSizeResponseDoHDecompresed) {
		this.byteSizeResponseDoHDecompresed = byteSizeResponseDoHDecompresed;
	}

	public NetworkInterface getInterfaceToSend() {
		return interfaceToSend;
	}

	public void setInterfaceToSend(NetworkInterface interfaceToSend) {
		this.interfaceToSend = interfaceToSend;
	}

	public boolean isWasSend() {
		return wasSend;
	}

	public void setWasSend(boolean wasSend) {
		this.wasSend = wasSend;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}
	
	public DNSTaskBase(boolean recursion, boolean dnssec, boolean caFlag, boolean rrRecords, String domain, Q_COUNT[] types,
			TRANSPORT_PROTOCOL transport_protocol, APPLICATION_PROTOCOL application_protocol, String resolverIP, NetworkInterface netInterface) throws UnsupportedEncodingException, NotValidIPException, NotValidDomainNameException, UnknownHostException
	{
		super();
		requests = new ArrayList<Request>();
		header = new Header(recursion, dnssec, types.length, rrRecords, caFlag);
		size = Header.getSize();
		addRequests(types, checkAndStripFullyQualifyName(domain));
		
		messagesSentProperty = new SimpleIntegerProperty();
		durationProperty = new SimpleDoubleProperty();
		
		requestProperty = new SimpleObjectProperty<TreeItem<String>>();
		responseProperty = new SimpleObjectProperty<TreeItem<String>>();
		
		// this.resolverIP = resolverIP;
		this.transport_protocol = transport_protocol;
		this.application_protocol = application_protocol;
		if (application_protocol != APPLICATION_PROTOCOL.DOH) {
			this.ip = InetAddress.getByName(resolverIP);
		}
		this.resolver = resolverIP;
		this.recieveReply = new byte[1232];
		this.rrRecords = rrRecords;
		this.dnssec = dnssec;
		this.qcountTypes = types;
		this.domainAsString = domain;
		setMessagesSentProperty(0);

		this.wasSend = false;
		interfaceToSend = netInterface;
	}
	
	private void addRequests(Q_COUNT[] types, String domain)
			throws NotValidIPException, UnsupportedEncodingException, NotValidDomainNameException {
		for (Q_COUNT qcount : types) {
			Request r = new Request(domain, qcount);
			requests.add(r);
			size += r.getSize();
		}
	}
	
	private String checkAndStripFullyQualifyName(String domain) {
		if (domain.endsWith(".")) {
			return domain.substring(0, domain.length() - 1);
		} else {
			return domain;
		}
	}
	
	protected void messageToBytes() {
		int curentIndex = 0;
		if (rrRecords) {
			size += new Response().getDnssecAsBytes().length;
		}
		if (transport_protocol == TRANSPORT_PROTOCOL.TCP) {

			this.messageAsBytes = new byte[size + 2];
			UInt16 tcpSize = new UInt16(size);
			curentIndex = 2;
			this.messageAsBytes[1] = tcpSize.getAsBytes()[0];
			this.messageAsBytes[0] = tcpSize.getAsBytes()[1];
		} else {
			this.messageAsBytes = new byte[size];
		}

		byte head[] = header.getHaderAsBytes();
		for (int i = 0; i < head.length; i++) {
			this.messageAsBytes[curentIndex] = head[i];
			curentIndex++;
		}
		for (Request r : requests) {
			byte requestBytes[] = r.getRequestAsBytes();
			for (int i = 0; i < requestBytes.length; i++) {
				this.messageAsBytes[curentIndex] = requestBytes[i];
				curentIndex++;
			}
		}
		byte opt[] = new Response().getDnssecAsBytes();
		int j = 0;
		for (int i = curentIndex; i < size; i++) {
			this.messageAsBytes[i] = opt[j];
			j++;
		}
		byteSizeQuery = messageAsBytes.length;

	}
	
	public TreeItem<String> getAsTreeItem() {
		root = new TreeItem<String>(KEY_REQUEST);
		root.getChildren().add(header.getAsTreeItem());
		addRequestToTreeItem();
		// OPT in DNS
		if (rrRecords) {
			TreeItem<String> optRecord = new TreeItem<String>(MessageParser.KEY_ADDITIONAL_RECORDS);
			optRecord.getChildren().add(Response.getOptAsTreeItem(true, false));
			root.getChildren().add(optRecord);
		}
		if (mdnsType != null) {
			TreeItem<String> optRecord = new TreeItem<String>(MessageParser.KEY_ADDITIONAL_RECORDS);
			optRecord.getChildren().add(Response.getOptAsTreeItem(mdnsDnssecSignatures, true));
			root.getChildren().add(optRecord);
		}
		if (transport_protocol == TRANSPORT_PROTOCOL.TCP) {
			TreeItem<String> tcpTreeItem = new TreeItem<String>("");
			tcpTreeItem.getChildren().add(new TreeItem<String>(KEY_LENGHT + ": " + (byteSizeQuery - 2)));
			tcpTreeItem.getChildren().add(root);
			return tcpTreeItem;

		}
		return root;
	}
	
	private void addRequestToTreeItem() {
		TreeItem<String> subRequest = new TreeItem<String>(KEY_QUERY);
		if (requests.size() > 0) {
			for (Request request : requests) {
				subRequest.getChildren().add(request.getAsTreeItem());
			}
			root.getChildren().add(subRequest);
		}
	}

	protected abstract void sendData() throws TimeoutException, MessageTooBigForUDPException, InterfaceDoesNotHaveIPAddressException;

	protected abstract void updateProgressUI();

	protected abstract void updateResultUI();

	@Override
	protected Void call() throws IndexOutOfBoundsException, UnknownHostException, UnsupportedEncodingException,
			QueryIdNotMatchException, TimeoutException, MessageTooBigForUDPException, InterfaceDoesNotHaveIPAddressException {

		// transform message to bytes array
		messageToBytes();

		// send data over network
		// method will be specified in the implementation of method
		sendData();

		// parse received message
		MessageParser parser = new MessageParser(getRecieveReply(),getHeader(),getTransport_protocol());
		parser.parse();
		// store request and response as TreeItem<String> so it can be passed to GUI
		setResponse(parser.getAsTreeItem());
		setRequest(getAsTreeItem());

		// update UI with results
		updateResultUI();

		return null;

	}

	protected double calculateDuration()
	{
		double h = (getStopTime() - getStartTime()) / 1000000.00;
		h = Math.round(h * 100) / 100.0;
		// store data about duration and count of messages
		return h;
	}

}
