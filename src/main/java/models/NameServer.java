package models;

import javafx.scene.input.KeyCode;

import java.util.LinkedList;
import java.util.List;

public class NameServer
{

    private String name;

    private String domainName;

    private List<String> IPv4Addr;

    private List<String> IPv6Addr;

    private KeyCode keyCode = null;

    public NameServer(String name, String domainName, List<String> iPv4Addr, List<String> iPv6Addr)
    {
        super();
        this.name = name;
        this.domainName = domainName;
        this.IPv4Addr = iPv4Addr;
        this.IPv6Addr = iPv6Addr;
    }

    public NameServer(String name, String domainName, String iPv4Addr, String iPv6Addr)
    {
		this.name = name;
        this.domainName = domainName;
        this.IPv4Addr = new LinkedList<>();
        this.IPv6Addr = new LinkedList<>();
        this.IPv4Addr.add(iPv4Addr);
        this.IPv6Addr.add(iPv6Addr);
    }

    public String getDomainName()
    {
        return domainName;
    }

    public void setDomainName(String domainName)
    {
        this.domainName = domainName;
    }

    public List<String> getIPv4Addr()
    {
        return IPv4Addr;
    }

    public void setIPv4Addr(List<String> iPv4Addr)
    {
        IPv4Addr = iPv4Addr;
    }

    public List<String> getIPv6Addr()
    {
        return IPv6Addr;
    }

    public void setIPv6Addr(List<String> iPv6Addr)
    {
        IPv6Addr = iPv6Addr;
    }

    public KeyCode getKeyCode()
    {
        return keyCode;
    }

    public void setKeyCode(KeyCode keyCode)
    {
        this.keyCode = keyCode;
    }

}
