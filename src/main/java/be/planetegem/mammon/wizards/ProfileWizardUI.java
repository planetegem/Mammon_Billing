package be.planetegem.mammon.wizards;

import be.planetegem.mammon.statics.StyleSheet;
import be.planetegem.mammon.util.ui.JWizardHeader;
import be.planetegem.mammon.util.ui.JWizardLabel;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.HashMap;
import java.awt.Color;

public class ProfileWizardUI extends JInternalFrame {

    public final int previewWidth = 450;
    public final int previewHeight = 150;

    private JLabel logoPreview;
    private JWizardLabel introText;

    protected JTextField cnInput, vatInput, accountInput, vnInput, fnInput, streetInput, houseInput, boxInput, placeInput, pcInput, countryInput;
    protected JButton addLogo, submitForm;
    
    
    
    // UI update methods
    protected void setLogoPreview(Icon img){
        this.logoPreview.setIcon(img);
    }
    protected void completeForm(HashMap<String, String> currentProfile){
        // Change header
        String content = "<html><body align=\"center\">Welkom terug, " + currentProfile.get("COMPANYNAME") + "!<br>";
        content += "Vul onderstaande gegevens in om uw profiel aan te passen.</body></html>";
        introText.setText(content);

        // Fill in text fields
        cnInput.setText(currentProfile.get("COMPANYNAME"));
        vatInput.setText(currentProfile.get("VATNUMBER"));
        accountInput.setText(currentProfile.get("ACCOUNTNUMBER"));
        vnInput.setText(currentProfile.get("FIRSTNAME"));
        fnInput.setText(currentProfile.get("FAMILYNAME"));
        streetInput.setText(currentProfile.get("STREETNAME"));
        houseInput.setText(currentProfile.get("HOUSENUMBER"));
        boxInput.setText(currentProfile.get("BOXNUMBER"));
        placeInput.setText(currentProfile.get("PLACENAME"));
        pcInput.setText(currentProfile.get("POSTALCODE"));
        countryInput.setText(currentProfile.get("COUNTRYNAME"));
    }

    // Constructor: builds entire UI
    protected ProfileWizardUI(){
        super("PROFIEL WIZARD", false, true, false, false);

        // Wizard size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension wizardSize = new Dimension (600, 700);
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
        String content = "<html><body align=\"center\">Welkom nieuwe gebruiker!";
        content += "<br>Vul onderstaande gegevens in om een profiel aan te maken.</body></html>";
        introText = new JWizardLabel(content);
        introText.setForeground(Color.gray.darker());

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 4;
        c.insets = new Insets(5, 0, 5, 0);
        c.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(introText, c);
        getContentPane().add(introText);

        // 1. Logo
        JWizardHeader logoHeader = new JWizardHeader("<html><u>1. Logo</u></html>");
        c.gridy = 1;
        c.insets = new Insets(5, 0, 0, 0);
        layout.setConstraints(logoHeader, c);
        getContentPane().add(logoHeader);

        logoPreview = new JLabel();
        logoPreview.setHorizontalAlignment(JLabel.CENTER);
        logoPreview.setPreferredSize(new Dimension(previewWidth, previewHeight));
        logoPreview.setOpaque(true);
        logoPreview.setBackground(Color.white);
        logoPreview.setBorder(BorderFactory.createLineBorder(Color.black));
        c.gridy = 2;
        c.insets = new Insets(10, 0, 5, 0);
        layout.setConstraints(logoPreview, c);
        getContentPane().add(logoPreview);

        addLogo = new JButton("Kies een afbeelding");
        addLogo.setFont(StyleSheet.wizardFont);
        c.gridy = 3;
        c.ipady = 2;
        c.insets = new Insets(0, 0, 5, 0);
        layout.setConstraints(addLogo, c);
        getContentPane().add(addLogo);

        content = "(*) Afbeeldingen in png, bmp of jpg-formaat. Landschapsoriëntie is aangeraden.<br>";
        content += "(**) Optioneel. Als u geen logo kiest, zal uw handelnaam als hoofding gebruikt worden.";
        JWizardLabel logoSpecs = new JWizardLabel(content);
        c.gridy = 4;
        c.insets = new Insets(0, 0, 0, 0);
        layout.setConstraints(logoSpecs, c);
        getContentPane().add(logoSpecs);

        // 2. Company details
        JWizardHeader detailHeader = new JWizardHeader("<html><u>2. Bedrijfsgegevens</u></html>");
        c.gridy = 5;
        c.insets = new Insets(15, 0, 10, 0);
        layout.setConstraints(detailHeader, c);
        getContentPane().add(detailHeader);

        JWizardLabel cnLabel = new JWizardLabel("Handelsnaam* :");
        c.gridy = 6;
        c.gridwidth = 2;
        c.weightx = 0.5;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(0, 0, 0, 10);
        layout.setConstraints(cnLabel, c);
        getContentPane().add(cnLabel);
        cnInput = new JTextField(20);
        c.gridx = 2;
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(cnInput, c);
        getContentPane().add(cnInput);

        JWizardLabel vatLabel = new JWizardLabel("BTW-nummer* :");
        c.gridy = 7;
        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        layout.setConstraints(vatLabel, c);
        getContentPane().add(vatLabel);
        vatInput = new JTextField(20);
        c.gridx = 2;
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(vatInput, c);  
        getContentPane().add(vatInput);

        JWizardLabel accountLabel = new JWizardLabel("Rekeningnummer :");
        c.gridy = 8;
        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        layout.setConstraints(accountLabel, c);
        getContentPane().add(accountLabel);
        accountInput = new JTextField(20);
        c.gridx = 2;
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(accountInput, c);
        getContentPane().add(accountInput);
        
        // 3. Personal details
        JWizardHeader personalHeader = new JWizardHeader("<html><u>3. Zaakvoerder</u></html>");
        c.gridy = 9;
        c.gridwidth = 4;
        c.gridx = 0;
        c.insets = new Insets(15, 0, 10, 0);
        c.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(personalHeader, c);
        getContentPane().add(personalHeader);

        JPanel vnContainer = new JPanel();
        vnContainer.setBackground(StyleSheet.wizardBlue);
        JWizardLabel vnLabel = new JWizardLabel("Voornaam :");
        vnContainer.add(vnLabel);
        vnInput = new JTextField(12);
        c.gridy = 10;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(0, 0, 0, 10);
        layout.setConstraints(vnContainer, c);
        vnContainer.add(vnInput);
        getContentPane().add(vnContainer);

        JPanel fnContainer = new JPanel();
        fnContainer.setBackground(StyleSheet.wizardBlue);
        JWizardLabel fnLabel = new JWizardLabel("Familienaam :");
        fnContainer.add(fnLabel);
        fnInput = new JTextField(20);
        c.gridx = 2;
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(fnContainer, c);
        fnContainer.add(fnInput);
        getContentPane().add(fnContainer);

        // 3. Address details
        JWizardHeader adHeader = new JWizardHeader("<html><u>3. Adresgegevens</u></html>");
        c.gridy = 11;
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
        c.gridy = 12;
        c.insets = new Insets(0, 0, 0, 0);
        c.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(adContainer, c);
        adContainer.add(boxInput);
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
        c.gridy = 13;
        layout.setConstraints(placeContainer, c);
        placeContainer.add(countryInput);        
        getContentPane().add(placeContainer);

        // 4. Final warning & submit
        JWizardLabel optionalDetails = new JWizardLabel("(*) verplichte inbreng");
        c.gridy = 14;
        c.insets = new Insets(10, 0, 0, 0);
        layout.setConstraints(optionalDetails, c);
        getContentPane().add(optionalDetails);

        submitForm = new JButton("Indienen");
        submitForm.setFont(StyleSheet.headerFont);
        c.gridy = 15;
        c.ipady = 2;
        c.insets = new Insets(10, 0, 10, 0);
        layout.setConstraints(submitForm, c);
        getContentPane().add(submitForm);
        
        pack();
    }
}