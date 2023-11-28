package be.planetegem.mammon.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import be.planetegem.mammon.Mammon;
import be.planetegem.mammon.db.DbConsole;
import be.planetegem.mammon.util.JResizedImage;

public class ProfileWizard extends ProfileWizardUI implements ActionListener {

    private Mammon parent;
    private DbConsole db;
    private HashMap<String, String> currentProfile;
    private boolean editMode;
    private String logoPath = "";
    
    
    public void actionPerformed(ActionEvent e) {
        // Form submit logic: convert form data to hashmap -> check required fields -> save to db
        if (e.getSource() == submitForm){
            HashMap<String, String> formData = new HashMap<String, String>();
            formData.put("companyName", cnInput.getText());
            formData.put("vatNumber", vatInput.getText());
            formData.put("accountNumber", accountInput.getText());
            formData.put("firstName", vnInput.getText());
            formData.put("familyName", fnInput.getText());
            formData.put("streetName", streetInput.getText());
            formData.put("houseNumber", houseInput.getText());
            formData.put("boxNumber", boxInput.getText());
            formData.put("placeName", placeInput.getText());
            formData.put("postalCode", pcInput.getText());
            formData.put("countryName", countryInput.getText());
            formData.put("logoPath", logoPath);

            if (
                formData.get("companyName").equals("") || formData.get("vatNumber").equals("") || 
                formData.get("streetName").equals("") || formData.get("houseNumber").equals("") || 
                formData.get("placeName").equals("") || formData.get("postalCode").equals("") ||
                formData.get("countryName").equals("")
                ){
                    db.logEvent("Profile modification refused: missing data");
                    JOptionPane.showMessageDialog(this, "Niet alle verplichte velden zijn ingevuld.");
            } else {
                if(editMode){
                    db.editProfile(formData, currentProfile.get("USERID"));
                } else {
                    db.addProfile(formData);
                }
                this.dispose();
                parent.getIvf().getPm().setData(!editMode);
            }
        }

        // Logo logic: select file with filechooser -> generate preview image in form & save image path
        if (e.getSource() == addLogo){
            db.logEvent("Profile wizard: looking for image");

            JFileChooser fc = new JFileChooser();
            fc.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "bmp"));
            fc.setAcceptAllFileFilterUsed(false);

            int option = fc.showOpenDialog(parent);
            if (option == JFileChooser.APPROVE_OPTION){
                logoPath = fc.getSelectedFile().getPath();
                db.logEvent("Image selected: " + logoPath);

                try {
                    JResizedImage image = new JResizedImage(logoPath, previewWidth - 30, previewHeight - 10);
                    setLogoPreview(new ImageIcon(image.resized));
                    db.logEvent("Image resized and loaded");

                } catch (IOException exception){
                    db.logEvent("Exception while loading image from path: " + exception.getMessage());

                };

                repaint();
                revalidate();
                pack();
            }
        }
    }

    // Standard profile wizard: editMode is false, so new profile is created
    public ProfileWizard(Mammon parent){
        super();
        this.parent = parent;
        this.db = parent.getDb();
        this.editMode = false;
        
        // Event listeners
        addLogo.addActionListener(this);
        submitForm.addActionListener(this);
    }

    // Wizard in editMode: existing profile is modified
    public ProfileWizard(Mammon parent, HashMap<String, String> currentProfile){
        super();
        this.parent = parent;
        this.db = parent.getDb();
        this.editMode = true;
        this.currentProfile = currentProfile;

        // Recall logo & complete form data
        if (currentProfile.get("LOGOPATH") != null){
            logoPath = currentProfile.get("LOGOPATH");
            db.logEvent("Trying to load image from path: " + logoPath);

            try {
                JResizedImage image = new JResizedImage(currentProfile.get("LOGOPATH"), previewWidth - 30, previewHeight - 10);
                setLogoPreview(new ImageIcon(image.resized));
                db.logEvent("Image resized and loaded");
                        
            } catch (IOException e){
                db.logEvent("Exception while loading image from path: " + e.getMessage());

            };
        }
        completeForm(currentProfile);

        // Event listeners
        addLogo.addActionListener(this);
        submitForm.addActionListener(this);
    }
}
