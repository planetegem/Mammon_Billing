package be.planetegem.mammon.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.JOptionPane;

import be.planetegem.mammon.Mammon;
import be.planetegem.mammon.db.DbConsole;

public class CustomerWizard extends CustomerWizardUI implements ItemListener, ActionListener {
    private Mammon parent;
    private DbConsole db;
    private HashMap<String, String> currentCustomer;
    private boolean editMode;
    private boolean internationalClient = false;
    
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == submitForm){
            HashMap<String, String> formData = new HashMap<String, String>();
            formData.put("companyName", cnInput.getText());
            formData.put("vatNumber", vatInput.getText());
            formData.put("vatNumber2", vatInput2.getText());
            formData.put("firstName", vnInput.getText());
            formData.put("familyName", fnInput.getText());
            formData.put("streetName", streetInput.getText());
            formData.put("houseNumber", houseInput.getText());
            formData.put("boxNumber", boxInput.getText());
            formData.put("placeName", placeInput.getText());
            formData.put("postalCode", pcInput.getText());
            formData.put("countryName", countryInput.getText());

            if (
                formData.get("companyName").equals("") || formData.get("streetName").equals("") || 
                formData.get("houseNumber").equals("") || formData.get("placeName").equals("") || 
                formData.get("postalCode").equals("") || formData.get("countryName").equals("") ||
                (formData.get("vatNumber").equals("") && !internationalClient)
                ){
                    JOptionPane.showMessageDialog(this, "Niet alle verplichte velden zijn ingevuld.");
                    db.logEvent("Customer modification refused: missing data");
            } else {
                if (!internationalClient){
                    formData.put("customerType", "domestic");  
                } else {
                    formData.put("customerType", "international");
                }
                if(editMode){
                    db.editCustomer(formData, currentCustomer.get("CUSTOMERID"));
                } else {
                    db.addCustomer(formData);
                }
                this.dispose();
                parent.getIvf().getCm().setData(!editMode);
            }
        }
    }
    
    public void itemStateChanged(ItemEvent e){
        if (e.getSource() == domestic){
            this.internationalClient = false;
        }
        if (e.getSource() == international){
            this.internationalClient = true;
        }
        updateClientType(this.internationalClient);
    }

    // Standard customer wizard: editMode is false, so new customer is created
    public CustomerWizard(Mammon parent){
        super();
        this.parent = parent;
        this.db = parent.getDb();
        this.editMode = false;

        // add listeners
        domestic.addItemListener(this);
        international.addItemListener(this);
        submitForm.addActionListener(this);
    }

    // Wizard in editMode: existing customer is modified
    public CustomerWizard(Mammon parent, HashMap<String, String> customer){
        super();
        this.parent = parent;
        this.db = parent.getDb();
        this.editMode = true;
        this.currentCustomer = customer;

        completeForm(customer);

        // add listeners
        domestic.addItemListener(this);
        international.addItemListener(this);
        submitForm.addActionListener(this);
    }
}
