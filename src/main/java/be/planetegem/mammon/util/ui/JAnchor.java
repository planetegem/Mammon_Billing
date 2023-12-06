package be.planetegem.mammon.util.ui;

import java.awt.event.MouseListener;
import java.io.IOException;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;

public class JAnchor extends JLabel implements MouseListener {
    private String title;
    private String url;

    // Overloaded constructor: can construct with only url or seperate title and url
    public JAnchor(String title, String url){
        super(title);

        this.title = title;
        this.url = url;

        standardFormatting();
        addMouseListener(this);
    }
    public JAnchor(String url){
        super(url);

        this.title = url;
        this.url = url;

        standardFormatting();
        addMouseListener(this);
    }

    private void standardFormatting(){
        setForeground(Color.blue.darker());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void mousePressed(MouseEvent e) {
       // DO nothing;
    }
    public void mouseReleased(MouseEvent e) {
        // Do nothing
    }

    // Underline on enter, reverse on exit, open url on click
    public void mouseEntered(MouseEvent e) {
        String underlinedTitle = "<html><u>" + title + "</u></html>";
        setText(underlinedTitle);
    }
    public void mouseExited(MouseEvent e) {
       setText(title);
    }
    public void mouseClicked(MouseEvent e) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e1){}
    }
}
