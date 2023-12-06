package be.planetegem.mammon.util.ui;

import java.awt.Component;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import be.planetegem.mammon.util.ChangeListener;

public class JDynamicField extends JTextField{
    public JDynamicField(Component parent, String text){
        super(text);
        getDocument().addDocumentListener(new ChangeListener() {
            @Override
            public void updateField(DocumentEvent e){
                parent.revalidate();
                parent.repaint();
            }
        });
    }
    public JDynamicField(Component parent){
        super();
        getDocument().addDocumentListener(new ChangeListener() {
            @Override
            public void updateField(DocumentEvent e){
                parent.revalidate();
                parent.repaint();
            }
        });
    }
}
