package application;

import models.Ip;
import models.NameServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import javafx.scene.input.KeyCode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Config {
	
	private static final String configResource = "./config.properties";
	
	private static PropertiesConfiguration conf = null;

	private static List<NameServer> rootNameServers = new LinkedList<>();
	
	private static List<NameServer> NameServers = new LinkedList<>();
	
	private static List<NameServer> DoHNameServers = new LinkedList<>();
	
	public static final String ROOTSERVERSDOMAINNAMES = "rootServersDomainNames";

	public static final String SERVERSDOMAINNAMES = "serversDomainNames";

	public static List<NameServer> getRootNameServers() {
		return Config.rootNameServers;
	}

	public static void setRootNameServers(List<NameServer> rootNameServers) {
		Config.rootNameServers = rootNameServers;
	}

	public static List<NameServer> getNameServers() {
		return Config.NameServers;
	}

	public static void setNameServers(List<NameServer> nameServers) {
		Config.NameServers = nameServers;
	}

	public static List<NameServer> getDoHNameServers() {
		return Config.DoHNameServers;
	}

	public static void setDoHNameServers(List<NameServer> doHNameServers) {
		Config.DoHNameServers = doHNameServers;
	}
	
	public static PropertiesConfiguration getConfProperties() throws ConfigurationException
	{
		if (Config.conf == null)
		{
			Config.conf = new PropertiesConfiguration(Config.configResource);
		}
		return Config.conf;
	}
	
	@SuppressWarnings("unchecked")
	public static void loadConfiguration() throws ConfigurationException, IOException, ParseException {
		Config.rootNameServers = new LinkedList<NameServer>();
		
		String rootServersFilePath = Config.getConfProperties().getString(ROOTSERVERSDOMAINNAMES);
		BufferedReader reader;
		reader = new BufferedReader(new FileReader(rootServersFilePath));
		
		String line = reader.readLine();
		while(line != null) {
			NameServer ns = new NameServer(Config.keyCodeFromDomainName(line).toString().toUpperCase(),line,new LinkedList<String>(List.of(new String[]{Ip.getIpV4OfDomainName(line)})),
					new LinkedList<String>(List.of(new String[]{Ip.getIpV6OfDomainName(line)})));
			ns.setKeyCode(Config.keyCodeFromDomainName(line));
			Config.rootNameServers.add(ns);
			
			line = reader.readLine();
		}
		reader.close();

		// parse JSON with DNS servers
		JSONParser parser = new JSONParser();
		FileReader fileReader = new FileReader(Config.getConfProperties().getString(SERVERSDOMAINNAMES));
		JSONArray json = (JSONArray) parser.parse(fileReader);

		// for each DNS server create NameServer object
		json.forEach(obj -> {
			JSONObject nameServer = (JSONObject) obj;
			// get IPv4 and IPv6 addresses
			JSONArray ipv4 = (JSONArray) nameServer.get("ipv4");
			JSONArray ipv6 = (JSONArray) nameServer.get("ipv6");
			List<String> ipv4List = new LinkedList<>();
			List<String> ipv6List = new LinkedList<>();

			if (ipv4!=null)
			{
				ipv4.forEach(o -> ipv4List.add((String)o));
			}
			if (ipv6!=null)
			{
				ipv6.forEach(o -> ipv6List.add((String)o));
			}

			NameServer ns = new NameServer((String) nameServer.get("name"),(String) nameServer.get("domainName"),
					ipv4List,ipv6List);
			Config.NameServers.add(ns);
		});
		fileReader.close();
	}
	
	public static KeyCode keyCodeFromDomainName(String domain)
	{
		domain = domain.toUpperCase();
		String parts[] = domain.split("\\.");
		if (Array.getLength(parts) > 0)
		{
			return KeyCode.getKeyCode(parts[0]);
		}
		return null;
	}
	
}
