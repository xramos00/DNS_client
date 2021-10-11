package ui;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Ip;
import models.Language;
import models.Settings;

public abstract class GeneralController {

	protected Language language;
	public static String APP_TITTLE = "title";
	public String PROTOCOL = "DNS";
	protected Settings settings;
	protected Logger LOGGER;
	protected Ip ipDns;

	@FXML
	protected Button sendButton;

	@FXML
	protected TreeView requestTreeView;

	@FXML
	protected TreeView responseTreeView;

	@FXML
	protected TextField domainNameTextField;


	@FXML
	protected CheckBox aCheckBox = new CheckBox();

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

	public Settings getSettings() {
		return settings;
	}

	public void setIpDns(Ip ip) {
		this.ipDns = ip;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
		// System.out.println("Language loads to another window");
	}

	public String getTitle()
	{
		String client = language.getLanguageBundle().getString("title");
		String protocol = getProtocol();
		return client+" "+protocol;
	}

	public abstract String getProtocol();

	public void setLabels() {
		// To be overridden
	}

	public void loadDataFromSettings() {
		// to be overridden
	}

	public void networkInterfaces() {
		try {
			interfaceToggleGroup = new ToggleGroup();
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			ArrayList<RadioMenuItem> listMenuItems = new ArrayList<RadioMenuItem>();
			while (e.hasMoreElements()) {
				RadioMenuItem pom = new RadioMenuItem();
				NetworkInterface ni = e.nextElement();
				if (ni.getName().equals(settings.getInterface().getName())) {
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

	protected NetworkInterface getInterface() {
		NetworkInterface netInterface = (NetworkInterface) interfaceToggleGroup.getSelectedToggle().getUserData();
		LOGGER.info(netInterface.getDisplayName().toString() + " " + netInterface.getName());
		settings.setInterface(netInterface);
		return netInterface;
	}

	protected void showAller(String exceptionName) {
		Alert alert = new Alert(Alert.AlertType.ERROR, language.getLanguageBundle().getString(exceptionName));
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner((Stage) sendButton.getScene().getWindow());
		alert.show();
	}

	@FXML
	protected void onDomainNameAction(ActionEvent e) {
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

}
