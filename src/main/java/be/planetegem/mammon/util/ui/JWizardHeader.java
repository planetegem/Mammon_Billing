package be.planetegem.mammon.util.ui;

import javax.swing.JLabel;

import be.planetegem.mammon.statics.StyleSheet;

public class JWizardHeader extends JLabel {
    public JWizardHeader(String txt){
        super(txt);
        setFont(StyleSheet.headerFont);
    }
}
