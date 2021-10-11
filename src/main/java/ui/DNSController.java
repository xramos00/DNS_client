package ui;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import application.App;
import application.Config;
import enums.APPLICATION_PROTOCOL;
import enums.IP_PROTOCOL;
import enums.Q_COUNT;
import enums.TRANSPORT_PROTOCOL;
import enums.WIRESHARK_FILTER;
import exceptions.DnsServerIpIsNotValidException;
import exceptions.MoreRecordsTypesWithPTRException;
import exceptions.NonRecordSelectedException;
import exceptions.NotValidDomainNameException;
import exceptions.NotValidIPException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.DomainConvert;
import models.Ip;
import models.NameServer;
import tasks.DNSOverTCPTask;
import tasks.DNSOverUDPTask;
import tasks.DNSTaskBase;

public class DNSController extends GeneralController
{

    public static final String FXML_FILE_NAME_SMALL = "/fxml/DNS_small.fxml";
    public static final String FXML_FILE_NAME_LARGE = "/fxml/DNS_small.fxml";

    public static boolean layoutLarge = true;

    @FXML
    private VBox vboxRoot;

    // text fields
    @FXML
    private TextField dnsServerTextField;
    @FXML
    private ComboBox<String> dnsServerComboBox;

    @FXML
    private TitledPane DNSSECTitledPane;

    @FXML
    CheckBox DNSSECOkCheckBox;

    @FXML
    CheckBox authenticateDataCheckBox;

    @FXML
    CheckBox checkingDisabledCheckBox;

    @FXML
    protected Label wiresharkLabel;

    // radio buttons
    @FXML
    private RadioButton tcpRadioButton;
    @FXML
    private RadioButton udpRadioButton;
    @FXML
    private RadioButton recursiveQueryRadioButton;
    @FXML
    private RadioButton iterativeQueryRadioButton;

    @FXML
    private RadioButton IPv4RadioButton;
    @FXML
    private RadioButton IPv6RadioButton;

    @FXML
    protected RadioButton dnssecYesRadioButton;
    @FXML
    protected RadioButton dnssecNoRadioButton;
    // menu items
    @FXML
    protected MenuItem deleteDomainNameHistory;
    @FXML
    private MenuItem deleteDNSServersHistory;


    @FXML
    protected RadioMenuItem justIp;
    @FXML
    protected RadioMenuItem ipAsFilter;
    @FXML
    private RadioMenuItem ipWithUDPAsFilter;
    @FXML
    protected RadioMenuItem ipwithTCPAsFilter;
    @FXML
    private RadioMenuItem ipWithUDPandTcpAsFilter;
    @FXML
    protected CheckBox nsec3CheckBox;

    @FXML
    protected GridPane dnsServerGridPane;

    @FXML
    protected VBox rootServersVBox;

    @FXML
    protected VBox leftRootServersVBox;

    @FXML
    protected VBox rightRootServersVBox;

    @FXML
    protected VBox otherDNSVbox;

    // titledpane
    @FXML
    private TitledPane transportTitledPane;
    @FXML
    protected TitledPane dnsServerTitledPane;
    @FXML
    protected TitledPane iterativeTitledPane;

    // toogleGroup
    // toogleGroup
    private ToggleGroup transportToggleGroup;
    private ToggleGroup iterativeToggleGroup;
    protected ToggleGroup dnsserverToggleGroup;
    protected ToggleGroup otherDNSToggleGroup;
    protected ToggleGroup wiresharkFilterToogleGroup;
    protected ToggleGroup dnssecToggleGroup;
    protected ToggleGroup IPprotToggleGroup;

    // choice box
    @FXML
    private ComboBox<String> savedDNSChoiceBox;
    @FXML
    private ProgressBar progressBar;
    @FXML
    protected ImageView cloudflareIpv4ImageView;
    @FXML
    protected ImageView googleIpv4IamgeView;
    @FXML
    private ImageView cznicIpv4RadioIamgeView;
    @FXML
    private ImageView cloudflareIpv6ImageView;
    @FXML
    private ImageView googleIpv6ImagaView;
    @FXML
    private ImageView cznicIpv6ImageView;
    @FXML
    private ImageView systemIpv4DNSImageView;
    @FXML
    private ImageView systemIpv6DNSIamgeView;
    @FXML
    private ImageView custumImageView;

    private Map<KeyCode, RadioButton> keyMappings;

    public DNSController()
    {

        super();
        LOGGER = Logger.getLogger(DNSController.class.getName());
        PROTOCOL = "DNS";

    }

    public void initialize()
    {


        transportToggleGroup = new ToggleGroup();
        tcpRadioButton.setToggleGroup(transportToggleGroup);
        udpRadioButton.setToggleGroup(transportToggleGroup);

        iterativeToggleGroup = new ToggleGroup();
        iterativeQueryRadioButton.setToggleGroup(iterativeToggleGroup);
        recursiveQueryRadioButton.setToggleGroup(iterativeToggleGroup);

        dnssecToggleGroup = new ToggleGroup();

        //dnssecYesRadioButton.setToggleGroup(dnssecToggleGroup);
        //dnssecNoRadioButton.setToggleGroup(dnssecToggleGroup);

        IPprotToggleGroup = new ToggleGroup();

        IPv4RadioButton.setToggleGroup(IPprotToggleGroup);
        IPv6RadioButton.setToggleGroup(IPprotToggleGroup);

        keyMappings = new HashMap<>();

        dnsserverToggleGroup = new ToggleGroup();
        int i = 0;
        for (NameServer ns : Config.getRootNameServers())
        {
            RadioButton rb = new RadioButton(ns.getKeyCode().toString().toUpperCase());
            if (i % 2 == 0)
            {
                leftRootServersVBox.getChildren().add(rb);
            } else
            {
                rightRootServersVBox.getChildren().add(rb);
            }
            rb.setUserData(ns);
            rb.setTooltip(new Tooltip(ns.getIPv4Addr() + "\n" + ns.getIPv6Addr()));
            rb.setToggleGroup(dnsserverToggleGroup);
            keyMappings.put(ns.getKeyCode(), rb);
            i++;
        }

        Ip ip = new Ip();

        Config.getNameServers().add(new NameServer("System DNS", "System DNS", ip.getIpv4DnsServer(),
                ip.getIpv6DnsServer()));
        for (NameServer ns : Config.getNameServers())
        {
            otherDNSVbox.getChildren().add(new NameServerVBox(ns, dnsserverToggleGroup));
        }

        HBox customDNS = new HBox();
        RadioButton customToggle = new RadioButton();
        customToggle.setToggleGroup(dnsserverToggleGroup);
        TextField input = new TextField();
        //input.setPromptText(language.getLanguageBundle().getString("dnsServerDropDownLabel"));
        customToggle.setUserData(input);
        input.setOnMouseClicked(actionEvent -> {
            customToggle.setSelected(true);
        });
        customDNS.getChildren().addAll(customToggle, input);
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 5, 0));
        otherDNSVbox.getChildren().add(separator);
        otherDNSVbox.getChildren().add(customDNS);

        wiresharkFilterToogleGroup = new ToggleGroup();
        justIp.setToggleGroup(wiresharkFilterToogleGroup);
        ipAsFilter.setToggleGroup(wiresharkFilterToogleGroup);
        ipWithUDPAsFilter.setToggleGroup(wiresharkFilterToogleGroup);
        ipwithTCPAsFilter.setToggleGroup(wiresharkFilterToogleGroup);
        ipWithUDPandTcpAsFilter.setToggleGroup(wiresharkFilterToogleGroup);

        // hide root tree item in tree view, so it is expanded after receiving response
        requestTreeView.setShowRoot(false);
        responseTreeView.setShowRoot(false);

        // disable checkbox for holding TCP connection, because default selected transport protocol is UDP
        holdConectionCheckbox.setDisable(true);
        tcpRadioButton.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue)
            {
                holdConectionCheckbox.setDisable(false);
            } else
            {
                holdConectionCheckbox.setDisable(true);
            }
        });


    }

    @Override
    public String getProtocol()
    {
        return "DNS";
    }

    public void setLabels()
    {
        setKeyboardShortcuts();
        setUserDataTransportProtocol();
        setUserDataWiresharkRadioMenuItem();
        setUserDataRecords();
        setUserDataIPRadioButtons();
    }

    private void setKeyboardShortcuts()
    {
        vboxRoot.getScene().addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            System.out.println(ke.getCode());
            if (!ke.isShiftDown())
            {
                return;
            }
            if (keyMappings.get(ke.getCode()) != null)
            {
                keyMappings.get(ke.getCode()).setSelected(true);
            }
            ke.consume();
        });
    }

    private void setUserDataIPRadioButtons()
    {
        IPv4RadioButton.setUserData(IP_PROTOCOL.IPv4);
        IPv6RadioButton.setUserData(IP_PROTOCOL.IPv6);
    }

    private void setUserDataWiresharkRadioMenuItem()
    {
        justIp.setUserData(WIRESHARK_FILTER.JUST_IP);
        ipAsFilter.setUserData(WIRESHARK_FILTER.IP_FILTER);
        ipWithUDPAsFilter.setUserData(WIRESHARK_FILTER.IP_WITH_UDP);
        ipwithTCPAsFilter.setUserData(WIRESHARK_FILTER.IP_WITH_TCP);
        ipWithUDPandTcpAsFilter.setUserData(WIRESHARK_FILTER.IP_WITH_UDP_AND_TCP);
    }

    private void setUserDataTransportProtocol()
    {
        tcpRadioButton.setUserData(TRANSPORT_PROTOCOL.TCP);
        udpRadioButton.setUserData(TRANSPORT_PROTOCOL.UDP);
    }

    protected void setUserDataRecords()
    {
        aCheckBox.setUserData(Q_COUNT.A);
        aaaaCheckBox.setUserData(Q_COUNT.AAAA);
        cnameCheckBox.setUserData(Q_COUNT.CNAME);
        mxCheckBox.setUserData(Q_COUNT.MX);
        nsCheckBox.setUserData(Q_COUNT.NS);
        caaCheckBox.setUserData(Q_COUNT.CAA);
        ptrCheckBox.setUserData(Q_COUNT.PTR);
        txtCheckBox.setUserData(Q_COUNT.TXT);
        dnskeyCheckBox.setUserData(Q_COUNT.DNSKEY);
        soaCheckBox.setUserData(Q_COUNT.SOA);
        dsCheckBox.setUserData(Q_COUNT.DS);
        rrsigCheckBox.setUserData(Q_COUNT.RRSIG);
        nsecCheckBox.setUserData(Q_COUNT.NSEC);
        nsec3CheckBox.setUserData(Q_COUNT.NSEC3);
        nsec3paramCheckBox.setUserData(Q_COUNT.NSEC3PARAM);
        anyCheckBox.setUserData(Q_COUNT.ANY);
    }

    public void loadDataFromSettings()
    {/*
		savedDomainNamesChoiseBox.getItems().addAll(settings.getDomainNamesDNS());
		savedDNSChoiceBox.getItems().addAll(settings.getDnsServers());*/
    }

    @FXML
    private void onRadioButtonChange(ActionEvent event)
    {
        if (dnsserverToggleGroup.getSelectedToggle().getUserData() != null)
        {
            dnsServerTextField.setText("");
        }
    }

    @FXML
    private void deselectRootDNS(ActionEvent event)
    {
        if (dnsserverToggleGroup.getSelectedToggle() != null)
        {
            dnsserverToggleGroup.getSelectedToggle().setSelected(false);
        }
    }

    private String getDnsServerIp() throws DnsServerIpIsNotValidException, UnknownHostException
    {

        Toggle selected = dnsserverToggleGroup.getSelectedToggle();

        if (selected == null)
        {
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
            serverIp = (String) selectedAddress.getUserData();
        } else if (userDataObject instanceof TextField)
        {
            TextField input = (TextField) userDataObject;
            serverIp = input.getText();
        }

        return serverIp;

    }

    @FXML
    protected void backButtonFired(ActionEvent event)
    {
        otherDNSVbox.getChildren().clear();
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MainController.FXML_FILE_NAME));
            Stage newStage = new Stage();
            newStage.setScene(new Scene((Parent) loader.load()));
            GeneralController controller = (GeneralController) loader.getController();
            controller.setLanguage(language);
            controller.setSettings(settings);
            newStage.initModality(Modality.APPLICATION_MODAL);

            Stage oldStage = (Stage) sendButton.getScene().getWindow();
            newStage.setX(oldStage.getX() + (oldStage.getWidth() / 4));
            newStage.setY(oldStage.getY() + (oldStage.getHeight() / 4));
            newStage.getIcons().add(new Image(App.ICON_URI));
            newStage.show();

            oldStage.close();
            controller.setLabels();
            controller.setIpDns(ipDns);
        } catch (Exception e)
        {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR, language.getLanguageBundle().getString("windowError"));
            alert.showAndWait();
        }
    }

    protected String getDomain() throws NotValidDomainNameException
    {
        try
        {
            String domain = (domainNameTextField.getText());
            LOGGER.info("Domain name: " + domain);
            if (domain.equals("."))
            {
                return domain;
            }
            if (domain == "")
            {
                throw new NotValidDomainNameException();
            }

            if ((domain.contains(".arpa")) && ptrCheckBox.isSelected())
            {
                return domain;
            }
            if ((Ip.isIPv4Address(domain) || Ip.isIpv6Address(domain)) && ptrCheckBox.isSelected())
            {
                LOGGER.info("PTR record request");
                return Ip.getIpReversed(domain);
            }
            if (DomainConvert.isValidDomainName(domain))
            {
                settings.addDNSDomain(domain);

                return DomainConvert.encodeIDN(domain);
            } else
            {
                throw new NotValidDomainNameException();
            }
        } catch (Exception e)
        {
            LOGGER.warning(e.toString());
            throw new NotValidDomainNameException();
        }
    }

    protected Q_COUNT[] getRecordTypes() throws MoreRecordsTypesWithPTRException, NonRecordSelectedException
    {
        ArrayList<Q_COUNT> list = new ArrayList<Q_COUNT>();
        CheckBox[] checkBoxArray = {aCheckBox, aaaaCheckBox, nsCheckBox, mxCheckBox, soaCheckBox, cnameCheckBox,
                ptrCheckBox, dnskeyCheckBox, dsCheckBox, caaCheckBox, txtCheckBox, rrsigCheckBox, nsecCheckBox,
                nsec3CheckBox, nsec3paramCheckBox, anyCheckBox};
        for (int i = 0; i < checkBoxArray.length; i++)
        {
            if (checkBoxArray[i].isSelected())
            {
                list.add((Q_COUNT) checkBoxArray[i].getUserData());
            }
        }
        if (list.contains(Q_COUNT.PTR) && list.size() > 1)
        {
            throw new MoreRecordsTypesWithPTRException();
        }
        if (list.size() == 0)
        {
            throw new NonRecordSelectedException();
        }
        Q_COUNT returnList[] = new Q_COUNT[list.size()];
        for (int i = 0; i < returnList.length; i++)
        {
            returnList[i] = list.get(i);
        }
        return returnList;
    }

    private TRANSPORT_PROTOCOL getTransportProtocol()
    {
        if (udpRadioButton.isSelected())
        {
            return TRANSPORT_PROTOCOL.UDP;
        } else
        {
            return TRANSPORT_PROTOCOL.TCP;
        }
    }

    private boolean isRecursiveSet()
    {
        return recursiveQueryRadioButton.isSelected();
    }

    private boolean isDNSSECSet()
    {
        return dnssecYesRadioButton.isSelected();
    }

    private void logMessage(String dnsServer, String domain, Q_COUNT[] records, boolean recursive, boolean dnssec,
                            TRANSPORT_PROTOCOL transport, boolean dnssecRRsig, boolean holdConnection)
    {
        LOGGER.info("DNS server: " + dnsServer + "\n" + "Domain: " + domain + "\n" + "Records: " + records.toString()
                + "\n" + "Recursive:" + recursive + "\n" + "DNSSEC: " + dnssec + "\n" + "DNSSEC sig records"
                + dnssecRRsig + "\n" + "Transport protocol: " + transport + "\n" + "Hold connection: " + holdConnection
                + "\n" + "Application protocol: " + APPLICATION_PROTOCOL.DNS);
    }

    private void logMessage(String dnsServer, String domain, Q_COUNT[] records, boolean recursive, boolean dnssec,
                            TRANSPORT_PROTOCOL transport, boolean dnssecRRsig, boolean holdConnection,
                            boolean checkingdisabled)
    {
        LOGGER.info("DNS server: " + dnsServer + "\n" + "Domain: " + domain + "\n" + "Records: " + records.toString()
                + "\n" + "Recursive:" + recursive + "\n" + "DNSSEC: " + dnssec + "\n" + "DNSSEC sig records"
                + dnssecRRsig + "\n" + "Transport protocol: " + transport + "\n" + "Hold connection: " + holdConnection
                + "\n" + "Application protocol: " + APPLICATION_PROTOCOL.DNS + "\n" + "Checking disabled " + checkingdisabled);
    }


    @FXML
    protected void sendButtonFired(ActionEvent event)
    {

        try
        {
            String dnsServer = getDnsServerIp();
            if (dnsServer == null)
            {
                showAller("ChooseDNSServer");
                return;
            }
            //addServerToList();
            LOGGER.info(dnsServer);
            Q_COUNT[] records = getRecordTypes();
            TRANSPORT_PROTOCOL transport = getTransportProtocol();
            String domain = getDomain();
            boolean recursive = isRecursiveSet();
            //boolean dnssec = isDNSSECSet();
            boolean holdConnection = holdConectionCheckbox.isSelected();
            boolean caFlag = checkingDisabledCheckBox.isSelected();
            boolean adFlag = authenticateDataCheckBox.isSelected();
            boolean doFlag = DNSSECOkCheckBox.isSelected();
            logMessage(dnsServer, domain, records, recursive, adFlag, transport, doFlag, holdConnection, caFlag);
			
			/*sender = new MessageSender(recursive, dnssec, dnssecRRSig, domain, records, transport,
					APPLICATION_PROTOCOL.DNS, dnsServer);
			if (transport == TRANSPORT_PROTOCOL.TCP) {
				sender.setTcp(tcpConnection);
				sender.setCloseConnection(!holdConnection);
			} else {
				closeHoldedConnection();
			}
			sender.setInterfaceToSend(getInterface());
			sender.send();
			parser = new MessageParser(sender.getRecieveReply(), sender.getHeader(), transport);
			parser.parse();
			tcpConnection = sender.getTcp();*/
            /*setControls();*/
            DNSTaskBase dnsTask = null;
            if (transport == TRANSPORT_PROTOCOL.TCP)
            {
                dnsTask = new DNSOverTCPTask(recursive, adFlag, doFlag, holdConnection, domain, records,
                        transport, APPLICATION_PROTOCOL.DNS, dnsServer, getInterface(), caFlag);
            } else
            {
                dnsTask = new DNSOverUDPTask(recursive, adFlag, doFlag, domain, records, transport,
                        APPLICATION_PROTOCOL.DNS,
                        dnsServer, getInterface(), caFlag);
            }

            numberOfMessagesValueLabel.textProperty().bind(dnsTask.messagesSentPropertyProperty().asString());
            responseTimeValueLabel.textProperty().bind(dnsTask.durationPropertyProperty().asString());
            requestTreeView.rootProperty().bind(dnsTask.requestPropertyProperty());
            responseTreeView.rootProperty().bind(dnsTask.responsePropertyProperty());
            querySizeLabel.textProperty().bind(dnsTask.querySizeProperty().asString());
            responseSizeLabel.textProperty().bind(dnsTask.responseSizeProperty().asString());
            Thread task = new Thread(dnsTask);
            // pass new progress bar to Task
            progressBar.setProgress(0.0);
            dnsTask.setProgressBar(progressBar);
            task.start();
        } catch (NotValidDomainNameException | NotValidIPException | DnsServerIpIsNotValidException
                | MoreRecordsTypesWithPTRException | NonRecordSelectedException | IOException e)
        {
            String fullClassName = e.getClass().getSimpleName();
            LOGGER.info(fullClassName);
            showAller(fullClassName);
        } catch (Exception e)
        {
            LOGGER.warning(e.toString());
            showAller("Exception");
        }

    }

    @FXML
    private void onDnsServerNameChoiseBoxAction(ActionEvent event)
    {
		/*try {
			if (!savedDNSChoiceBox.getValue().equals(null) && !savedDNSChoiceBox.getValue().equals("")) {
				dnsServerTextField.setText(savedDNSChoiceBox.getValue());
				copyDataToClipBoard(dnsServerTextField.getText());
			}
		} catch (Exception e) {
			LOGGER.warning(e.toString());
		}*/
    }

    @FXML
    protected void domainNameKeyPressed(KeyEvent event)
    {
		/*controlKeys(event, domainNameTextField);
		autobinging(domainNameTextField.getText(), settings.getDomainNamesDNS(), savedDomainNamesChoiseBox);*/
    }

    @FXML
    private void deleteDomainNameHistoryFired(Event event)
    {
		/*settings.eraseDomainNames();
		savedDomainNamesChoiseBox.getItems().removeAll(savedDomainNamesChoiseBox.getItems());*/
    }

    @FXML
    private void deleteDNSServerHistoryFired(Event event)
    {
        settings.eraseDNSServers();
        savedDNSChoiceBox.getItems().removeAll(savedDNSChoiceBox.getItems());
    }

    @FXML
    private void transportProtocolAction(ActionEvent event)
    {
        if (tcpRadioButton.isSelected())
        {
            holdConectionCheckbox.setDisable(false);
        } else
        {
            holdConectionCheckbox.setDisable(true);
            holdConectionCheckbox.setSelected(false);
        }
    }

    @FXML
    private void holdConnectionAction(ActionEvent event) throws IOException
    {
        if (!holdConectionCheckbox.isSelected())
        {
            if (DNSTaskBase.getTcp() != null)
            {
                DNSTaskBase.getTcp().closeAll();
            }
        }
    }

    @FXML
    private void IPv4RadioButtonAction(ActionEvent event)
    {
        if (IPv4RadioButton.isSelected())
        {
            reloadDNSServers(true);
        }
    }

    private void reloadDNSServers(boolean ipv4)
    {
        for (Node node : otherDNSVbox.getChildren())
        {
            if (!(node instanceof NameServerVBox))
            {
                return;
            }
            NameServerVBox nsVBox = (NameServerVBox) node;
            if (ipv4)
            {
                nsVBox.loadIPv4();
            } else
            {
                nsVBox.loadIPv6();
            }
            if (DNSTaskBase.getTcp() != null)
            {
                try
                {
                    DNSTaskBase.getTcp().closeAll();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void IPv6RadioButtonAction(ActionEvent event)
    {
        if (IPv6RadioButton.isSelected())
        {
            reloadDNSServers(false);
        }
    }

    @FXML
    private void changeLayout(ActionEvent event)
    {
        // change to small layout
        try
        {
            FXMLLoader loader = null;
            if (layoutLarge)
            {
                loader = new FXMLLoader(getClass().getResource(FXML_FILE_NAME_SMALL));
            } else
            {
                loader = new FXMLLoader(getClass().getResource(FXML_FILE_NAME_LARGE));
            }
            Stage newStage = new Stage();

            newStage.setScene(new Scene((Parent) loader.load()));
            GeneralController controller = (GeneralController) loader.getController();

            Stage oldStage = (Stage) sendButton.getScene().getWindow();
            newStage.setX(oldStage.getX());
            newStage.setY(oldStage.getY());
            newStage.getIcons().add(new Image(App.ICON_URI));
            controller.setLanguage(language);
            controller.setSettings(settings);
            controller.setIpDns(ipDns);
            controller.setLabels();
            controller.loadDataFromSettings();
            controller.networkInterfaces();
            newStage.show();
            oldStage.close();
            layoutLarge = !layoutLarge;
        } catch (Exception e)
        {
            e.printStackTrace();
            LOGGER.severe("Could not open new window:" + e.toString());
            Alert alert = new Alert(AlertType.ERROR, language.getLanguageBundle().getString("windowError"));
            alert.showAndWait();
        }
    }

}