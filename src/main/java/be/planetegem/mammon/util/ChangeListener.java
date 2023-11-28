package be.planetegem.mammon.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public interface ChangeListener extends DocumentListener {
    void updateField(DocumentEvent e);

    @Override
    default void insertUpdate(DocumentEvent e){
        updateField(e);
    }
    @Override
    default void removeUpdate(DocumentEvent e) {
        updateField(e);
    }
    @Override
    default void changedUpdate(DocumentEvent e) {
        updateField(e);
    }
}
