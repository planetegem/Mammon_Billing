package be.planetegem.mammon.util.ui;

import javax.swing.JLabel;

import be.planetegem.mammon.statics.StyleSheet;

public class JWizardLabel extends JLabel {
    public JWizardLabel(String txt){
        super("<html><body align=\"center\">" + txt + "</body></html>");
        setFont(StyleSheet.wizardFont);
    }
}
