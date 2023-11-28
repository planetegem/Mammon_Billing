package be.planetegem.mammon.invoice;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import be.planetegem.mammon.Mammon;
import be.planetegem.mammon.db.DbConsole;
import be.planetegem.mammon.statics.LanguageFile;

public class InvoiceMaker extends InvoiceMakerUI implements ActionListener, ItemListener {
    
    // components
    private Mammon parent;
    private DbConsole db;
    private ProfileManager profileManager;
    private CustomerManager customerManager;
    private InvoiceTable invoiceTable;

    boolean ignoreCreditSelector = false;
    boolean ignoreInvoiceTypeSelector = false;
    public int lang = LanguageFile.NL;

    
    // Getters for console & parent
    public DbConsole getDb(){
        return db;
    }
    public Mammon getParent(){
        return parent;
    }

    // Profile logic
    private HashMap<String, String> currentProfile = new HashMap<String, String>();

    public ProfileManager getPm(){
        return profileManager;
    }
    public void setCurrentProfile(HashMap<String, String> profile){
        this.currentProfile = profile;
        System.out.println(profile);
    }

    // Auto completes form with default values (governed by profile)
    public void setDefaults(){
        if (currentProfile.size() == 0){
            invoicePlace = "[plaats]";
            invoiceAccount = "[rekeningnummer]";
            invoiceNumber = (Year.now().getValue() % 100) + "001";
        } else {
            invoicePlace = currentProfile.get("PLACENAME");
            if (currentProfile.get("ACCOUNTNUMBER") == null){
                invoiceAccount = "[rekeningnummer]";
            } else {
                invoiceAccount = currentProfile.get("ACCOUNTNUMBER");
            }
            int count = db.getInvoiceNumber(currentProfile.get("USERID"));
            invoiceNumber = String.format("%03d", count);
            invoiceNumber = (Year.now().getValue() % 100) + invoiceNumber;
        }
        invoiceDate = dateFormat.format(new Date());
        invoiceTerm = "30";

        // to be verified later
        currentCustomer = 0;

        invoiceType = "FACTUUR";        
        ignoreCreditSelector = true;
        currentPositiveInvoice = -1;


        // transfer data to fields        
        invoiceNumberField.setText(invoiceNumber);
        invoiceAccountField.setText(invoiceAccount);
        invoicePlaceField.setText(invoicePlace);
        invoiceDateField.setText(invoiceDate);
        invoiceTermField.setText(invoiceTerm);

    }


    // 2. Customer logic
    int currentCustomer = 0; // = index + 1 in customers ArrayList, 0 if nothing selected
    ArrayList<HashMap<String, String>> customers;

    
    // 3. invoice details
    String invoiceType, invoicePlace, invoiceDate, invoiceAccount, invoiceTerm, invoiceNumber, vatType;
    ArrayList<HashMap<String, String>> invoices, positiveInvoices;
    int currentInvoice, currentPositiveInvoice;

    // 3a. retrieve fresh invoice data from db
    private void refreshInvoiceData(){
        invoices = new ArrayList<HashMap<String, String>>();
        positiveInvoices = new ArrayList<HashMap<String, String>>();
        if (currentProfile.size() > 0){
            invoices = db.getInvoices(currentProfile.get("USERID"));
            positiveInvoices = db.getPositiveInvoices(currentProfile.get("USERID"));
        }

        // Update combobox options
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
        invoiceSelector.setSelectedIndex(currentInvoice);

        String[] positiveInvoiceNames = new String[positiveInvoices.size()];
        for (int i = 0; i < positiveInvoices.size(); i++){
            positiveInvoiceNames[i] = positiveInvoices.get(i).get("INVOICENUMBER");;
        }
        DefaultComboBoxModel<String> options2 = new DefaultComboBoxModel<>(positiveInvoiceNames);
        creditSelector.setModel(options2);
    }
    // 3b. Fill invoice preview with data 
    private void loadInvoiceData(){

        String selection = invoiceSelector.getSelectedItem().toString();
        ArrayList<HashMap<String, String>> loadedTable = new ArrayList<HashMap<String, String>>();
    
        if (selection.equals("[nieuw]")){
            
            
        } else {
            selection = selection.split("]", 2)[0];
            selection = selection.replaceFirst(Pattern.quote("["), "");
            currentInvoice = Integer.parseInt(selection);

            HashMap<String, String> selectedInvoice = invoices.get(currentInvoice - 1);
            invoiceAccount = selectedInvoice.get("INVOICEACCOUNT");
            invoiceNumber = selectedInvoice.get("INVOICENUMBER");
            invoicePlace = selectedInvoice.get("INVOICEPLACE");
            invoiceDate = selectedInvoice.get("INVOICEDATE");
            invoiceTerm = selectedInvoice.get("INVOICETERM");
            invoiceType = selectedInvoice.get("INVOICETYPE");

            //invoiceTable.setVatSelector(selectedInvoice.get("VATTYPE"));            
            loadedTable = db.getInvoiceLines(selectedInvoice.get("INVOICEID"));

            ignoreCreditSelector = true;
            currentPositiveInvoice = -1;
            creditSelector.setSelectedItem(invoiceNumber);

            // Find current customer
            int customerIndex = 0;
            for (int i = 0; i < customers.size(); i++){
                if (customers.get(i).get("CUSTOMERID").equals(selectedInvoice.get("CUSTOMERID"))){
                    customerIndex = i + 1;
                }
            }
            currentCustomer = customerIndex;
        }
        invoiceNumberField.setText(invoiceNumber);
        invoiceTypeSelector.setSelectedItem(invoiceType);
        invoiceAccountField.setText(invoiceAccount);
        invoicePlaceField.setText(invoicePlace);
        invoiceDateField.setText(invoiceDate);
        invoiceTermField.setText(invoiceTerm);

        //invoiceTable.loadTable(loadedTable, false);
    }

    public void preloadCreditNote(){
        int selection = creditSelector.getSelectedIndex();
        if (selection != currentPositiveInvoice){
            currentPositiveInvoice = selection;
            db.logEvent("Current positive invoice is " + currentPositiveInvoice);

            HashMap<String, String> selectedInvoice = positiveInvoices.get(currentPositiveInvoice);
            
            // Find current customer
            int customerIndex = 0;
            for (int i = 0; i < customers.size(); i++){
                if (customers.get(i).get("CUSTOMERID").equals(selectedInvoice.get("CUSTOMERID"))){
                    customerIndex = i + 1;
                }
            }
            currentCustomer = customerIndex;
            
            // Set vat
            // invoiceTable.setVatSelector(selectedInvoice.get("VATTYPE"));

            // Load negative version of table
            ArrayList<HashMap<String, String>> loadedTable = db.getInvoiceLines(selectedInvoice.get("INVOICEID"));
            //invoiceTable.loadTable(loadedTable, true);
        }
    }

    public void checkType(String selection){
        // Check if language needs to be changed
        boolean isChanged = false;
        if (selection.equals("FACTUUR") || selection.equals("CREDITFACTUUR")){
            if (lang == LanguageFile.EN){
                lang = LanguageFile.NL;
                isChanged = true;
            }
        } else if (selection.equals("INVOICE") || selection.equals("CREDIT MEMO")){
            if (lang == LanguageFile.NL){
                lang = LanguageFile.EN;
                isChanged = true;
            }
        }
        if (isChanged){
            setLanguage(this.lang);
            profileManager.setLanguage(this.lang); // Reload profile entirely
            invoiceTable.setLanguage(this.lang);
        }

        // Check if direction needs to be changed
        isChanged = false;
        if (selection.equals("FACTUUR") || selection.equals("INVOICE")){
            if (currentInvoiceType.equals("NEGATIVE")){
                currentInvoiceType = "POSITIVE";
                isChanged = true;
            }
        } else if (selection.equals("CREDITFACTUUR") || selection.equals("CREDIT MEMO")){
            if (currentInvoiceType.equals("POSITIVE")){
                currentInvoiceType = "NEGATIVE";
                isChanged = true;
            }
        }
        if (isChanged){
            crd.next(invoiceTypeCard);
        }
    }
    


    public void itemStateChanged(ItemEvent e){
        if (e.getSource() == invoiceSelector){
            loadInvoiceData();
        }
        if (e.getSource() == creditSelector){
            if (ignoreCreditSelector){
                ignoreCreditSelector = false;
            } else {
                preloadCreditNote();
            }  
        }
        if (e.getSource() == invoiceTypeSelector){
            if (ignoreInvoiceTypeSelector){
                ignoreInvoiceTypeSelector = false;
            } else {
                checkType(invoiceTypeSelector.getSelectedItem().toString());
            }   
        } 
    }
        
    public void actionPerformed(ActionEvent e){
        HashMap<String, String> newInvoice;
        // Collect all invoice data
        if (currentProfile.size() > 0 && currentCustomer > 0 && invoiceTable.tableArray.size() > 0){
            newInvoice = new HashMap<String, String>();
            newInvoice.put("profileId", currentProfile.get("USERID"));
            newInvoice.put("customerId", customers.get(currentCustomer - 1).get("CUSTOMERID"));

            invoiceType = invoiceTypeSelector.getSelectedItem().toString();
            newInvoice.put("invoiceType", invoiceType);
            newInvoice.put("realType", currentInvoiceType);
            vatType = invoiceTable.vatSelector.getSelectedItem().toString();
            newInvoice.put("vatType", vatType);

            if (currentInvoiceType.equals("POSITIVE")){
                newInvoice.put("invoiceNumber", invoiceNumberField.getText());
            } else if (currentInvoiceType.equals("NEGATIVE")){
                newInvoice.put("invoiceNumber", creditSelector.getSelectedItem().toString());
            }
            
            newInvoice.put("invoicePlace", invoicePlaceField.getText());
            newInvoice.put("invoiceDate", invoiceDateField.getText());
            newInvoice.put("invoiceTerm", invoiceTermField.getText());
            newInvoice.put("invoiceAccount", invoiceAccountField.getText());
            
            if (newInvoice.get("invoiceNumber").equals("") || newInvoice.get("invoicePlace").equals("") || 
                newInvoice.get("invoiceDate").equals("") || newInvoice.get("invoiceTerm").equals("") || 
                newInvoice.get("invoiceAccount").equals("")
            ){
                JOptionPane.showMessageDialog(this, "Niet alle verplichte velden zijn ingevuld.");
            } else {
                db.addInvoice(newInvoice);
                int invoiceId = db.getInvoiceId(newInvoice.get("profileId"), newInvoice.get("invoiceNumber"), newInvoice.get("realType"));
                invoiceTable.pushTableArray(invoiceId);
                if (currentInvoice == 0){
                    currentInvoice = invoices.size() + 1;
                }
                refreshInvoiceData();
            }
        } else {
            String message = "<html><body align=\"center\">Selecteer minstens een profiel en een begunstigde.";
            message += "<br>Een geldige factuur bestaat uit minstens 1 factuurlijn</body></html>";
            JOptionPane.showMessageDialog(this, message);
        }
    }

    public InvoiceMaker(Mammon parent, DbConsole db){
        super();
        this.parent = parent;
        this.db = db;
        this.currentInvoice = 0;
        

        ArrayList<HashMap<String, String>> loadedTable = new ArrayList<HashMap<String, String>>();
        invoiceTableSection.add(invoiceTable);

        // 4. prepare invoice preview
        
        // 5. Add event listeners
        confirmInvoice.addActionListener(this);
        confirmPdf.addActionListener(this);
        invoiceSelector.addItemListener(this);
        invoiceTypeSelector.addItemListener(this);
        creditSelector.addItemListener(this);

        // 6. Set invoice language
        setLanguage(this.lang);
    }
}
