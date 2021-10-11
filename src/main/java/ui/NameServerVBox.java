package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Language;
import models.NameServer;

import java.util.List;

public class NameServerVBox extends VBox {

    private Tooltip mainTooltip;

    private List<IPToggleButton> IPv4ToggleButtons;
    private List<IPToggleButton> IPv6ToggleButtons;

    private HBox IPv4ToggleButtonsHBox;
    private HBox IPv6ToggleButtonsHBox;

    private RadioButton IPv4radioButton;
    private RadioButton IPv6radioButton;

    private ToggleGroup addressGroup;

    private ToggleGroup IPv4AddressesGroup=null;
    private ToggleGroup IPv6AddressesGroup=null;

    public NameServerVBox (NameServer nameServer, ToggleGroup toggleGroup)
    {
        super();
        int i=0;

        IPv4radioButton = new RadioButton();
        IPv6radioButton = new RadioButton();
        addressGroup = new ToggleGroup();
        // assign IPv4/6 radio buttons to same toggle group
        IPv4radioButton.setToggleGroup(toggleGroup);
        IPv4radioButton.setText(nameServer.getDomainName());
        IPv6radioButton.setToggleGroup(toggleGroup);
        IPv6radioButton.setText(nameServer.getDomainName());

        // create buttons and toggle groups for every IP
        if (nameServer.getIPv4Addr().size() > 1)
        {
            i=0;
            IPv4AddressesGroup = new ToggleGroup();
            IPv4ToggleButtonsHBox = new HBox();
            // set IPv4AddressesGroup as user data of IPv4radioButton, so it can be used later
            IPv4radioButton.setUserData(IPv4AddressesGroup);
            // if nameserver has more tha one IPv4, create toggleButton for each one
            for (String ip: nameServer.getIPv4Addr())
            {
                // create toggle button for IP
                IPToggleButton tb = new IPToggleButton(IPv4radioButton);
                tb.setText(Integer.toString(i+1) + ". IP");
                // store IP as user data
                tb.setUserData(ip);
                // set same toggle group for all buttons
                tb.setToggleGroup(IPv4AddressesGroup);
                // show IP in tool tip
                tb.setTooltip(new Tooltip("IPv4: "+ip));
                tb.setOnAction(actionEvent -> {
                    tb.getControlButton().setSelected(true);
                });
                // add button to its HBox
                IPv4ToggleButtonsHBox.getChildren().add(tb);
                i++;
            }
            IPv4radioButton.setTooltip(new Tooltip("Choose from one of the IP buttons"));
        }
        else if (nameServer.getIPv4Addr().size() == 1)
        {
            IPv4ToggleButtonsHBox = null;
            IPv4ToggleButtons = null;
            // get the only IPv4 address of a DNS server
            String ip = nameServer.getIPv4Addr().get(0);
            // store IP as user data of radio button
            IPv4radioButton.setUserData(ip);
            // show IP in Tooltip
            IPv4radioButton.setTooltip(new Tooltip("IPv4: "+ip));
        }
        else
        {
            // user data is null to indicate that NS has no IPv6 address
            IPv4ToggleButtonsHBox = null;
            IPv4radioButton.setUserData(null);
            IPv4radioButton.setTooltip(new Tooltip("-"));
        }

        if (nameServer.getIPv6Addr().size() > 1)
        {
            IPv6AddressesGroup = new ToggleGroup();
            IPv6ToggleButtonsHBox = new HBox();
            IPv6radioButton.setUserData(IPv6AddressesGroup);
            i=0;
            for (String ip: nameServer.getIPv6Addr())
            {
                // create toggle button for IP
                IPToggleButton tb = new IPToggleButton(IPv6radioButton);
                tb.setText(Integer.toString(i+1)+". IP");
                // set same toggle group for all buttons
                tb.setToggleGroup(IPv6AddressesGroup);
                // store IP as user data
                tb.setUserData(ip);
                // show IP in tool tip
                tb.setTooltip(new Tooltip("IPv6: "+ip));
                tb.setOnAction(actionEvent -> {
                    tb.getControlButton().setSelected(true);
                });
                // add button to its HBox
                IPv6ToggleButtonsHBox.getChildren().add(tb);
                i++;
            }
        }
        else if (nameServer.getIPv6Addr().size() == 1)
        {
            IPv6ToggleButtonsHBox = null;
            IPv6ToggleButtons = null;
            String ip = nameServer.getIPv6Addr().get(0);
            IPv6radioButton.setUserData(ip);
            IPv6radioButton.setTooltip(new Tooltip("IPv6: "+ip));
        }
        else
        {
            // user data is null to indicate that NS has no IPv6 address
            IPv6ToggleButtonsHBox=null;
            IPv6radioButton.setUserData(null);
            IPv6radioButton.setTooltip(new Tooltip("-"));
        }
        loadIPv4();
    }

    public void loadIPv4()
    {
        Separator separator = new Separator();
        separator.setPadding(new Insets(10,0,0,0));
        this.getChildren().clear();
        if(IPv4ToggleButtonsHBox == null)
        {
            this.getChildren().addAll(separator,IPv4radioButton);
        }
        else
        {
            IPv4ToggleButtonsHBox.setAlignment(Pos.CENTER_RIGHT);
            this.getChildren().addAll(separator,IPv4radioButton,IPv4ToggleButtonsHBox);
        }
    }

    public void loadIPv6()
    {
        Separator separator = new Separator();
        separator.setPadding(new Insets(10,0,0,0));
        this.getChildren().clear();
        if(IPv6ToggleButtonsHBox == null)
        {
            this.getChildren().addAll(separator,IPv6radioButton);
        }
        else
        {
            IPv6ToggleButtonsHBox.setAlignment(Pos.CENTER_RIGHT);
            this.getChildren().addAll(separator,IPv6radioButton,IPv6ToggleButtonsHBox);
        }
    }
}
