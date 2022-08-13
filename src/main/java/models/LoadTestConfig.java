package models;
/*
 * Author - Patricia Ramosova
 * Link - https://github.com/xramos00/DNS_client
 * */
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoadTestConfig {
    @JsonProperty(value = "maxNumOfRequests")
    private int maxNumOfRequests;
    @JsonProperty(value = "minNumOfRequests")
    private int minNumOfRequests;
    @JsonProperty(value = "maxTimeBetweenRequests")
    private int maxTimeBetweenRequests;
    @JsonProperty(value = "minTimeBetweenRequests")
    private int minTimeBetweenRequests;
    @JsonProperty(value = "defaultNumberOfRequests")
    private int defaultNumberOfRequests;
    @JsonProperty(value= "domain")
    private String domain;
    @JsonProperty(value= "record")
    private String record;
    @JsonProperty(value= "ipv4")
    private boolean ipv4;
    @JsonProperty(value= "recursive")
    private boolean recursive;
    @JsonProperty(value = "protocol")
    private String protocol;
    @JsonProperty(value = "holdConnection")
    private boolean holdConnection;
    @JsonProperty(value = "defaultTimeBetweenRequests")
    private int defaultTimeBetweenRequests;
    @JsonProperty(value = "do")
    private boolean do_;
    @JsonProperty(value = "ad")
    private boolean ad;
    @JsonProperty(value = "cd")
    private boolean cd;

    public LoadTestConfig(){
        maxNumOfRequests = 100;
        minNumOfRequests = 1;
        maxTimeBetweenRequests = 2000;
        minTimeBetweenRequests = 10;
        defaultNumberOfRequests = 10;
        domain = "seznam.cz;facebook.com";
        record = "A";
        ipv4 = true;
        recursive = true;
        protocol = "UDP";
        holdConnection = false;
        defaultTimeBetweenRequests = 500;
    }

    public LoadTestConfig(int maxNumOfRequests, int minNumOfRequests, int maxTimeBetweenRequests, int minTimeBetweenRequests,
                          int defaultNumberOfRequests, String domain, String record, boolean ipv4, boolean recursive,
                          String protocol, boolean holdConnection, boolean do_, boolean ad, boolean cd){
        this.maxNumOfRequests = maxNumOfRequests;
        this.minNumOfRequests = minNumOfRequests;
        this.maxTimeBetweenRequests = maxTimeBetweenRequests;
        this.minTimeBetweenRequests = minTimeBetweenRequests;
        this.defaultNumberOfRequests = defaultNumberOfRequests;
        this.domain = domain;
        this.record = record;
        this.ipv4 = ipv4;
        this.recursive = recursive;
        this.protocol = protocol;
        this.holdConnection = holdConnection;
        this.do_ = do_;
        this.ad = ad;
        this.cd = cd;
    }

}
