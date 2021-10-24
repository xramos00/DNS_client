package ui;

import exceptions.DnsServerIpIsNotValidException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.*;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;

public abstract class GeneralController
{

    public static String APP_TITTLE = "title";
    public String PROTOCOL = "DNS";
    protected Language language;
    protected Settings settings;
    protected Logger LOGGER;
    protected Ip ipDns;

    protected ToggleGroup wiresharkFilterToogleGroup;
    protected List<WiresharkFilter> filters;

    protected Map<String, String> parameters;
    protected ToggleGroup IPprotToggleGroup;
    protected ToggleGroup dnsserverToggleGroup;

    @FXML
    protected Menu wiresharkMenu;

    @FXML
    protected Button sendButton;

    @FXML
    protected TreeView requestTreeView;

    @FXML
    protected TreeView responseTreeView;

    @FXML
    protected TextField domainNameTextField;

    @FXML
    protected RadioButton IPv4RadioButton;
    @FXML
    protected RadioButton IPv6RadioButton;

    @FXML
    protected CheckBox aCheckBox = new CheckBox();
    @FXML
    protected Label responseTimeLabel;
    @FXML
    protected Label responseTimeValueLabel;
    @FXML
    protected Label numberOfMessagesLabel;
    @FXML
    protected Label numberOfMessagesValueLabel;
    @FXML
    protected Menu interfaceMenu;
    protected ToggleGroup interfaceToggleGroup;
    @FXML
    CheckBox aaaaCheckBox = new CheckBox();
    @FXML
    CheckBox ptrCheckBox = new CheckBox();
    @FXML
    CheckBox txtCheckBox = new CheckBox();
    @FXML
    CheckBox nsecCheckBox = new CheckBox();
    @FXML
    CheckBox anyCheckBox = new CheckBox();
    @FXML
    CheckBox soaCheckBox = new CheckBox();
    @FXML
    CheckBox dnskeyCheckBox = new CheckBox();
    @FXML
    CheckBox dsCheckBox = new CheckBox();
    @FXML
    CheckBox caaCheckBox = new CheckBox();
    @FXML
    CheckBox cnameCheckBox = new CheckBox();
    @FXML
    CheckBox nsCheckBox = new CheckBox();
    @FXML
    CheckBox mxCheckBox = new CheckBox();
    @FXML
    CheckBox rrsigCheckBox = new CheckBox();
    @FXML
    CheckBox holdConectionCheckbox = new CheckBox();
    @FXML
    CheckBox nsec3paramCheckBox = new CheckBox();
    @FXML
    CheckBox dnssecRecordsRequestCheckBox = new CheckBox();
    @FXML
    Label querySizeLabel;
    @FXML
    Label responseSizeLabel;

    public GeneralController() {}

    /**
     * Method creates basic wireshark filters and respective buttons in menu
     */
    protected void setWiresharkMenuItems()
    {
        parameters = new HashMap<String, String>();
        parameters.put("prefix", "ipv4");
        parameters.put("ip", null);
        parameters.put("tcpPort", null);
        parameters.put("udpPort", null);

        filters = new LinkedList<>();
        filters.add(new WiresharkFilter("IP", "${ip}"));
        filters.add(new WiresharkFilter("IP filter", "${prefix}.addr == ${ip}"));
        filters.add(new WiresharkFilter("IP & UDP", "${prefix}.addr == ${ip} && udp.port == ${udpPort}"));
        filters.add(new WiresharkFilter("IP & TCP", "${prefix}.addr == ${ip} && tcp.port == ${tcpPort}"));
        filters.add(new WiresharkFilter("IP & TCP & UDP", "${prefix}.addr == ${ip} && (tcp.port == ${tcpPort} || udp" +
                ".port == ${udpPort})"));

        for (WiresharkFilter filter : filters)
        {
            RadioMenuItem menuItem = new RadioMenuItem(filter.getName());
            menuItem.setUserData(filter);
            menuItem.setToggleGroup(wiresharkFilterToogleGroup);
            wiresharkMenu.getItems().add(menuItem);
        }
    }

    /**
     * Method is called when user wants to copy wireshark filter and is intended to supply specific parameters to
     * specific filters
     */
    abstract protected void updateCustomParameters();

    public Settings getSettings()
    {
        return settings;
    }

    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }

    public void setIpDns(Ip ip)
    {
        this.ipDns = ip;
    }

    public Language getLanguage()
    {
        return language;
    }

    public void setLanguage(Language language)
    {
        this.language = language;
        // System.out.println("Language loads to another window");
    }

    public String getTitle()
    {
        String client = language.getLanguageBundle().getString("title");
        String protocol = getProtocol();
        return client + " " + protocol;
    }

    public abstract String getProtocol();

    public void setLabels()
    {
        // To be overridden
    }

    public void loadDataFromSettings()
    {
        // to be overridden
    }

    public void networkInterfaces()
    {
        try
        {
            interfaceToggleGroup = new ToggleGroup();
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            ArrayList<RadioMenuItem> listMenuItems = new ArrayList<RadioMenuItem>();
            while (e.hasMoreElements())
            {
                RadioMenuItem pom = new RadioMenuItem();
                NetworkInterface ni = e.nextElement();
                if (ni.getName().equals(settings.getInterface().getName()))
                {
                    pom.setSelected(true);
                }
                pom.setText(ni.getName() + " -- " + ni.getDisplayName());
                pom.setUserData(ni);
                pom.setToggleGroup(interfaceToggleGroup);
                listMenuItems.add(pom);
            }
            interfaceMenu.getItems().addAll(listMenuItems);
        } catch (SocketException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Method returns selected network interface, which can be used to send data into network
     *
     * @return
     */
    protected NetworkInterface getInterface()
    {
        NetworkInterface netInterface = (NetworkInterface) interfaceToggleGroup.getSelectedToggle().getUserData();
        LOGGER.info(netInterface.getDisplayName().toString() + " " + netInterface.getName());
        settings.setInterface(netInterface);
        return netInterface;
    }

    /**
     * Method creates Modal window containing translated message for user
     *
     * @param exceptionName name of property containing desired message
     */
    protected void showAller(String exceptionName)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR, language.getLanguageBundle().getString(exceptionName));
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner((Stage) sendButton.getScene().getWindow());
        alert.show();
    }

    protected void showAlert(String messageId, Alert.AlertType alertType)
    {
        Alert alert = new Alert(alertType, language.getLanguageBundle().getString(messageId));
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner((Stage) sendButton.getScene().getWindow());
        alert.show();
    }

    /**
     * Function takes data from parameter in form of String
     * and put them into a clipboard
     *
     * @param data
     */
    protected void copyDataToClipBoard(String data)
    {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(data);
        clipboard.setContent(content);
    }

    @FXML
    protected void onDomainNameAction(ActionEvent e)
    {
        sendButtonFired(e);
    }

    protected void sendButtonFired(ActionEvent e)
    {
    }

    @FXML
    protected void copyJsonResponseDataFired(ActionEvent event)
    {
    }

    @FXML
    protected void copyJsonRequestDataFired(ActionEvent event)
    {
    }

    /**
     * Function creates filter for Wireshark based on selected RadioMenuItem in main menu
     */
    public void copyWiresharkFilter()
    {
        String ip;
        String prefix;
        try
        {
            ip = getDnsServerIp();
            if (ip == null)
            {
                return;
            }
        } catch (DnsServerIpIsNotValidException | UnknownHostException e)
        {
            showAller(language.getLanguageBundle().getString("CustomEndPointException"));
            return;
        }
        if (Ip.isIPv4Address(ip))
        {
            prefix = "ipv4";
        } else
        {
            prefix = "ipv6";
        }

        parameters.put("ip", ip);
        parameters.put("prefix", prefix);

        updateCustomParameters();

        if (wiresharkFilterToogleGroup.getSelectedToggle() == null)
        {
            showAller("chooseFilter");
            return;
        }
        String out =
                ((WiresharkFilter) wiresharkFilterToogleGroup.getSelectedToggle().getUserData()).generateQuery(parameters);
        copyDataToClipBoard(out);
        showAlert("filterCopied", Alert.AlertType.INFORMATION);

    }

    /**
     * Function returns IP address, v4 or v6, of selected DNS server from DNS servers TitledPane
     *
     * @return String representation of selected DNS server's IP address, null otherwise
     * @throws DnsServerIpIsNotValidException
     * @throws UnknownHostException
     */
    protected String getDnsServerIp() throws DnsServerIpIsNotValidException, UnknownHostException
    {

        Toggle selected = dnsserverToggleGroup.getSelectedToggle();

        if (selected == null)
        {
            showAller("ChooseDNSServer");
            return null;
        }

        Object userDataObject = selected.getUserData();

        String serverIp = null;

        if (userDataObject == null)
        {
            return null;
        }

        if (userDataObject instanceof String)
        {
            serverIp = (String) userDataObject;
        } else if (userDataObject instanceof NameServer)
        {
            serverIp = IPv4RadioButton.isSelected() ?
                    ((NameServer) userDataObject).getIPv4Addr().get(0) :
                    ((NameServer) userDataObject).getIPv6Addr().get(0);
        } else if (userDataObject instanceof ToggleGroup)
        {
            ToggleGroup group = (ToggleGroup) userDataObject;
            Toggle selectedAddress = group.getSelectedToggle();
            if (selectedAddress == null)
            {
                showAller("ChooseDNSServer");
                return null;
            }
            serverIp = (String) selectedAddress.getUserData();
        } else if (userDataObject instanceof TextField)
        {
            TextField input = (TextField) userDataObject;
            serverIp = input.getText();
        }

        return serverIp;

    }

}
