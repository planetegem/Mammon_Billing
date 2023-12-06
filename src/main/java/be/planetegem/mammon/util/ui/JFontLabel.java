package be.planetegem.mammon.util.ui;

import java.awt.Font;
import javax.swing.JLabel;

public class JFontLabel extends JLabel{
    public JFontLabel(String str, Font font){
        super("<html><p align=\"center\">" + str + "</p></html>");
        setFont(font);
    }
}
