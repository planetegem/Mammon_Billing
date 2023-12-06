package be.planetegem.mammon.ivf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

public class CustomerManager extends CustomerManagerUI implements ActionListener {

    public void setCustomer(String customerId){
        selectCustomer.removeActionListener(this);
        selectCustomer.setSelectedItem(null);
        selectCustomer.addActionListener(this);

        for (int index = 0; index < customers.size(); index++){
            if (customers.get(index).get("CUSTOMERID").equals(customerId)){
                selectCustomer.setSelectedIndex(index);
                break;
            }
        }
    }
    public void resetCustomer(){
        db.logEvent("Resetting customer manager");
        this.currentCustomer = new HashMap<String, String>();
        clearPreview();

        selectCustomer.removeActionListener(this);
        selectCustomer.setSelectedItem(null);
        selectCustomer.addActionListener(this);

        // pass selection to parent
        parent.setCurrentCustomer(currentCustomer);

        // Update UI
        repaint();
        revalidate();
    }   

    public void actionPerformed(ActionEvent e){
        // If add new
        if (e.getSource() == addCustomer){
            db.logEvent("Event triggered: add new customer");
            grandparent.setCustomerWizard();
        }
        // if update
        if(e.getSource() == editCustomer){
            db.logEvent("Event triggered: edit customer");
            if (customers.size() > 0){
                grandparent.setCustomerWizard(customers.get(customerIndex));
            } else {
                db.logEvent("Edit customer event skipped: no customers to edit");
            }
        }
        // If remove
        if(e.getSource() == removeCustomer){
            db.logEvent("Event triggered: remove customer");
            if (customers.size() > 0){
                String message = "Bent u zeker dat u deze klant wenst te verwijderen?<br>";
                message += "Door deze klant te verwijderen kan data op uw bestaande facturen verdwijnen.";
                message = "<html><body align=\"center\">" + message + "</body></html>";
                String[] options = {"annuleer", "bevestig"};

                int selection = JOptionPane.showOptionDialog(this, message, "OPGELET", 0, 2, null, options, options[0]);
                if (selection == 1){
                    db.removeCustomer(currentCustomer.get("CUSTOMERID"));
                    if (customerIndex > 0){
                        customerIndex--;
                    }
                    setData(false);
                } else {
                    db.logEvent("Operation cancelled by user");
                }
            } else {
                db.logEvent("Remove customer event skipped: no customers to remove");
            }
        }
        // if select
        if (e.getSource() == selectCustomer && selectCustomer.getSelectedItem() != null){
            db.logEvent("Event triggered: customer selection");
            customerIndex = selectCustomer.getSelectedIndex();

            db.logEvent("Loading new customer data");
            this.currentCustomer = customers.get(customerIndex);
            setPreview(currentCustomer);

            // pass selection to parent
            parent.setCurrentCustomer(currentCustomer);

            // Update UI
            repaint();
            revalidate();
        }
    }
    public void setData(boolean addedNew){
        this.customers = db.getCustomers();
        this.currentCustomer = new HashMap<String, String>();

        // Update combobox options
        String[] customerNames = new String[customers.size()];
        for (int i = 0; i < customers.size(); i++){
            String tempCustomerName = "[" + (i + 1) + "]";
            tempCustomerName += " " + customers.get(i).get("COMPANYNAME");
            customerNames[i] = tempCustomerName;
        }
        DefaultComboBoxModel<String> options = new DefaultComboBoxModel<>(customerNames);
        selectCustomer.setModel(options);
        if (customerIndex == -1){
            selectCustomer.setSelectedItem(null);
        }

        // Choose selected item in combobox, which will trigger redraw
        if (customers.size() > 0 && customerIndex >=0 ){
            if (addedNew){
                selectCustomer.setSelectedIndex(customers.size() -1);
            } else {
                selectCustomer.setSelectedIndex(customerIndex);
            }
        } else {
            db.logEvent("No customers: loading empty customer to invoice");
            parent.setCurrentCustomer(currentCustomer);

            clearPreview();

            repaint();
            revalidate();
        }
    }

    public CustomerManager(InvoiceFactory parent){
        super();
        this.parent = parent;
        this.grandparent = parent.getAncestor();
        this.db = parent.getDb();
            
        // Add listeners
        addCustomer.addActionListener(this);
        editCustomer.addActionListener(this);
        removeCustomer.addActionListener(this);
        selectCustomer.addActionListener(this);

        setData(false);
    }
}
