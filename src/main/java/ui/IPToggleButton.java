package ui;

import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;

public class IPToggleButton extends ToggleButton {

    public RadioButton getControlButton()
    {
        return controlButton;
    }

    public void setControlButton(RadioButton controlButton)
    {
        this.controlButton = controlButton;
    }

    private RadioButton controlButton;

    public IPToggleButton(RadioButton controlButton)
    {
        super();
        this.controlButton = controlButton;
    }

}
