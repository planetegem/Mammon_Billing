package be.planetegem.mammon.ivf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import be.planetegem.mammon.Mammon;
import be.planetegem.mammon.db.DbConsole;
import be.planetegem.mammon.statics.DocConstraints;
import be.planetegem.mammon.statics.LanguageFile;
import be.planetegem.mammon.statics.StyleSheet;
import be.planetegem.mammon.util.ResizedImage;
import be.planetegem.mammon.util.ui.JFontLabel;
import be.planetegem.mammon.util.ui.JSmallButton;

public class ProfileManagerUI extends JPanel {

    // parent and grandparent are used to interact with other components
    protected InvoiceFactory parent;
    protected Mammon grandparent;

    // data properties
    protected int profileIndex = 0;
    protected ArrayList<HashMap<String, String>> profiles;
    protected HashMap<String, String> currentProfile = new HashMap<String, String>();

    // formatting properties
    public int lang = LanguageFile.NL;
    public boolean logoAvailable;
    public int[] imageDimensions = new int[2];

    // buttons and variable swing components
    private JPanel profileButtons, profilePreview;
    protected JSmallButton removeProfile, editProfile, addProfile;
    protected JComboBox<String> selectProfile;
    private JFontLabel addressLabel, vatLabel;
    
    // Give access to console for logging purposes
    protected DbConsole db;
    public void setDb(DbConsole db){
        this.db = db;
    }

    // converts profile details to simple string (used during pdf generation)
    public String getPdfString(){
        String details = "";
        String unicodeSymbol = " \u2981 ";
        
        if (!currentProfile.get("LOGOPATH").equals("") && currentProfile.get("FAMILYNAME").equals("")){
            details += currentProfile.get("COMPANYNAME");
            details += unicodeSymbol;
        } else if (!currentProfile.get("FAMILYNAME").equals("")){
            details += currentProfile.get("FIRSTNAME") + " " + currentProfile.get("FAMILYNAME");
            details += unicodeSymbol;
        }

        // Address label
        details += currentProfile.get("STREETNAME") + " " + currentProfile.get("HOUSENUMBER");
        if (!currentProfile.get("BOXNUMBER").equals("")){
            details += " " + currentProfile.get("BOXNUMBER");
        }
        details += ", " + currentProfile.get("POSTALCODE") + " " + currentProfile.get("PLACENAME");
        if (currentProfile.get("COUNTRYNAME").equals("België")){
            details += " - " + LanguageFile.belgium[lang];
        } else {
            details += " - " + currentProfile.get("COUNTRYNAME");
        }
        details += unicodeSymbol;

        // Vat label
        details += LanguageFile.vat[lang] + " " + currentProfile.get("VATNUMBER");

        if (!currentProfile.get("ACCOUNTNUMBER").equals("")){
            details += unicodeSymbol;
            details += "IBAN " + currentProfile.get("ACCOUNTNUMBER");
        }
        return details;
    }

    // Switch to other language: called from invoice maker
    public void setLanguage(int lang){
        this.lang = lang;

        // Address label
        if (currentProfile.size() > 0){
            String str = currentProfile.get("STREETNAME") + " " + currentProfile.get("HOUSENUMBER");
            if (!currentProfile.get("BOXNUMBER").equals("")){
                str += " " + currentProfile.get("BOXNUMBER");
            }
            str += ", " + currentProfile.get("POSTALCODE") + " " + currentProfile.get("PLACENAME");
            if (currentProfile.get("COUNTRYNAME").equals("België")){
                str += " - " + LanguageFile.belgium[lang];
            } else {
                str += " - " + currentProfile.get("COUNTRYNAME");
            }
            addressLabel.setText(str);

            // Vat label
            str = LanguageFile.vat[lang] + " " + currentProfile.get("VATNUMBER");
            vatLabel.setText(str);
        }
    }

    // Clear 'canvas': called every time profiles are loaded
    protected void clearPreview(){
        profilePreview.removeAll();
    }

    // Create new profile preview: call whenever new profile is loaded/selected
    protected void setPreview(HashMap<String, String> selectedProfile){
        clearPreview();

        this.logoAvailable = true; // Checks if logo path is valid
        this.imageDimensions[0] = DocConstraints.lineWidth*DocConstraints.previewRatio;
        this.imageDimensions[1] = (int) Math.round(DocConstraints.lineHeight*DocConstraints.previewRatio*1.5);
            
        if (!currentProfile.get("LOGOPATH").equals("")){
            db.logEvent("Profile manager is loading image from path: " + currentProfile.get("LOGOPATH"));
            try {
                ResizedImage image = new ResizedImage(
                    currentProfile.get("LOGOPATH"), 
                    DocConstraints.logoWidth*DocConstraints.previewRatio, 
                    DocConstraints.logoHeight*DocConstraints.previewRatio
                );
                imageDimensions[0] = image.width;
                imageDimensions[1] = image.height;

                JLabel logo = new JLabel(new ImageIcon(image.resized));
                logo.setBackground(Color.white);
                logo.setOpaque(true);

                // Position image
                logo.setBounds(
                    (int) Math.round((DocConstraints.lineWidth*DocConstraints.previewRatio - image.width)*0.5), 
                    DocConstraints.halfLine*DocConstraints.previewRatio, 
                    image.width, 
                    image.height
                );
                db.logEvent("Logo has been loaded in Profile Manager");
                profilePreview.add(logo);

            } catch (IOException exception){
                this.logoAvailable = false;
                db.logEvent("Exception when loading logo from path: " + exception.getMessage());
            }
        }
        // If logo path is incorrect or empty, generate adhoc logo
        if (!logoAvailable || currentProfile.get("LOGOPATH").equals("")){
            db.logEvent("Creating adhoc logo");
            JLabel logo = new JLabel(
                "<html><strong>" + currentProfile.get("COMPANYNAME").toUpperCase() + "</strong></html>", 
                SwingConstants.CENTER);
            logo.setFont(StyleSheet.logoFont);
            logo.setBounds(
                0, 
                DocConstraints.halfLine*DocConstraints.previewRatio,
                imageDimensions[0], 
                imageDimensions[1]
            );
            logo.setVerticalAlignment(JLabel.BOTTOM);
            profilePreview.add(logo);
        }
        
        // Prepare container for profile details
        JPanel profileDetails = new JPanel();
        profileDetails.setBounds(
            0, 
            imageDimensions[1] + DocConstraints.lineHeight*DocConstraints.previewRatio, 
            DocConstraints.lineWidth*DocConstraints.previewRatio, 
            (DocConstraints.lineHeight + DocConstraints.halfLine)*DocConstraints.previewRatio
        );
        profileDetails.setBackground(Color.white);
        profilePreview.add(profileDetails);

        // Create labels to be added
        String unicodeSymbol = "<html>\u2981</html>";
        String str;
        Font docFont = StyleSheet.documentFont;
        
        if (logoAvailable && currentProfile.get("FAMILYNAME").equals("")){
            str = currentProfile.get("COMPANYNAME");
            profileDetails.add(new JFontLabel(str, docFont));
            profileDetails.add(new JFontLabel(unicodeSymbol, docFont));
        } else if (!currentProfile.get("FAMILYNAME").equals("")){
            str = currentProfile.get("FIRSTNAME") + " " + currentProfile.get("FAMILYNAME");
            profileDetails.add(new JFontLabel(str, docFont));
            profileDetails.add(new JFontLabel(unicodeSymbol, docFont));
        }

        // Address label
        str = currentProfile.get("STREETNAME") + " " + currentProfile.get("HOUSENUMBER");
        if (!currentProfile.get("BOXNUMBER").equals("")){
            str += " " + currentProfile.get("BOXNUMBER");
        }
        str += ", " + currentProfile.get("POSTALCODE") + " " + currentProfile.get("PLACENAME");
        if (currentProfile.get("COUNTRYNAME").equals("België")){
            str += " - " + LanguageFile.belgium[lang];
        } else {
            str += " - " + currentProfile.get("COUNTRYNAME");
        }
        addressLabel = new JFontLabel(str, docFont);
        profileDetails.add(addressLabel);
        profileDetails.add(new JFontLabel(unicodeSymbol, docFont));

        // Vat label
        str = LanguageFile.vat[lang] + " " + currentProfile.get("VATNUMBER");
        vatLabel = new JFontLabel(str, docFont);
        profileDetails.add(vatLabel);

        if (!currentProfile.get("ACCOUNTNUMBER").equals("")){
            profileDetails.add(new JFontLabel(unicodeSymbol, docFont));
            str = "IBAN " + currentProfile.get("ACCOUNTNUMBER");
            profileDetails.add(new JFontLabel(str, docFont));
        }
    }

    // Constructor prepares base UI: buttons + empty profile preview
    protected ProfileManagerUI(){
        super();

        // Segment dimensions & layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.white);
        Dimension mainPanelSize = new Dimension(
            DocConstraints.a4Width*DocConstraints.previewRatio, 
            (DocConstraints.profileHeight + DocConstraints.lineHeight)*DocConstraints.previewRatio
        );
        setMinimumSize(mainPanelSize);
        setPreferredSize(mainPanelSize);
        setMaximumSize(mainPanelSize);

        profileButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        Dimension buttonPanelSize = new Dimension(
            DocConstraints.lineWidth*DocConstraints.previewRatio, 
            DocConstraints.lineHeight*DocConstraints.previewRatio
        );
        profileButtons.setPreferredSize(buttonPanelSize);
        profileButtons.setMaximumSize(buttonPanelSize);
        profileButtons.setMinimumSize(buttonPanelSize);
        profileButtons.setBackground(Color.white);
        add(profileButtons);

        JLabel selectProfileLabel = new JLabel("Huidig profiel:");
        selectProfileLabel.setFont(StyleSheet.wizardFont);
        selectProfile = new JComboBox<String>();
        selectProfile.setFont(StyleSheet.wizardFont);
        selectProfile.setPrototypeDisplayValue("[0] Company Name goes here");
        profileButtons.add(selectProfileLabel);
        profileButtons.add(selectProfile);

        editProfile = new JSmallButton("wijzig");
        editProfile.setFont(StyleSheet.wizardFont);
        removeProfile = new JSmallButton("verwijder");
        removeProfile.setFont(StyleSheet.wizardFont);
        addProfile = new JSmallButton("nieuw");
        addProfile.setFont(StyleSheet.wizardFont);
        profileButtons.add(editProfile);
        profileButtons.add(removeProfile);
        profileButtons.add(addProfile);

        profilePreview = new JPanel();
        profilePreview.setLayout(null);
        Dimension previewPanelSize = new Dimension(
            DocConstraints.lineWidth*DocConstraints.previewRatio, 
            DocConstraints.profileHeight*DocConstraints.previewRatio
        );
        profilePreview.setPreferredSize(previewPanelSize);
        profilePreview.setMaximumSize(previewPanelSize);
        profilePreview.setMinimumSize(previewPanelSize);
        profilePreview.setBackground(Color.white);
        add(profilePreview);
    }
}
