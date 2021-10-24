package models;

import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

public class WiresharkFilter
{

    private String name;
    private String template;

    public WiresharkFilter(String name, String template)
    {
        this.name = name;
        this.template = template;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTemplate()
    {
        return template;
    }

    public void setTemplate(String template)
    {
        this.template = template;
    }

    public String generateQuery(Map<String, String> parameters)
    {
        // Build StringSubstitutor
        StringSubstitutor sub = new StringSubstitutor(parameters);

        return sub.replace(template);
    }

    public class Parameters
    {
        public static final String TCPPORT = "tcpPort";
        public static final String UDPPORT = "udpPort";
        public static final String IP = "ip";
        public static final String PREFIX = "prefix";
    }
}
