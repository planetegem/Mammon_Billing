package be.planetegem.mammon.ivf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import be.planetegem.mammon.statics.DocConstraints;
import be.planetegem.mammon.statics.StyleSheet;
import be.planetegem.mammon.util.ui.JBlinkingLabel;
import be.planetegem.mammon.util.ui.JDateField;
import be.planetegem.mammon.util.ui.JSmallButton;

public class LineInput extends JPanel implements ActionListener{

    private InvoiceTable parent;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private JPanel descriptionContainer, dateContainer, amountContainer, priceContainer, totalContainer;
    private JSmallButton confirmButton, cancelButton;
    private JDateField dateField;
    private JFormattedTextField amountField, priceField;
    private JTextField unitField, descriptionField;

    // sets focus to description field after creation (called from InvoiceTable)
    public void setFocusToDescription(){
        descriptionField.requestFocus();
    }

    // event handlers
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == confirmButton){
            // Make hashmap of line
            HashMap<String, String> lineData = new HashMap<String, String>();
            lineData.put("description", descriptionField.getText());
            lineData.put("date", dateField.getText());
            if (amountField.getText().equals("")){
                lineData.put("amount", "/");
            } else {
                lineData.put("amount", amountField.getText());
            }
            lineData.put("unit", unitField.getText());
            lineData.put("price", priceField.getText());

            // If previous line exists, accept empty description and empty price
            if (parent.tableArray.size() > 0){
                HashMap<String, String> previousLine = parent.tableArray.get(parent.tableArray.size() - 1);
                if (lineData.get("description").equals("")){
                    lineData.replace("description", previousLine.get("description"));
                }
                if (lineData.get("price").equals("")){
                    lineData.replace("price", previousLine.get("price"));
                }
            }
            // Check if required fields have been completed
            if (lineData.get("description").equals("") || 
                lineData.get("date").equals("") || 
                lineData.get("price").equals("")
            ){
                String message = "Vul minstens een omschrijving, datum en prijs in.<br>";
                message += "Onder aantal en prijs kunnen enkel numerieke waarden ingegeven worden.<br>";
                message += "In het 2de veld onder aantal kan eventueel een eenheid vermeld worden.<br>";
                message += "De prijs is altijd uitgedrukt in €.";

                JOptionPane.showMessageDialog(this, "<html><body align=\"center\">" + message + "</body></html>");
                
            } else {        
                parent.addLine(lineData);
            }
        }
        if (e.getSource() == cancelButton){
            parent.endLineInput();
        }        
    }

    public LineInput(InvoiceTable parent){
        super();
        this.parent = parent;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.white);

        // Create containers
        JPanel header = new JPanel();
        header.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JBlinkingLabel blinkLabel = new JBlinkingLabel("!! nieuwe lijn !!", 750);
        blinkLabel.setFont(StyleSheet.captionFont);
        header.add(blinkLabel);
        Dimension containerSize = new Dimension(
            DocConstraints.lineWidth*DocConstraints.previewRatio,
            DocConstraints.halfLine*DocConstraints.previewRatio
        );
        header.setPreferredSize(containerSize);
        header.setMaximumSize(containerSize);
        header.setMinimumSize(containerSize);
        header.setBackground(StyleSheet.wizardBlue);
        Border border = BorderFactory.createMatteBorder(0, 1, 1, 1, Color.black);
        header.setBorder(border);
        add(header);

        // Create all content containers
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        add(contentContainer);

        descriptionContainer = new JPanel();
        containerSize = new Dimension(
            DocConstraints.tDescription*DocConstraints.previewRatio,
            DocConstraints.lineHeight*DocConstraints.previewRatio
        );
        descriptionContainer.setPreferredSize(containerSize);
        descriptionContainer.setBackground(Color.white);
        border = BorderFactory.createMatteBorder(0, 1, 1, 1, Color.black);
        descriptionContainer.setBorder(border);
        contentContainer.add(descriptionContainer);

        dateContainer = new JPanel();
        containerSize = new Dimension(
            DocConstraints.tDate*DocConstraints.previewRatio,
            DocConstraints.lineHeight*DocConstraints.previewRatio
        );
        dateContainer.setPreferredSize(containerSize);
        dateContainer.setBackground(Color.white);
        border = BorderFactory.createMatteBorder(0, 0, 1, 1, Color.black);
        dateContainer.setBorder(border);
        contentContainer.add(dateContainer);

        amountContainer = new JPanel();
        containerSize = new Dimension(
            DocConstraints.tNumber*DocConstraints.previewRatio,
            DocConstraints.lineHeight*DocConstraints.previewRatio
        );
        amountContainer.setPreferredSize(containerSize);
        amountContainer.setBackground(Color.white);
        amountContainer.setBorder(border);
        contentContainer.add(amountContainer);

        priceContainer = new JPanel();
        containerSize = new Dimension(
            DocConstraints.tPrice*DocConstraints.previewRatio,
            DocConstraints.lineHeight*DocConstraints.previewRatio
        );
        priceContainer.setPreferredSize(containerSize);
        priceContainer.setBackground(Color.white);
        priceContainer.setBorder(border);
        contentContainer.add(priceContainer);

        totalContainer = new JPanel();
        containerSize = new Dimension(
            DocConstraints.tTotal*DocConstraints.previewRatio,
            DocConstraints.lineHeight*DocConstraints.previewRatio
        );
        totalContainer.setPreferredSize(containerSize);
        totalContainer.setBackground(Color.white);
        totalContainer.setBorder(border);
        contentContainer.add(totalContainer);

        // buttons
        confirmButton = new JSmallButton("bevestig");
        confirmButton.setFont(StyleSheet.wizardFont);
        confirmButton.addActionListener(this);
        totalContainer.add(confirmButton);
        cancelButton = new JSmallButton("x");
        cancelButton.setFont(StyleSheet.wizardFont);
        cancelButton.addActionListener(this);
        totalContainer.add(cancelButton);

        // input fields
        descriptionField = new JTextField(17);
        descriptionField.setFont(StyleSheet.documentFont);
        descriptionField.setBorder(BorderFactory.createDashedBorder(StyleSheet.backgroundGrey));
        descriptionContainer.add(descriptionField);
        descriptionField.requestFocusInWindow();

        dateField = new JDateField(dateFormat, true);
        dateField.setFont(StyleSheet.documentFont);
        dateField.setBorder(BorderFactory.createDashedBorder(StyleSheet.backgroundGrey));
        dateContainer.add(dateField);

        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setGroupingUsed(false);

        amountField = new JFormattedTextField(formatter);
        amountField.setColumns(5);
        amountField.setFont(StyleSheet.documentFont);
        amountField.setBorder(BorderFactory.createDashedBorder(StyleSheet.backgroundGrey));
        amountContainer.add(amountField);
        unitField = new JTextField(4);
        unitField.setFont(StyleSheet.documentFont);
        unitField.setBorder(BorderFactory.createDashedBorder(StyleSheet.backgroundGrey));
        amountContainer.add(unitField);

        priceField = new JFormattedTextField(formatter);
        priceField.setColumns(5);
        priceField.setFont(StyleSheet.documentFont);
        priceField.setBorder(BorderFactory.createDashedBorder(StyleSheet.backgroundGrey));
        priceContainer.add(priceField);
    }
}
