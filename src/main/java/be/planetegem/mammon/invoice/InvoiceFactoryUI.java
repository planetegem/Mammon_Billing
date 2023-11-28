package be.planetegem.mammon.invoice;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import be.planetegem.mammon.statics.DocumentConstraints;
import be.planetegem.mammon.statics.LanguageFile;
import be.planetegem.mammon.statics.StyleSheet;
import be.planetegem.mammon.util.JDateField;
import be.planetegem.mammon.util.JDynamicField;
import be.planetegem.mammon.util.JFontLabel;
import be.planetegem.mammon.util.JSmallButton;

public class InvoiceFactoryUI extends JPanel {

    // Main sections
    protected JPanel profileSection;
    protected ProfileManager pm;
    protected JPanel customerSection;
    protected CustomerManager cm;
    protected JPanel tableSection;
    protected InvoiceTable ivTable;

    // Select invoice type: positive/negative, english/dutch
    protected JComboBox<String> typeSelector, creditnoteSelector, invoiceSelector;
    protected String[] invoiceTypes = {"FACTUUR", "INVOICE", "CREDITFACTUUR", "CREDIT MEMO"};

    // Invoice fields: place, date, term and account
    protected JDynamicField placeField, invoiceNumberField, paymentTermField, accountField;
    protected JDateField dateField;
    protected DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    protected JSmallButton confirmInvoice, confirmPdf;

    protected JPanel invoiceCardContainer;
    protected CardLayout crd = new CardLayout(0, 0);
    protected String direction = "POSITIVE";


    // Language logic
    public int lang = LanguageFile.NL;
    private JLabel createdAtLabel, onDateLabel, positiveLabel, negativeLabel, paymentLabel1, paymentLabel2; 

    public void setLanguage(int lang){
        this.lang = lang;
        createdAtLabel.setText(" " + LanguageFile.created[lang] + " ");
        onDateLabel.setText(" " + LanguageFile.on[lang] + " ");
        positiveLabel.setText(LanguageFile.invoiceNr[lang] + " ");
        negativeLabel.setText(LanguageFile.creditNote[lang] + " ");
        paymentLabel1.setText(LanguageFile.footer1[lang]);
        paymentLabel2.setText(LanguageFile.footer2[lang]);

        pm.setLanguage(this.lang);
        cm.setLanguage(this.lang);
        ivTable.setLanguage(this.lang);
    }

    protected InvoiceFactoryUI(){
        super();
        
        // 1. Main body = a4 simulation
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension((int) Math.round(DocumentConstraints.a4Width*4), (int) Math.round(DocumentConstraints.a4Height*4)));

        // 2. ProfileManager
        profileSection = new JPanel();
        profileSection.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        Dimension profileSize = new Dimension(
            DocumentConstraints.a4Width*DocumentConstraints.previewRatio, 
            (DocumentConstraints.profileHeight + DocumentConstraints.lineHeight)*DocumentConstraints.previewRatio
        );
        profileSection.setMinimumSize(profileSize);
        profileSection.setPreferredSize(profileSize);
        profileSection.setMaximumSize(profileSize);
        add(profileSection);

        // 3. invoice Header: select type, location & date
        JPanel invoiceHeaderContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        Dimension invoiceHeaderContainerSize = new Dimension(
            DocumentConstraints.a4Width*DocumentConstraints.previewRatio, 
            DocumentConstraints.lineHeight*DocumentConstraints.previewRatio
        );
        invoiceHeaderContainer.setPreferredSize(invoiceHeaderContainerSize);
        invoiceHeaderContainer.setMaximumSize(invoiceHeaderContainerSize);
        invoiceHeaderContainer.setMinimumSize(invoiceHeaderContainerSize);
        invoiceHeaderContainer.setBackground(Color.white);
        add(invoiceHeaderContainer);

        JLabel padding1 = new JLabel();
        Dimension paddingSize = new Dimension(
            DocumentConstraints.baseMargin*DocumentConstraints.previewRatio, 
            DocumentConstraints.lineHeight*DocumentConstraints.previewRatio
        );
        padding1.setPreferredSize(paddingSize);
        invoiceHeaderContainer.add(padding1);

        typeSelector = new JComboBox<String>(invoiceTypes);
        typeSelector.setBackground(Color.white);
        typeSelector.setFont(StyleSheet.documentFont);
        invoiceHeaderContainer.add(typeSelector);

        createdAtLabel = new JLabel();
        createdAtLabel.setFont(StyleSheet.documentFont);
        invoiceHeaderContainer.add(createdAtLabel);

        placeField = new JDynamicField(this, "[plaats]");
        placeField.setFont(StyleSheet.documentFont);
        invoiceHeaderContainer.add(placeField);

        onDateLabel = new JLabel();
        onDateLabel.setFont(StyleSheet.documentFont);
        invoiceHeaderContainer.add(onDateLabel);

        dateField = new JDateField(dateFormat);
        dateField.setFont(StyleSheet.documentFont);
        invoiceHeaderContainer.add(dateField);

        // 4. CustomerManager
        customerSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        Dimension customerSize = new Dimension(
            DocumentConstraints.a4Width*DocumentConstraints.previewRatio, 
            (DocumentConstraints.customerHeight + DocumentConstraints.lineHeight)*DocumentConstraints.previewRatio
        );
        customerSection.setPreferredSize(customerSize);
        customerSection.setMaximumSize(customerSize);
        customerSection.setMinimumSize(customerSize);
        add(customerSection);

        // 5. Invoice number
        JPanel invoiceNumberContainer = new JPanel();
        add(invoiceNumberContainer);
        invoiceNumberContainer.setBackground(Color.white);
        Dimension invoiceNumberSize = new Dimension(
            DocumentConstraints.a4Width*DocumentConstraints.previewRatio, 
            DocumentConstraints.invoiceNumberHeight*DocumentConstraints.previewRatio
        );
        invoiceNumberContainer.setPreferredSize(invoiceNumberSize);
        invoiceNumberContainer.setMaximumSize(invoiceNumberSize);
        invoiceNumberContainer.setMinimumSize(invoiceNumberSize);
        invoiceNumberContainer.setLayout(new BoxLayout(invoiceNumberContainer, BoxLayout.X_AXIS));

        JPanel invoiceNumber = new JPanel();
        invoiceNumber.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        invoiceNumber.setBackground(Color.white);
        invoiceNumberContainer.add(invoiceNumber);
        
        JLabel padding2 = new JLabel();
        paddingSize = new Dimension(
            DocumentConstraints.invoiceNumberLeading*DocumentConstraints.previewRatio, 
            DocumentConstraints.invoiceNumberHeight*DocumentConstraints.previewRatio
        );
        padding2.setPreferredSize(paddingSize);
        invoiceNumberContainer.add(padding2);

        invoiceCardContainer = new JPanel();
        invoiceCardContainer.setLayout(crd);
        invoiceNumberContainer.add(invoiceCardContainer);
        invoiceCardContainer.setBackground(Color.white);

        JPanel positiveCard = new JPanel();
        positiveCard.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        positiveCard.setBackground(Color.white);
        positiveLabel = new JLabel();
        positiveLabel.setFont(StyleSheet.documentFont);
        positiveCard.add(positiveLabel);
        invoiceNumberField = new JDynamicField(this, "");
        invoiceNumberField.setFont(StyleSheet.documentFont);
        positiveCard.add(invoiceNumberField);

        JPanel negativeCard = new JPanel();
        negativeCard.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        negativeLabel = new JLabel();
        negativeLabel.setFont(StyleSheet.documentFont);
        negativeCard.add(negativeLabel);

        creditnoteSelector = new JComboBox<String>();
        creditnoteSelector.setFont(StyleSheet.documentFont);
        creditnoteSelector.setPrototypeDisplayValue("23001");
        negativeCard.add(creditnoteSelector);
        negativeCard.setBackground(Color.white);

        invoiceCardContainer.add("a", positiveCard);
        invoiceCardContainer.add("b", negativeCard);

        JPanel invoiceSelectorContainer = new JPanel();
        invoiceNumberContainer.add(invoiceSelectorContainer);
        invoiceSelectorContainer.setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        invoiceSelectorContainer.setBackground(Color.white);

        JLabel invoiceSelectorLabel = new JLabel("Open een factuur:");
        invoiceSelectorLabel.setFont(StyleSheet.wizardFont);
        invoiceSelectorContainer.add(invoiceSelectorLabel);
        
        String[] starterOptions = {"[nieuw]"};
        invoiceSelector = new JComboBox<String>(starterOptions);
        invoiceSelector.setFont(StyleSheet.wizardFont);
        invoiceSelectorContainer.add(invoiceSelector);

        JLabel padding3 = new JLabel();
        paddingSize = new Dimension(
            DocumentConstraints.baseMargin*DocumentConstraints.previewRatio, 
            DocumentConstraints.invoiceNumberHeight*DocumentConstraints.previewRatio
        );
        padding3.setPreferredSize(paddingSize);
        invoiceSelectorContainer.add(padding3);

        // 6. Invoice lines
        tableSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        Dimension invoiceLinesSize = new Dimension(
            DocumentConstraints.a4Width*DocumentConstraints.previewRatio, 
            DocumentConstraints.invoiceTable*DocumentConstraints.previewRatio
        );
        tableSection.setPreferredSize(invoiceLinesSize);
        tableSection.setMaximumSize(invoiceLinesSize);
        tableSection.setMinimumSize(invoiceLinesSize);
        tableSection.setBackground(Color.white);
        add(tableSection);

        // 7. Invoice payment
        JPanel paymentContainer = new JPanel();
        Dimension paymentSize = new Dimension(
            DocumentConstraints.a4Width*DocumentConstraints.previewRatio, 
            DocumentConstraints.invoicePaymentHeight*DocumentConstraints.previewRatio
        );
        paymentContainer.setPreferredSize(paymentSize);
        paymentContainer.setMaximumSize(paymentSize);
        paymentContainer.setMinimumSize(paymentSize);
        paymentContainer.setLayout(new BoxLayout(paymentContainer, BoxLayout.Y_AXIS));
        paymentContainer.setBackground(Color.white);
        add(paymentContainer);

        JPanel payment1 = new JPanel();
        payment1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        Dimension lineSize = new Dimension(
            DocumentConstraints.a4Width*DocumentConstraints.previewRatio, 
            DocumentConstraints.softLine*DocumentConstraints.previewRatio
        );
        payment1.setPreferredSize(lineSize);
        payment1.setMaximumSize(lineSize);
        payment1.setMinimumSize(lineSize);
        payment1.setBackground(Color.white);
        paymentContainer.add(payment1);
        paymentLabel1 = new JLabel();
        paymentLabel1.setFont(StyleSheet.documentFont);
        payment1.add(paymentLabel1);
        paymentTermField = new JDynamicField(payment1, "30");
        paymentTermField.setFont(StyleSheet.documentFont);
        payment1.add(paymentTermField);
        paymentLabel2 = new JLabel();
        paymentLabel2.setFont(StyleSheet.documentFont);
        payment1.add(paymentLabel2);

        JPanel payment2 = new JPanel();
        payment2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        payment2.setBackground(Color.white);
        payment2.setPreferredSize(lineSize);
        payment2.setMaximumSize(lineSize);
        payment2.setMinimumSize(lineSize);
        paymentContainer.add(payment2);
        JFontLabel paymentLabel3 = new JFontLabel("IBAN ", StyleSheet.documentFont);
        payment2.add(paymentLabel3);
        accountField = new JDynamicField(payment2, "[rekeningnummer]");
        accountField.setFont(StyleSheet.documentFont);
        payment2.add(accountField);

        paymentContainer.add(Box.createVerticalGlue());

        JPanel invoiceButtons = new JPanel();
        invoiceButtons.setBackground(Color.white);
        confirmInvoice = new JSmallButton("OPSLAAN");
        confirmInvoice.setFont(StyleSheet.headerFont);
        invoiceButtons.add(confirmInvoice);
        confirmPdf = new JSmallButton("PDF");
        confirmPdf.setFont(StyleSheet.headerFont);
        invoiceButtons.add(confirmPdf);

        paymentContainer.add(invoiceButtons);
    }
}
