package be.planetegem.mammon.util;

import java.awt.Insets;

import javax.swing.JButton;

public class JSmallButton extends JButton {

    public JSmallButton(String str){
        super(str);
        setMargin(new Insets(1, 3, 1, 3));
    }
    
}
