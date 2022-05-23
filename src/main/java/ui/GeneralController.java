package ui;
/*
* Author - Martin Biolek
* Link - https://github.com/mbio16/clientDNS
* Moved common parts from another controllers to this controller
* */

import application.App;
import enums.Q_COUNT;
import exceptions.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Data;
import models.*;
import tasks.DNSTaskBase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Data
public abstract class GeneralController {

    public static String APP_TITTLE = "title";
    public String PROTOCOL = "DNS";
    protected static Language language = new Language();
    private ToggleGroup languageToggleGroup;
    protected Settings settings;
    protected static Logger LOGGER = Logger.getLogger(GeneralController.class.getName());
    protected Ip ipDns;

    protected ToggleGroup wiresharkFilterToogleGroup = new ToggleGroup();
    protected List<WiresharkFilter> filters;

    protected Map<String, String> parameters;
    protected ToggleGroup IPprotToggleGroup;
    protected ToggleGroup dnsserverToggleGroup;

    protected List<CheckBox> checkBoxArray;

    public static Map<String, String> requestResponseMap = new HashMap<>();

    @FXML
    CheckBox DNSSECOkCheckBox;
    @FXML
    CheckBox authenticateDataCheckBox;
    @FXML
    CheckBox checkingDisabledCheckBox;

    @FXML
    protected Menu wiresharkMenu;

    @FXML
    protected VBox vboxRoot;

    @FXML
    @Translation
    protected Button sendButton;

    @FXML
    protected TreeView requestTreeView;

    @FXML
    @Translation
    protected Label responseLabel;

    @FXML
    protected Button dnsButton;

    @FXML
    @Translation
    protected Label queryLabel;

    @FXML
    @Translation
    protected TitledPane domainNameTitledPane;

    @FXML
    @Translation
    protected Menu actionMenu;

    @FXML
    @Translation
    protected Menu historyMenu;
    @FXML
    @Translation
    protected Menu languageMenu;
    @FXML
    @Translation
    protected Menu interfaceMenu;
    @FXML
    @Translation
    protected MenuItem backMenuItem;

    @FXML
    @Translation
    protected MenuItem deleteDomainNameHistory;

    @FXML
    @Translation
    protected MenuItem deleteDNSServersHistory;

    @FXML
    @Translation
    protected TitledPane networkTitledPane;

    @FXML
    @Translation
    protected TitledPane recordTypeTitledPane;

    @FXML
    @Translation
    protected Button copyRequestJsonButton;

    @FXML
    @Translation
    protected Button copyResponseJsonButton;

    @FXML
    protected TreeView responseTreeView;

    @FXML
    protected TextField domainNameTextField;

    @FXML
    protected RadioButton IPv4RadioButton;
    @FXML
    protected RadioButton IPv6RadioButton;

    @FXML
    protected CheckBox aCheckBox;
    @FXML
    @Translation
    protected Label responseTimeLabel;
    @FXML
    protected Label responseTimeValueLabel;
    @FXML
    @Translation
    protected Label numberOfMessagesLabel;
    @FXML
    protected Label numberOfMessagesValueLabel;

    @FXML
    @Translation
    protected Label rootServerLabel;

    protected ToggleGroup interfaceToggleGroup;
    @FXML
    protected CheckBox aaaaCheckBox;
    @FXML
    protected CheckBox ptrCheckBox;
    @FXML
    protected CheckBox txtCheckBox;
    @FXML
    protected CheckBox nsecCheckBox;
    @FXML
    protected CheckBox anyCheckBox;
    @FXML
    protected CheckBox soaCheckBox;
    @FXML
    protected CheckBox dnskeyCheckBox;
    @FXML
    protected CheckBox dsCheckBox;
    @FXML
    protected CheckBox caaCheckBox;
    @FXML
    protected CheckBox cnameCheckBox;
    @FXML
    protected CheckBox nsCheckBox;
    @FXML
    protected CheckBox mxCheckBox;
    @FXML
    protected CheckBox rrsigCheckBox;
    @FXML
    protected CheckBox nsec3CheckBox;
    @FXML
    @Translation
    protected CheckBox holdConnectionCheckbox;
    @FXML
    protected CheckBox nsec3paramCheckBox;
    @FXML
    protected CheckBox dnssecRecordsRequestCheckBox;
    @FXML
    protected CheckBox cdsCheckBox;
    @FXML
    protected CheckBox cdnskeyCheckBox;
    @FXML
    protected Label querySizeLabel;
    @FXML
    protected Label responseSizeLabel;
    @FXML
    protected ProgressBar progressBar = new ProgressBar();

    @FXML
    protected VBox otherDNSVbox;

    protected NameServer nameServer = null;

    @FXML
    private ComboBox<String> dnsServerComboBox;

    @FXML
    protected ComboBox<String> savedDomainNamesChoiseBox;

    @FXML
    protected RadioMenuItem czechRadioButton;

    @FXML
    protected RadioMenuItem englishRadioButton;

    @FXML
    protected RadioButton englishLangRadioButton;
    @FXML
    protected RadioButton czechLangRadioButton;
    @FXML
    @Translation
    protected RadioButton recursiveQueryRadioButton;
    @FXML
    @Translation
    protected RadioButton iterativeQueryRadioButton;

    @FXML
    protected TitledPane ipTitledPane;

    protected ToggleGroup iterativeToggleGroup;

    @FXML
    @Translation
    protected TitledPane iterativeTitledPane;

    protected Thread thread = null;

    protected DNSTaskBase task = null;

    protected String buttonText;

    @Translation
    protected MenuItem responseMenuItem;

    @Translation
    protected MenuItem requestMenuItem;

    @Translation
    protected MenuItem darkModeMenu;

    @FXML
    @Translation
    protected RadioMenuItem darkModeMenuItem;

    protected Exception ecx = null;

    protected static boolean darkMode=false;

    public GeneralController() {
        // this is set of DNS resource records that are common to all implemented protocols
        // specific RR should be added in constructors of certain protocols
        checkBoxArray = new LinkedList<>();
        /*System.setProperty("java.net.preferIPv4Addresses","true");
        System.setProperty("java.net.preferIPv4Stack","true");*/
        /*System.setProperty("java.net.preferIPv4Stack","false");
        System.setProperty("java.net.preferIPv6Addresses","true");
        System.setProperty("java.net.preferIPv6Stack","true");*/
    }

    public void initialize() {
        setUserDataRecords();
        setCustomUserDataRecords();
        setWiresharkMenuItems();
        czechRadioButton.setToggleGroup(languageToggleGroup);
        englishRadioButton.setToggleGroup(languageToggleGroup);
        czechRadioButton.setSelected(GeneralController.language.getCurrentLanguage().equals(Language.CZECH));

        // create a menu
        ContextMenu responseContextMenu = new ContextMenu();
        ContextMenu requestContextMenu = new ContextMenu();
        responseMenuItem = new MenuItem(GeneralController.language.getLanguageBundle().getString("responseMenuItem"));
        requestMenuItem = new MenuItem(GeneralController.language.getLanguageBundle().getString("requestMenuItem"));

        // create menuitems
        responseMenuItem.setOnAction(event -> {
            getValueFromTreeViewToClipboard(responseTreeView);
            showAlert("dataCopied", Alert.AlertType.INFORMATION);
        });
        requestMenuItem.setOnAction(event -> {
            getValueFromTreeViewToClipboard(requestTreeView);
            showAlert("dataCopied", Alert.AlertType.INFORMATION);
        });
        responseContextMenu.getItems().add(responseMenuItem);
        requestContextMenu.getItems().add(requestMenuItem);

        if (GeneralController.darkMode){
            darkModeMenuItem.setSelected(true);
        }
        darkModeMenuItem.setOnAction((mouseEvent) -> {
            if (GeneralController.darkMode) {
                GeneralController.darkMode = !GeneralController.darkMode;
                clearDarkMode();
            } else {
                GeneralController.darkMode = !GeneralController.darkMode;
                setDarkMode();
            }
        });

        if (responseTreeView != null){
            responseTreeView.setContextMenu(responseContextMenu);
        }
        if (requestTreeView !=null){
            requestTreeView.setContextMenu(requestContextMenu);
        }


        //savedDomainNamesChoiseBox.getItems().addAll(settings.getDomainNamesDNS());
    }

    public String getButtonText(){
        return GeneralController.language.getLanguageBundle().getString("sendButton");
    }

    protected void clearDarkMode(){
        if (sendButton== null){
            dnsButton.getScene().getRoot().setStyle("");
        }else {
            vboxRoot.getScene().getRoot().setStyle("");
        }
    }

    protected void setDarkMode(){
        if (sendButton == null) {
            dnsButton.getScene().getRoot().setStyle(".root { \n" +
                    "    -fx-accent: #1e74c6;\n" +
                    "    -fx-focus-color: -fx-accent;\n" +
                    "    -fx-base: #373e43;\n" +
                    "    -fx-control-inner-background: derive(-fx-base, 35%);\n" +
                    "    -fx-control-inner-background-alt: -fx-control-inner-background ;\n" +
                    "}\n" +
                    "\n" +
                    ".label{\n" +
                    "    -fx-text-fill: lightgray;\n" +
                    "}\n" +
                    "\n" +
                    ".text-field {\n" +
                    "    -fx-prompt-text-fill: gray;\n" +
                    "}\n" +
                    "\n" +
                    ".titulo{\n" +
                    "    -fx-font-weight: bold;\n" +
                    "    -fx-font-size: 18px;\n" +
                    "}\n" +
                    "\n" +
                    ".button{\n" +
                    "    -fx-focus-traversable: false;\n" +
                    "}\n" +
                    "\n" +
                    ".button:hover{\n" +
                    "    -fx-text-fill: white;\n" +
                    "}\n" +
                    "\n" +
                    ".separator *.line { \n" +
                    "    -fx-background-color: #3C3C3C;\n" +
                    "    -fx-border-style: solid;\n" +
                    "    -fx-border-width: 1px;\n" +
                    "}");
        } else {
            vboxRoot.getScene().getRoot().setStyle(".root { \n" +
                "    -fx-accent: #1e74c6;\n" +
                "    -fx-focus-color: -fx-accent;\n" +
                "    -fx-base: #373e43;\n" +
                "    -fx-control-inner-background: derive(-fx-base, 35%);\n" +
                "    -fx-control-inner-background-alt: -fx-control-inner-background ;\n" +
                "}\n" +
                "\n" +
                ".label{\n" +
                "    -fx-text-fill: lightgray;\n" +
                "}\n" +
                "\n" +
                ".text-field {\n" +
                "    -fx-prompt-text-fill: gray;\n" +
                "}\n" +
                "\n" +
                ".titulo{\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-font-size: 18px;\n" +
                "}\n" +
                "\n" +
                ".button{\n" +
                "    -fx-focus-traversable: false;\n" +
                "}\n" +
                "\n" +
                ".button:hover{\n" +
                "    -fx-text-fill: white;\n" +
                "}\n" +
                "\n" +
                ".separator *.line { \n" +
                "    -fx-background-color: #3C3C3C;\n" +
                "    -fx-border-style: solid;\n" +
                "    -fx-border-width: 1px;\n" +
                "}");
        }

    }

    /*
    * Modified from Martin Biolek thesis
    * */
    protected void setUserDataRecords() {
        checkBoxArray.add(aCheckBox);
        checkBoxArray.add(aaaaCheckBox);
        checkBoxArray.add(ptrCheckBox);
        checkBoxArray.add(nsecCheckBox);
        checkBoxArray.add(txtCheckBox);
        checkBoxArray.add(anyCheckBox);
        aCheckBox.setUserData(Q_COUNT.A);
        aaaaCheckBox.setUserData(Q_COUNT.AAAA);
        ptrCheckBox.setUserData(Q_COUNT.PTR);
        nsecCheckBox.setUserData(Q_COUNT.NSEC);
        txtCheckBox.setUserData(Q_COUNT.TXT);
        anyCheckBox.setUserData(Q_COUNT.ANY);
    }

    protected abstract void setCustomUserDataRecords();

    /**
     * Method creates basic wireshark filters and respective buttons in menu
     */
    protected void setWiresharkMenuItems() {
        // wiresharkFilterToogleGroup = new ToggleGroup();
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

        for (WiresharkFilter filter : filters) {
            RadioMenuItem menuItem = null;
            if (getProtocol().equals("LLMNR") || getProtocol().equals("MDNS")) {
                menuItem = new RadioMenuItem(filter.getName());
                menuItem.setOnAction(event -> copyWiresharkFilter());
            } else {
                menuItem = new RadioMenuItem(filter.getName());
            }
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

    public static Language getLanguage() {
        return GeneralController.language;
    }

    public String getTitle() {
        String client = GeneralController.language.getLanguageBundle().getString("title");
        String protocol = getProtocol();
        return client + " " + protocol;
    }

    protected void getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

    }

    @FXML
    private void languageChanged(ActionEvent event) throws IllegalAccessException {
        GeneralController.language.changeLanguageBundle(czechLangRadioButton.isSelected());
        translateUI();
    }

    @FXML
    protected void treeViewClicked(MouseEvent event)
    {
        if (event.getClickCount() == 2)
        {
            getValueFromTreeViewToClipboard((TreeView<String>) event.getSource());
            showAlert("dataCopied", Alert.AlertType.INFORMATION);
        }
    }

    protected void getValueFromTreeViewToClipboard(TreeView<String> treeView){
        String value = treeView.getSelectionModel().getSelectedItem().getValue();
        String[] array = value.split(": ");
        if (array.length != 1){
            copyDataToClipBoard(array[1]);
        } else {
            copyDataToClipBoard(value);
        }
    }

    protected void translateUI() {
        List<Field> fields = new ArrayList<>();
        getAllFields(fields, this.getClass());
        fields = fields.stream().filter(field -> field.isAnnotationPresent(Translation.class)).collect(Collectors.toList());
        fields.forEach(field -> {
            if (field != null) {
                try {
                    if (field.get(this) != null) {
                        if (Labeled.class.isAssignableFrom(field.getType())) {
                            ((Labeled) field.get(this)).setText(GeneralController.language.getLanguageBundle().getString(field.getName()));
                        } else if (TableColumnBase.class.isAssignableFrom(field.getType())) {
                            ((TableColumnBase<?, ?>) field.get(this)).setText(GeneralController.language.getLanguageBundle().getString(field.getName()));
                        } else {
                            ((MenuItem) field.get(this)).setText(GeneralController.language.getLanguageBundle().getString(field.getName()));
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    protected void changeLanguageMenu(ActionEvent e) {
        if (e.getSource().equals(czechRadioButton)) {
            GeneralController.language.changeLanguageBundle(true);
            englishRadioButton.setSelected(false);
            translateUI();
        } else {
            GeneralController.language.changeLanguageBundle(false);
            czechRadioButton.setSelected(false);
            translateUI();
        }

    }

    public abstract String getProtocol();

    public void setLabels() {
        // To be overridden
    }

    public void loadDataFromSettings() {
        // to be overridden
    }

    /*
     * Body of method taken from Martin Biolek thesis
     * */
    public void networkInterfaces() {
        try {
            interfaceToggleGroup = new ToggleGroup();
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            ArrayList<RadioMenuItem> listMenuItems = new ArrayList<RadioMenuItem>();
            while (e.hasMoreElements()) {
                RadioMenuItem pom = new RadioMenuItem();
                NetworkInterface ni = e.nextElement();
                if (ni.getName().equals(settings.getNetInterface().getName())) {
                    pom.setSelected(true);
                }
                pom.setText(ni.getName() + " -- " + ni.getDisplayName());
                pom.setUserData(ni);
                pom.setToggleGroup(interfaceToggleGroup);
                listMenuItems.add(pom);
            }
            interfaceMenu.getItems().addAll(listMenuItems);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method returns selected network interface, which can be used to send data into network
     *
     * @return
     */
    /*
     * Body of method taken from Martin Biolek thesis
     * */
    protected NetworkInterface getInterface() {
        NetworkInterface netInterface = (NetworkInterface) interfaceToggleGroup.getSelectedToggle().getUserData();
        //LOGGER.info(netInterface.getDisplayName().toString() + " " + netInterface.getName());
        settings.setNetInterface(netInterface);
        return netInterface;
    }

    /**
     * Method creates Modal window containing translated message for user
     *
     * @param exceptionName name of property containing desired message
     */
    /*
     * Body of method taken from Martin Biolek thesis
     * */
    public void showAller(String exceptionName) {
        Alert alert = new Alert(Alert.AlertType.ERROR, GeneralController.getLanguage().getLanguageBundle().getString(exceptionName));
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner((Stage) sendButton.getScene().getWindow());
        alert.show();
    }

    public void showAlert(String messageId, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType, GeneralController.language.getLanguageBundle().getString(messageId));
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner((Stage) sendButton.getScene().getWindow());
        alert.show();
    }

    public void showAlertMultipleExceptions(List<Exception> exceptions){
        StringBuilder stringBuilder = new StringBuilder();
        exceptions.forEach(e -> stringBuilder.append(GeneralController.getLanguage().getLanguageBundle().getString(e.getClass().getSimpleName())).append("\n"));
        if(stringBuilder.length() == 0){
            return;
        }
        Alert alert = new Alert(Alert.AlertType.ERROR, stringBuilder.toString());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner((Stage) sendButton.getScene().getWindow());
        alert.show();
    }

    public void showCustomAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType, message);
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
    /*
     * Body of method taken from Martin Biolek thesis
     * */
    protected void copyDataToClipBoard(String data) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(data);
        clipboard.setContent(content);
    }

    /*
     * Body of method taken from Martin Biolek thesis and modified
     * */
    @FXML
    protected void onDomainNameAction(ActionEvent e) throws NonRecordSelectedException, MoreRecordsTypesWithPTRException, NotValidDomainNameException, UnsupportedEncodingException, UnknownHostException, NotValidIPException {
        saveToDomainChoiceBox();
        sendButtonFired(e);
    }

    protected void saveToDomainChoiceBox() {
        if (savedDomainNamesChoiseBox.getItems().stream().noneMatch(s -> s.equalsIgnoreCase(domainNameTextField.getText()))) {
            savedDomainNamesChoiseBox.getItems().add(domainNameTextField.getText());
        }
    }

    protected void setChoiceBoxValue() {
        if (domainNameTextField.getText() != null && !domainNameTextField.getText().equals("")) {
            savedDomainNamesChoiseBox.setValue(domainNameTextField.getText());
        }
    }

    @FXML
    protected void sendButtonFired(ActionEvent e) {
        saveToDomainChoiceBox();
        setChoiceBoxValue();
    }

    protected boolean isTerminatingThread() {
        if (thread != null && thread.isAlive()) { // TODO check for duplicates in TesterController
            task.stopExecution();
            while (thread.isAlive() && !thread.isInterrupted()) {
                thread.interrupt();
            }
            Platform.runLater(() -> {
                sendButton.setText(getButtonText());
                DNSTaskBase.terminateAllTcpConnections();
                progressBar.setProgress(0);
            });
            return true;
        }
        buttonText = sendButton.getText();
        Platform.runLater(() -> {sendButton.setText("Stop");
        });
        return false;
    }

    /*
     * Body of method taken from Martin Biolek thesis and modified
     * */
    @FXML
    protected void copyJsonResponseDataFired(ActionEvent event) {
        copyDataToClipBoard(GeneralController.requestResponseMap.get("response"));
    }

    /*
     * Body of method taken from Martin Biolek thesis and modified
     * */
    @FXML
    protected void copyJsonRequestDataFired(ActionEvent event) {
        copyDataToClipBoard(GeneralController.requestResponseMap.get("request"));
    }

    @FXML
    protected void onDomainNameChoiseBoxAction(ActionEvent e) {
        if (savedDomainNamesChoiseBox.getValue() != null && !savedDomainNamesChoiseBox.getValue().equals("")) {
            domainNameTextField.setText(savedDomainNamesChoiseBox.getValue());
        }
    }

    public void copyWiresharkFilter() {
        String ip;
        try {
            ip = getDnsServerIp();
            if (ip == null) {
                return;
            }
        } catch (DnsServerIpIsNotValidException | NotValidDomainNameException | UnknownHostException | NoIpAddrForDomainName e) {
            showAller(e.getClass().getSimpleName());
            return;
        }
        copyWiresharkFilter(ip);
    }

    /**
     * Function creates filter for Wireshark based on selected RadioMenuItem in main menu
     */
    public void copyWiresharkFilter(String ip) {

        String prefix;

        if (Ip.isIPv4Address(ip)) {
            prefix = "ip";
        } else {
            prefix = "ipv6";
        }

        parameters.put("ip", ip);
        parameters.put("prefix", prefix);

        updateCustomParameters();

        if (wiresharkFilterToogleGroup.getSelectedToggle() == null) {
            showAller("chooseFilter");
            return;
        }
        String out =
                ((WiresharkFilter) wiresharkFilterToogleGroup.getSelectedToggle().getUserData()).generateQuery(parameters);
        copyDataToClipBoard(out);
        showAlert("filterCopied", Alert.AlertType.INFORMATION);

    }

    @FXML
    protected void IPv4RadioButtonAction(ActionEvent event) {
        if (IPv4RadioButton.isSelected()) {
            /*System.setProperty("java.net.preferIPv4Addresses","true");
            System.setProperty("java.net.preferIPv4Stack","true");
            System.clearProperty("java.net.preferIPv6Addresses");
            System.clearProperty("java.net.preferIPv6Stack");*/
            /*System.setProperty("java.net.preferIPv4Addresses","true");
            System.setProperty("java.net.preferIPv4Stack","true");
            System.setProperty("java.net.preferIPv6Addresses","false");
            System.setProperty("java.net.preferIPv6Stack","false");*/
            reloadDNSServers(true);
        }
    }

    @FXML
    protected void IPv6RadioButtonAction(ActionEvent event) {
        if (IPv6RadioButton.isSelected()) {
            /*System.clearProperty("java.net.preferIPv4Addresses");
            System.clearProperty("java.net.preferIPv4Stack");
            System.setProperty("java.net.preferIPv6Addresses","true");
            System.setProperty("java.net.preferIPv6Stack","true");*/
            /*System.setProperty("java.net.preferIPv4Addresses","false");
            System.setProperty("java.net.preferIPv4Stack","false");
            System.setProperty("java.net.preferIPv6Addresses","true");
            System.setProperty("java.net.preferIPv6Stack","true");*/
            reloadDNSServers(false);
        }
    }

    @FXML
    private void holdConnectionAction(ActionEvent event) throws IOException {
        if (!holdConnectionCheckbox.isSelected()) {
            if (DNSTaskBase.getTcp() != null) {
                DNSTaskBase.getTcp().forEach((s, tcpConnection) -> {
                    try {
                        tcpConnection.closeAll();
                    } catch (IOException e) {
                        LOGGER.info("Connection to " + s + " couldn't be closed due to exception");
                    }
                });
            }
        }
    }


    protected void reloadDNSServers(boolean ipv4) {
        if (otherDNSVbox == null){
            return;
        }
        for (Node node : otherDNSVbox.getChildren()) {
            if (!(node instanceof NameServerVBox)) {
                return;
            }
            NameServerVBox nsVBox = (NameServerVBox) node;
            if (ipv4) {
                nsVBox.loadIPv4();
                if (nsVBox.getNameServer().getIpv4().size() == 0){
                    nsVBox.setDisable(true);
                } else {
                    nsVBox.setDisable(false);
                }
            } else {
                if (nsVBox.getNameServer().getIpv6().size() == 0){
                    nsVBox.setDisable(true);
                } else {
                    nsVBox.setDisable(false);
                }
                nsVBox.loadIPv6();
            }
            if (DNSTaskBase.getTcp() != null) {
                DNSTaskBase.getTcp().forEach((s, tcpConnection) -> {
                    try {
                        tcpConnection.closeAll();
                    } catch (IOException e) {
                        LOGGER.info("Connection to " + s + " couldn't be closed due to exception");
                    }
                });
            }
        }
    }

    /**
     * Function returns IP address, v4 or v6, of selected DNS server from DNS servers TitledPane
     *
     * @return String representation of selected DNS server's IP address, null otherwise
     * @throws DnsServerIpIsNotValidException
     * @throws UnknownHostException
     */
    protected String getDnsServerIp() throws DnsServerIpIsNotValidException, UnknownHostException, NoIpAddrForDomainName, NotValidDomainNameException {

        Toggle selected = dnsserverToggleGroup.getSelectedToggle();

        if (selected == null) {
            Platform.runLater(()->sendButton.setText(getButtonText()));
            showAller("ChooseDNSServer");
            return null;
        }

        Object userDataObject = selected.getUserData();

        String serverIp = null;

        if (userDataObject == null) {
            showAller("unsupportedIPversion");
            return null;
        }

        if (userDataObject instanceof String) {
            serverIp = (String) userDataObject;
        } else if (userDataObject instanceof NameServer) {
            serverIp = IPv4RadioButton.isSelected() ?
                    ((NameServer) userDataObject).getIpv4().get(0) :
                    ((NameServer) userDataObject).getIpv6().get(0);
        } else if (userDataObject instanceof ToggleGroup) {
            ToggleGroup group = (ToggleGroup) userDataObject;
            Toggle selectedAddress = group.getSelectedToggle();
            if (selectedAddress == null) {
                sendButton.setText(getButtonText());
                showAller("ChooseDNSServer");
                return null;
            }
            serverIp = (String) selectedAddress.getUserData();
        } else if (userDataObject instanceof TextField) {
            TextField input = (TextField) userDataObject;
            String inputString = input.getText();
            if(DomainConvert.isValidDomainName(inputString)){
                if (IPv4RadioButton.isSelected()){
                    serverIp = Ip.getIpV4OfDomainName(inputString);
                } else {
                    serverIp = Ip.getIpV6OfDomainName(inputString);
                }
                if (serverIp == null){
                    throw new NoIpAddrForDomainName();
                }
            } else if (!Ip.isIpValid(inputString)){
                throw new DnsServerIpIsNotValidException();
            } else{
                serverIp = inputString;
            }
        }

        return serverIp;

    }

    /*
     * Body of method taken from Martin Biolek thesis and modified
     * */
    @FXML
    protected void backButtonFired(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MainController.FXML_FILE_NAME), GeneralController.language.getLanguageBundle());
            Stage newStage = new Stage();
            newStage.setScene(new Scene((Parent) loader.load()));
            GeneralController controller = (GeneralController) loader.getController();
            controller.setSettings(settings);
            newStage.initModality(Modality.APPLICATION_MODAL);

            Stage oldStage = (Stage) sendButton.getScene().getWindow();
            newStage.setX(oldStage.getX());
            newStage.setY(oldStage.getY());
            newStage.getIcons().add(new Image(App.ICON_URI));
            App.stage = newStage;
            newStage.show();

            oldStage.close();
            controller.setLabels();
            controller.setIpDns(ipDns);
            if(GeneralController.darkMode){
                controller.setDarkMode();
            } else {
                controller.clearDarkMode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, GeneralController.language.getLanguageBundle().getString("windowError"));
            alert.showAndWait();
        }
    }

    /*
     * Body of method taken from Martin Biolek thesis and modified
     * */
    protected Q_COUNT[] getRecordTypes() throws MoreRecordsTypesWithPTRException, NonRecordSelectedException {
        List<Q_COUNT> list;
        list = checkBoxArray.stream().filter(CheckBox::isSelected).map(Node::getUserData).map(o -> (Q_COUNT) o).collect(Collectors.toList());
        if (list.contains(Q_COUNT.PTR) && list.size() > 1) {
            throw new MoreRecordsTypesWithPTRException();
        }
        if (list.size() == 0) {
            throw new NonRecordSelectedException();
        }
        Q_COUNT returnList[] = list.stream().toArray(Q_COUNT[]::new);
        return returnList;
    }

    /*
     * Body of method taken from Martin Biolek thesis and modified
     * */
    protected String getDomain() throws NotValidDomainNameException, UnsupportedEncodingException {
        try {
            String domain = (domainNameTextField.getText());
            LOGGER.info("Domain name: " + domain);
            if (domain.equals(".")) {
                return domain;
            }
            if (domain == "") {
                throw new NotValidDomainNameException();
            }

            if ((domain.contains(".arpa")) && ptrCheckBox.isSelected()) {
                return domain;
            }
            if ((Ip.isIPv4Address(domain) || Ip.isIpv6Address(domain)) && ptrCheckBox.isSelected()) {
                LOGGER.info("PTR record request");
                return Ip.getIpReversed(domain);
            }
            if (DomainConvert.isValidDomainName(domain)) {
                saveDomain(domain);

                return DomainConvert.encodeIDN(domain);
            } else {
                throw new NotValidDomainNameException();
            }
        } catch (Exception e) {
            LOGGER.warning(e.toString());
            throw new NotValidDomainNameException();
        }
    }

    protected abstract void saveDomain(String domain);

    protected void setLanguageRadioButton()
    {
        if (language.getCurrentLanguage().equals(Language.CZECH))
        {
            czechRadioButton.setSelected(true);
            englishRadioButton.setSelected(false);
        } else
        {
            czechRadioButton.setSelected(false);
            englishRadioButton.setSelected(true);
        }
    }

}
