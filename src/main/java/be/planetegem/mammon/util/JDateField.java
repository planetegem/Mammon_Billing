package be.planetegem.mammon.util;

import java.text.DateFormat;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.text.DateFormatter;

public class JDateField extends JFormattedTextField {
    public JDateField(DateFormat format){
        super();

        DateFormatter dateFormatter = new DateFormatter(format);
        dateFormatter.setOverwriteMode(true);
        dateFormatter.setAllowsInvalid(false);
        setFormatter(dateFormatter);
        setText(format.format(new Date()));
    }
    public JDateField(DateFormat format, boolean emptyField){

        super();

        DateFormatter dateFormatter = new DateFormatter(format);
        dateFormatter.setOverwriteMode(false);
        dateFormatter.setAllowsInvalid(false);
        setFormatter(dateFormatter);
        if (!emptyField){
            setText(format.format(new Date()));
        } else {
            setColumns(7);
        }
    }
    
}
