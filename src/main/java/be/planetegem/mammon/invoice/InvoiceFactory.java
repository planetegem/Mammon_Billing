package be.planetegem.mammon.invoice;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import be.planetegem.mammon.Mammon;
import be.planetegem.mammon.db.DbCommands;
import be.planetegem.mammon.db.DbConsole;
import be.planetegem.mammon.statics.LanguageFile;

public class InvoiceFactory extends InvoiceFactoryUI implements ActionListener {

    private Mammon parent;
    private DbConsole db;

    // invoice data
    private ArrayList<HashMap<String, String>> invoices = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> positiveInvoices = new ArrayList<HashMap<String, String>>();
    private int invoiceIndex = 0;
    
    // table variables
    private ArrayList<HashMap<String, String>> loadedTable;
    private String currentVat;

    // Getters for console & parent
    public DbConsole getDb(){
        return db;
    }
    public Mammon getAncestor(){
        return parent;
    }

    // Profile logic
    private HashMap<String, String> currentProfile = new HashMap<String, String>();

    public ProfileManager getPm(){
        return pm;
    }
    public void setCurrentProfile(HashMap<String, String> profile){
        this.currentProfile = profile;
        invoiceSelector.setSelectedItem("[nieuw]");
    }

    // Customer logic
    private HashMap<String, String> currentCustomer = new HashMap<String, String>();

    public CustomerManager getCm(){
        return cm;
    }
    public void setCurrentCustomer(HashMap<String, String> customer){
        this.currentCustomer = customer;
    }

    // Auto completes form with default values (governed by profile)
    private void setDefaults(){
        db.logEvent("Applying default values");
        if (currentProfile.size() == 0){
            placeField.setText("[plaats]");
            invoiceNumberField.setText((Year.now().getValue() % 100) + "001");
            accountField.setText("[rekeningnummer]");
        } else {
            placeField.setText(currentProfile.get("PLACENAME"));

            // Propose invoice number = year + number of positive invoices
            int count = db.getInvoiceNumber(currentProfile.get("USERID"));
            String proposal = String.format("%03d", count);
            invoiceNumberField.setText((Year.now().getValue() % 100) + proposal);

            // propose default account number
            if (!currentProfile.get("ACCOUNTNUMBER").equals("")){
                accountField.setText(currentProfile.get("ACCOUNTNUMBER"));
            } else {
                accountField.setText("[rekeningnummer]");
            }
        }
        dateField.setText(dateFormat.format(new Date()));
        paymentTermField.setText("30");
        typeSelector.setSelectedItem("FACTUUR");

        this.loadedTable = new ArrayList<HashMap<String, String>>();
        this.currentVat = "21%";
        this.direction = "POSITIVE";
        ivTable.setTable(loadedTable, direction, currentVat);
        if (currentCustomer.size() > 0){
            cm.resetCustomer();
        }
        setInvoiceHistory();
    }

    // type selector logic
    private void considerType(String selection){
        db.logEvent("Considering invoice type selection");
        // Check if language has changed (EN vs. NL)
        if (selection.equals("FACTUUR") || selection.equals("CREDITFACTUUR")){
            if (lang == LanguageFile.EN){
                setLanguage(LanguageFile.NL);
            }
        } else if (selection.equals("INVOICE") || selection.equals("CREDIT MEMO")){
            if (lang == LanguageFile.NL){
                setLanguage(LanguageFile.EN);
            }
        }
        // check if direction has changed (positive vs. negative)
        if (selection.equals("FACTUUR") || selection.equals("INVOICE")){
            if (direction.equals("NEGATIVE")){
                this.direction = "POSITIVE";
                crd.next(invoiceCardContainer);
                ivTable.setDirection(direction);
            }
        } else if (selection.equals("CREDITFACTUUR") || selection.equals("CREDIT MEMO")){
            if (direction.equals("POSITIVE")){
                this.direction = "NEGATIVE";
                crd.next(invoiceCardContainer);
                ivTable.setDirection(direction);
            }
        }
    }

    // load Invoices: called after changing profile or adding/modifying invoice
    public void setInvoiceHistory(){
        // initialize invoice lists
        if (currentProfile.size() > 0){
            this.invoices = db.getInvoices(currentProfile.get("USERID"));
            this.positiveInvoices = db.getPositiveInvoices(currentProfile.get("USERID"));
        } else {
            this.invoices = new ArrayList<HashMap<String, String>>();
            this.positiveInvoices = new ArrayList<HashMap<String, String>>();
        }
        // use invoice lists to build comboboxes
        String[] invoiceNames = new String[invoices.size() + 1];
        invoiceNames[0] = "[nieuw]";
        for (int i = 1; i <= invoices.size(); i++){
            String tempInvoiceName = "[" + i + "]";
            tempInvoiceName += " " + invoices.get(i - 1).get("INVOICENUMBER");
            if (invoices.get(i - 1).get("REALTYPE").equals("NEGATIVE")){
                tempInvoiceName += "c";
            }
            invoiceNames[i] = tempInvoiceName;
        }
        DefaultComboBoxModel<String> options = new DefaultComboBoxModel<>(invoiceNames);
        invoiceSelector.setModel(options);

        String[] positiveInvoiceNames = new String[positiveInvoices.size()];
        for (int i = 0; i < positiveInvoices.size(); i++){
            positiveInvoiceNames[i] = positiveInvoices.get(i).get("INVOICENUMBER");;
        }
        DefaultComboBoxModel<String> options2 = new DefaultComboBoxModel<>(positiveInvoiceNames);
        creditnoteSelector.setModel(options2);
        creditnoteSelector.removeActionListener(this);
        creditnoteSelector.setSelectedItem(null);
        creditnoteSelector.addActionListener(this);
    }

    // invoice loader: called when selecting invoice or creditnote 
    private void setInvoice(HashMap<String, String> currentInvoice){
        db.logEvent("Loading data from invoice");

        // from top to bottom
        placeField.setText(currentInvoice.get("INVOICEPLACE"));
        dateField.setText(currentInvoice.get("INVOICEDATE"));

        cm.setCustomer(currentInvoice.get("CUSTOMERID"));

        invoiceNumberField.setText(currentInvoice.get("INVOICENUMBER"));
        if (currentInvoice.get("REALTYPE").equals("NEGATIVE")){
            creditnoteSelector.removeActionListener(this);
            creditnoteSelector.setSelectedItem(currentInvoice.get("INVOICENUMBER"));
            creditnoteSelector.addActionListener(this);
        }

        this.loadedTable = db.getInvoiceLines(currentInvoice.get("INVOICEID"));
        this.currentVat = currentInvoice.get("VATTYPE");

        paymentTermField.setText(currentInvoice.get("INVOICETERM"));
        accountField.setText(currentInvoice.get("INVOICEACCOUNT"));

        db.logEvent("Data loaded");
    }

    // send invoice to db
    private void updateDb(){
        // Check if data is complete: customer, place, term, account, minimum one line in table
        String ivPlace = placeField.getText();
        String ivTerm = paymentTermField.getText();
        String ivAccount = accountField.getText();
        String ivNumber = invoiceNumberField.getText();

        this.loadedTable = ivTable.getTable();
        this.currentVat = ivTable.getVat();

        String message = "";
        boolean refused = false;
        if (currentCustomer.size() == 0){
            message = "Selecteer een klant alvorens de factuur te bevestigen";
            refused = true;
        } else if (loadedTable.size() == 0){
            message = "Voeg minstens 1 lijn toe alvorens de factuur te bevestigen";
            refused = true;
        } else if (ivPlace.equals("") || ivPlace.equals("[plaats]")){
            message = "Vergeet geen plaats in te vullen alvorens de factuur te bevestigen";
            refused = true;
        } else if (ivTerm.equals("") || ivAccount.equals("") || ivAccount.equals("[rekeningnummer]")) {
            message = "Vergeet geen betaaltermijn & rekeningnummer in te vullen alvorens de factuur te bevestigen";
            refused = true;
        } else if (ivNumber.equals("") && direction == "POSITIVE"){
            message = "Het factuurnummer mag niet blanco zijn";
            refused = true;
        }

        if (refused){
            db.logEvent("Invoice submission cancelled due to missing info");
            JOptionPane.showMessageDialog(this, message);
        } else {
            // Collect all invoice data
            HashMap<String, String> newInvoice = new HashMap<String, String>();
            newInvoice.put("profileId", currentProfile.get("USERID"));
            newInvoice.put("customerId", currentCustomer.get("CUSTOMERID"));
            newInvoice.put("invoiceType", typeSelector.getSelectedItem().toString());                     
            newInvoice.put("realType", direction);
            newInvoice.put("vatType", currentVat);

            if (direction.equals("NEGATIVE")){
                ivNumber = creditnoteSelector.getSelectedItem().toString();
            }
            newInvoice.put("invoiceNumber", ivNumber);
                        
            newInvoice.put("invoicePlace", ivPlace);
            newInvoice.put("invoiceDate", dateField.getText());
            newInvoice.put("invoiceTerm", ivTerm);
            newInvoice.put("invoiceAccount", ivAccount);
    
            int status = db.addInvoice(newInvoice);
            db.logEvent("Invoice submission complete");

            // retrieve invoice id to add invoice lines
            db.logEvent("Retrieving invoiceId");
            int invoiceId = db.getInvoiceId(newInvoice.get("profileId"), ivNumber, direction);
            db.logEvent("Found invoiceId: " + invoiceId);
            ivTable.pushTableArray(invoiceId);

            // reload invoices & set selector to correct position
            if (status == DbCommands.UPDATE){
                invoiceIndex = invoiceSelector.getSelectedIndex();
            }
            setInvoiceHistory();
            if (status == DbCommands.CREATE){
                invoiceSelector.setSelectedIndex(invoices.size());
            } else {
                invoiceSelector.setSelectedIndex(invoiceIndex);
            }
        }
    }

    // event handlers
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == typeSelector){
            db.logEvent("Event triggered: type selection");
            String selection = typeSelector.getSelectedItem().toString();
            considerType(selection);
        }
        if (e.getSource() == invoiceSelector){
            db.logEvent("Event triggered: invoice selection");
            String selection = invoiceSelector.getSelectedItem().toString();
            db.logEvent("Considering invoice selection");
            if (selection.equals("[nieuw]")){
                setDefaults();
            } else {
                HashMap<String, String> currentInvoice = invoices.get(invoiceSelector.getSelectedIndex() - 1);
                typeSelector.setSelectedItem(currentInvoice.get("INVOICETYPE"));
                setInvoice(currentInvoice);

                db.logEvent("Setting table from history");
                ivTable.setTable(loadedTable, currentInvoice.get("REALTYPE"), currentVat);
            }
        }
        if (e.getSource() == creditnoteSelector){
            db.logEvent("Event triggered: credit note selection");
    
            HashMap<String, String> currentInvoice = positiveInvoices.get(creditnoteSelector.getSelectedIndex());
            setInvoice(currentInvoice);

            db.logEvent("Setting table from history");
            ivTable.setTable(loadedTable, "NEGATIVE", currentVat);
        }
        if (e.getSource() == confirmInvoice){
            db.logEvent("Event triggered: invoice submission");
            updateDb();
        }
    }

    public InvoiceFactory(Mammon parent){
        super();
        this.parent = parent;
        this.db = parent.getDb();

        // Add event listeners
        typeSelector.addActionListener(this);
        invoiceSelector.addActionListener(this);
        creditnoteSelector.addActionListener(this);
        confirmInvoice.addActionListener(this);
        confirmPdf.addActionListener(this);

        db.logEvent("Calling Invoice Table");
        ivTable = new InvoiceTable(this);
        tableSection.add(ivTable);

        db.logEvent("Calling Profile Manager");
        pm = new ProfileManager(this);
        profileSection.add(pm);

        db.logEvent("Calling Customer Manager");
        cm = new CustomerManager(this);
        customerSection.add(cm);

        db.logEvent("Applying language labels");
        setLanguage(this.lang);
        
    }
}
