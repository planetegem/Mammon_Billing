package be.planetegem.mammon.wizards;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import be.planetegem.mammon.statics.StyleSheet;
import be.planetegem.mammon.util.ui.JWizardHeader;
import be.planetegem.mammon.util.ui.JWizardLabel;

public class CustomerWizardUI extends JInternalFrame {

    protected JRadioButton domestic, international;
    protected JTextField cnInput, vatInput, vatInput2, vnInput, fnInput, streetInput, houseInput, boxInput, placeInput, pcInput, countryInput;
    protected JButton submitForm;

    private JWizardLabel vatLabel;

    // UI update methods
    protected void completeForm(HashMap<String, String> currentCustomer){
        cnInput.setText(currentCustomer.get("COMPANYNAME"));

        if (currentCustomer.get("CUSTOMERTYPE").equals("international")){
            international.setSelected(true);
        }
        vatInput.setText(currentCustomer.get("VATNUMBER"));
        vatInput2.setText(currentCustomer.get("VATNUMBER2"));

        vnInput.setText(currentCustomer.get("FIRSTNAME"));
        fnInput.setText(currentCustomer.get("FAMILYNAME"));
        streetInput.setText(currentCustomer.get("STREETNAME"));
        houseInput.setText(currentCustomer.get("HOUSENUMBER"));
        boxInput.setText(currentCustomer.get("BOXNUMBER"));

        placeInput.setText(currentCustomer.get("PLACENAME"));
        pcInput.setText(currentCustomer.get("POSTALCODE"));
        countryInput.setText(currentCustomer.get("COUNTRYNAME"));
    }

    protected void updateClientType(boolean internationalClient){
        if (internationalClient){
            vatLabel.setText("Fiscale gegevens :");
            vatInput.setText("");
            vatInput.setColumns(25);
            vatInput2.setVisible(true);
            pack();
        } else {
            vatLabel.setText("BTW-nummer* :");
            vatInput.setText("");
            vatInput.setColumns(20);
            vatInput2.setText("");
            vatInput2.setVisible(false);
            pack();
        }
    }

    // Constructor: builds entire UI
    CustomerWizardUI(){
        super("KLANT WIZARD", false, true, false, false);

        // Wizard dimensions & layout
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension wizardSize = new Dimension (600, 480);
        int leftMargin = (int) Math.round((screenSize.getWidth() - wizardSize.width)*0.5);
        int topMargin = (int) Math.round((screenSize.getHeight() - wizardSize.height)*0.25);

        setPreferredSize(wizardSize);
        setLocation(leftMargin, topMargin);
        setVisible(true);
        
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        getContentPane().setLayout(layout);
        getContentPane().setBackground(StyleSheet.wizardBlue);

        // 0. Intro
        String content = "Vul onderstaande gegevens in om een klantprofiel aan te maken of te wijzigen.";
        JWizardLabel introText = new JWizardLabel(content);
        introText.setForeground(Color.gray.darker());
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 4;
        c.insets = new Insets(5, 0, 5, 0);
        c.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(introText, c);
        getContentPane().add(introText);

        // 1. Customer details 
        JWizardHeader customerHeader = new JWizardHeader("<html><u>1. Klantgegevens</u></html>");
        c.gridy = 1;
        c.insets = new Insets(5, 0, 0, 0);
        layout.setConstraints(customerHeader, c);
        getContentPane().add(customerHeader);

        JPanel radioContainer = new JPanel();
        domestic = new JRadioButton("Domestieke klant", true);
        domestic.setFont(StyleSheet.wizardFont);
        domestic.setBackground(StyleSheet.wizardBlue);
        radioContainer.add(domestic);
        international = new JRadioButton("Internationale klant", false);
        international.setFont(StyleSheet.wizardFont);
        international.setBackground(StyleSheet.wizardBlue);
        radioContainer.add(international);
        radioContainer.setBackground(StyleSheet.wizardBlue);
        c.gridy = 2;
        c.gridx = 0;
        c.gridwidth = 4;
        c.insets = new Insets(5, 0, 0, 0);
        layout.setConstraints(radioContainer, c);
        getContentPane().add(radioContainer);

        ButtonGroup customerTypeOptions = new ButtonGroup();
        customerTypeOptions.add(domestic);
        customerTypeOptions.add(international);

        JWizardLabel cnLabel = new JWizardLabel("Naam* :");
        c.gridy = 3;
        c.gridx = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(10, 0, 0, 10);
        layout.setConstraints(cnLabel, c);
        getContentPane().add(cnLabel);
        cnInput = new JTextField(20);
        c.gridx = 2;
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(cnInput, c);
        getContentPane().add(cnInput);

        vatLabel = new JWizardLabel("BTW-nummer* :");
        c.gridy = 4;
        c.gridx = 0;
        c.insets = new Insets(0, 0, 0, 10);
        c.anchor = GridBagConstraints.EAST;
        layout.setConstraints(vatLabel, c);
        getContentPane().add(vatLabel);
        vatInput = new JTextField(20);
        c.gridx = 2;
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(vatInput, c);
        getContentPane().add(vatInput);
        vatInput2 = new JTextField(25);
        c.gridy = 5;
        layout.setConstraints(vatInput2, c);
        vatInput2.setVisible(false);
        getContentPane().add(vatInput2);
        
        // 2. Contact person
        JWizardHeader contactHeader = new JWizardHeader("<html><u>2. Contactpersoon</u></html>");
        c.gridy = 6;
        c.gridwidth = 4;
        c.gridx = 0;
        c.insets = new Insets(15, 0, 10, 0);
        c.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(contactHeader, c);
        getContentPane().add(contactHeader);

        JPanel vnContainer = new JPanel();
        vnContainer.setBackground(StyleSheet.wizardBlue);
        JWizardLabel vnLabel = new JWizardLabel("Voornaam :");
        vnContainer.add(vnLabel);
        vnInput = new JTextField(12);
        vnContainer.add(vnInput);
        c.gridy = 7;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(0, 0, 0, 10);
        layout.setConstraints(vnContainer, c);
        getContentPane().add(vnContainer);

        JPanel fnContainer = new JPanel();
        fnContainer.setBackground(StyleSheet.wizardBlue);
        JWizardLabel fnLabel = new JWizardLabel("Familienaam :");
        fnContainer.add(fnLabel);
        fnInput = new JTextField(20);
        fnContainer.add(fnInput);
        c.gridx = 2;
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(fnContainer, c);
        getContentPane().add(fnContainer);

        JWizardLabel contactAdvice = new JWizardLabel("(*) Vul enkel een contactpersoon in als deze op de factuur moet komen");
        c.gridy = 8;
        c.gridwidth = 4;
        c.gridx = 0;
        c.insets = new Insets(5, 0, 0, 0);
        c.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(contactAdvice, c);
        getContentPane().add(contactAdvice);
        
        // 3. Address details
        JWizardHeader adHeader = new JWizardHeader("<html><u>3. Adresgegevens</u></html>");
        c.gridy = 9;
        c.gridwidth = 4;
        c.gridx = 0;
        c.insets = new Insets(15, 0, 10, 0);
        c.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(adHeader, c);
        getContentPane().add(adHeader);        

        JPanel adContainer = new JPanel();
        adContainer.setBackground(StyleSheet.wizardBlue);
        JWizardLabel streetLabel = new JWizardLabel("Straat* :");
        adContainer.add(streetLabel);
        streetInput = new JTextField(15);
        adContainer.add(streetInput);
        JWizardLabel houseLabel = new JWizardLabel("Huisnr* :");
        adContainer.add(houseLabel);
        houseInput = new JTextField(3);
        adContainer.add(houseInput);
        JWizardLabel boxLabel = new JWizardLabel("Busnr :");
        adContainer.add(boxLabel);
        boxInput = new JTextField(3);
        adContainer.add(boxInput);
        c.gridy = 10;
        c.insets = new Insets(0, 0, 0, 0);
        c.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(adContainer, c);
        getContentPane().add(adContainer);

        JPanel placeContainer = new JPanel();
        placeContainer.setBackground(StyleSheet.wizardBlue);
        JWizardLabel pcLabel = new JWizardLabel("Postcode* :");
        placeContainer.add(pcLabel);
        pcInput = new JTextField(4);
        placeContainer.add(pcInput);
        JWizardLabel placeLabel = new JWizardLabel("Plaats* :");
        placeContainer.add(placeLabel);
        placeInput = new JTextField(12);
        placeContainer.add(placeInput);
        JWizardLabel countryLabel = new JWizardLabel("Land* :");
        placeContainer.add(countryLabel);
        countryInput = new JTextField(8);
        placeContainer.add(countryInput);
        c.gridy = 11;
        layout.setConstraints(placeContainer, c);        
        getContentPane().add(placeContainer);
        
        // 4. Last warning & submit
        JWizardLabel optionalDetails = new JWizardLabel("<html>(*) verplichte inbreng</html>");
        c.gridy = 12;
        c.insets = new Insets(10, 0, 0, 0);
        layout.setConstraints(optionalDetails, c);
        getContentPane().add(optionalDetails);

        submitForm = new JButton("Indienen");
        submitForm.setFont(StyleSheet.headerFont);
        c.gridy = 13;
        c.ipady = 2;
        c.insets = new Insets(10, 0, 10, 0);
        layout.setConstraints(submitForm, c);
        getContentPane().add(submitForm);

        pack();
    }
}
