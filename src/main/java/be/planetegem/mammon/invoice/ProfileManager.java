package be.planetegem.mammon.invoice;

import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ProfileManager extends ProfileManagerUI implements ActionListener {

    public void actionPerformed(ActionEvent e){
        // If add new
        if (e.getSource() == addProfile){
            db.logEvent("Event triggered: add new profile");
            grandparent.setProfileWizard();
        }
        // if update
        if(e.getSource() == editProfile){
            db.logEvent("Event triggered: edit profile");
            if (profiles.size() > 0){
                grandparent.setProfileWizard(profiles.get(profileIndex));
            } else {
                db.logEvent("Edit profile event skipped: no profiles to edit");
            }
        }
        // If remove
        if(e.getSource() == removeProfile){
            db.logEvent("Event triggered: remove profile");
            if (profiles.size() > 0){
                String message = "Bent u zeker dat u dit profiel wenst te verwijderen?<br>";
                message += "Door dit profiel te verwijderen worden alle gekoppelde facturen ontoegankelijk.";
                message = "<html><body align=\"center\">" + message + "</body></html>";
                String[] options = {"annuleer", "bevestig"};

                int selection = JOptionPane.showOptionDialog(this, message, "OPGELET", 0, 2, null, options, options[0]);
                if (selection == 1){
                    db.removeProfile(currentProfile.get("USERID"));
                    if (profileIndex > 0){
                        profileIndex--;
                    }
                    setData(false);
                } else {
                    db.logEvent("Operation cancelled by user");
                }
            } else {
                db.logEvent("Remove profile event skipped: no profiles to remove");
            }
        }
        // if select
        if (e.getSource() == selectProfile){
            db.logEvent("Event triggered: profile selection");
            profileIndex = selectProfile.getSelectedIndex();

            db.logEvent("Loading new profile data");
            this.currentProfile = profiles.get(profileIndex);
            setPreview(currentProfile);

            // pass selection to parent
            parent.setCurrentProfile(currentProfile);

            // Update UI
            repaint();
            revalidate();
        }
    }

    public void setData(boolean addedNew){
        this.profiles = db.getProfiles();
        this.currentProfile = new HashMap<String, String>();

        // Update combobox options
        String[] profileNames = new String[profiles.size()];
        for (int i = 0; i < profiles.size(); i++){
            String tempProfileName = "[" + (i + 1) + "]";
            tempProfileName += " " + profiles.get(i).get("COMPANYNAME");
            profileNames[i] = tempProfileName;
        }
        DefaultComboBoxModel<String> options = new DefaultComboBoxModel<>(profileNames);
        selectProfile.setModel(options);

        // Choose selected item in combobox, which will trigger redraw
        if (profiles.size() > 0){
            if (addedNew){
                selectProfile.setSelectedIndex(profiles.size() -1);
            } else {
                selectProfile.setSelectedIndex(profileIndex);
            }
        } else {
            db.logEvent("No profiles: loading empty profile to invoice");
            parent.setCurrentProfile(currentProfile);

            clearPreview();
            repaint();
            revalidate();
        }
    }

    public ProfileManager(InvoiceFactory parent){
        super();
        this.parent = parent;
        this.grandparent = parent.getAncestor();
        this.db = parent.getDb();

        // Add listeners
        addProfile.addActionListener(this);
        selectProfile.addActionListener(this);
        editProfile.addActionListener(this);
        removeProfile.addActionListener(this);

        // Load data
        setData(false);
    }
}
