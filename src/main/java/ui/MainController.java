package ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import application.Config;
import application.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Language;
import models.Settings;
import org.apache.commons.configuration.ConfigurationException;
import org.json.simple.parser.ParseException;

public class MainController extends GeneralController
{
    // FXML Components
    // language radio buttons
    @FXML
    private RadioButton englishLangRadioButton;
    @FXML
    private RadioButton czechLangRadioButton;
    // button to chose protocol
    @FXML
    private Button dnsButton;
    @FXML
    private Button llmrButton;
    @FXML
    private Button mdnsButton;
    @FXML
    private Button dohButton;
    @FXML
    private Button dotButton;
    @FXML
    private Button reportBugButton;
    // labels for protocol group
    @FXML
    private Label basicDNSLabel;
    @FXML
    private Label multicastDNSLabel;
    @FXML
    private Label encryptedDNSLabel;
    // help image
    @FXML
    private Label dnsButtonHelp;
    @FXML
    private ImageView llmrButtonHelp;
    @FXML
    private ImageView mdnsButtonHelp;
    @FXML
    private ImageView dohButtonHelp;
    @FXML
    private ImageView dotButtonHelp;

    private static final String BUG_URL = "https://github.com/mbio16/clientDNS/issues";
    private ToggleGroup languagegroup;

    public static final String FXML_FILE_NAME = "/fxml/Main.fxml";

    public void initialize()
    {

        //
        LOGGER = Logger.getLogger(DNSController.class.getName());

        // setup toogle group
        languagegroup = new ToggleGroup();
        czechLangRadioButton.setToggleGroup(languagegroup);
        englishLangRadioButton.setToggleGroup(languagegroup);
        PROTOCOL = "DNS";

    }

    @FXML
    private void languageChanged(ActionEvent event)
    {
        language.changeLanguageBundle(czechLangRadioButton.isSelected());
        setLabels();
    }

    @FXML
    private void definedButtonFired(ActionEvent event)
    {

        checkFiles();

        Button button = (Button) event.getSource();
        String fxml_file = (String) button.getUserData();
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml_file), language.getLanguageBundle());
            Stage newStage = new Stage();

            newStage.setScene(new Scene((Parent) loader.load()));
            GeneralController controller = (GeneralController) loader.getController();

            Stage oldStage = (Stage) dnsButton.getScene().getWindow();
            newStage.setX(oldStage.getX());
            newStage.setY(oldStage.getY());
            newStage.getIcons().add(new Image(App.ICON_URI));
            controller.setLanguage(language);
            newStage.setTitle(controller.getTitle());
            controller.setSettings(settings);
            controller.setIpDns(ipDns);
            controller.setLabels();
            controller.loadDataFromSettings();
            controller.networkInterfaces();
            newStage.show();
            oldStage.close();

        } catch (Exception e)
        {
            e.printStackTrace();
            LOGGER.severe("Could not open new window:" + e.toString());
            Alert alert = new Alert(AlertType.ERROR, language.getLanguageBundle().getString("windowError"));
            alert.showAndWait();
        }
    }

    private void checkFiles()
    {
        String files[] = {"rootServersDomainNames", "serversDomainNames"};

        for (String file : files)
        {
            checkFile(file);
        }

        // try load config.properties
        try
        {
            Config.loadConfiguration();
        } catch (ConfigurationException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    private void checkFile(String prop)
    {
        // load root DNS servers file path
        String rootDNSFilePath = null;
        try
        {
            rootDNSFilePath = Config.getConfProperties().getString(prop);
        } catch (ConfigurationException e)
        {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle(language.getLanguageBundle().getString("errorConfigProp") + " config.properties");
            info.setContentText(language.getLanguageBundle().getString("errorConfigProp") + " config.properties");
            info.initModality(Modality.APPLICATION_MODAL);
            info.initOwner((Stage) dnsButton.getScene().getWindow());
            info.showAndWait();
            System.exit(1);
        }

        // check if file exists
        File rootDNSFile = new File(rootDNSFilePath);
        if (!rootDNSFile.exists())
        {
            Alert info = new Alert(AlertType.ERROR);
            ButtonType yesButton = new ButtonType(language.getLanguageBundle().getString("yes"),
                    ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType(language.getLanguageBundle().getString("no"), ButtonBar.ButtonData.NO);
            info.getButtonTypes().setAll(yesButton, noButton);
            info.setTitle(language.getLanguageBundle().getString("errorConfigProp") + " " + prop);
            info.setContentText(language.getLanguageBundle().getString("errorConfigProp") + " " + prop);
            info.initModality(Modality.APPLICATION_MODAL);
            info.initOwner((Stage) dnsButton.getScene().getWindow());
            info.showAndWait().ifPresent(type -> {
                if (type.getButtonData() == ButtonBar.ButtonData.YES)
                {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Open Resource File");
                    fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("All Files", "*.*"));
                    File selectedFile = fileChooser.showOpenDialog((Stage) dnsButton.getScene().getWindow());
                    if (selectedFile != null)
                    {
                        try
                        {
                            Config.getConfProperties().setProperty(prop, selectedFile.getAbsolutePath());
                            Config.getConfProperties().save();
                        } catch (ConfigurationException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @FXML
    private void buttonFired(ActionEvent event)
    {
        LOGGER.warning("Calling a module which is not implemented");
        Alert alert = new Alert(AlertType.ERROR, language.getLanguageBundle().getString("notImplemented"));
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner((Stage) dnsButton.getScene().getWindow());
        alert.showAndWait();

    }

    @FXML
    private void reportBugButtonFired(ActionEvent event)
    {
        final Desktop desktop = Desktop.getDesktop();
        try
        {
            desktop.browse(new URI(BUG_URL));
        } catch (Exception e)
        {
            Alert alert = new Alert(AlertType.ERROR, language.getLanguageBundle().getString("bugButtonError"));
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner((Stage) dnsButton.getScene().getWindow());
            alert.showAndWait();
        }
    }

    @Override
    protected void updateCustomParameters()
    {

    }

    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }

    @Override
    public String getProtocol()
    {
        return "DNS";
    }

    public void setLabels()
    {
        basicDNSLabel.setText(language.getLanguageBundle().getString(basicDNSLabel.getId()));
        dnsButton.setUserData(DNSController.FXML_FILE_NAME_SMALL);
        multicastDNSLabel.setText(language.getLanguageBundle().getString(multicastDNSLabel.getId()));
        mdnsButton.setUserData(MDNSController.FXML_FILE_NAME);
        encryptedDNSLabel.setText(language.getLanguageBundle().getString(encryptedDNSLabel.getId()));
        dohButton.setUserData(DoHController.FXML_FILE_NAME);
        reportBugButton.setText(language.getLanguageBundle().getString(reportBugButton.getId()));
        Stage stage = (Stage) basicDNSLabel.getScene().getWindow();
        stage.setTitle(language.getLanguageBundle().getString(APP_TITTLE) + " " + PROTOCOL);

        switch (language.getCurrentLanguage())
        {
            case Language.CZECH:
                czechLangRadioButton.setSelected(true);
                englishLangRadioButton.setSelected(false);
                break;
            case Language.ENGLISH:
                czechLangRadioButton.setSelected(false);
                englishLangRadioButton.setSelected(true);
                break;
            default:
                break;
        }
    }
}
