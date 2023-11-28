package be.planetegem.mammon.invoice;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;

import javax.swing.JLabel;
import javax.swing.JPanel;

import be.planetegem.mammon.statics.DocumentConstraints;
import be.planetegem.mammon.statics.LanguageFile;
import be.planetegem.mammon.statics.StyleSheet;
import be.planetegem.mammon.util.JDateField;

import be.planetegem.mammon.util.JDynamicField;
import be.planetegem.mammon.util.JFontLabel;
import be.planetegem.mammon.util.JSmallButton;

public class InvoiceMakerUI extends JPanel {

    protected JPanel invoiceBody, profileSection, customerSection, invoiceTableSection, invoiceTypeCard;
    protected CardLayout crd = new CardLayout(0, 0);

    protected JComboBox<String> invoiceTypeSelector, invoiceSelector, creditSelector;
    protected String[] invoiceTypes = {"FACTUUR", "INVOICE", "CREDITFACTUUR", "CREDIT MEMO"};
    public String currentInvoiceType = "POSITIVE";
    

    protected JDynamicField invoicePlaceField, invoiceNumberField, invoiceAccountField, invoiceTermField;
    protected JDateField invoiceDateField;
    protected DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    protected JSmallButton confirmInvoice, confirmPdf;

    // language sensitive labels
    protected JLabel createdIn, onDate, invoiceNumberLabel, creditNoteLabel, paymentLabel1, paymentLabel2;
    
    public void setLanguage(int lang){
        createdIn.setText(LanguageFile.created[lang]);
        onDate.setText(LanguageFile.on[lang]);
        invoiceNumberLabel.setText(LanguageFile.invoiceNr[lang]);
        creditNoteLabel.setText(LanguageFile.creditNote[lang]);
        paymentLabel1.setText(LanguageFile.footer1[lang]);
        paymentLabel2.setText(LanguageFile.footer2[lang]);
    }
    
    protected InvoiceMakerUI(){
        super();

        // 1. Main body = a4 simulation
        invoiceBody = new JPanel();
        invoiceBody.setLayout(new BoxLayout(invoiceBody, BoxLayout.Y_AXIS));
        invoiceBody.setPreferredSize(new Dimension((int) Math.round(DocumentConstraints.a4Width*4), (int) Math.round(DocumentConstraints.a4Height*4)));
        invoiceBody.setMinimumSize(new Dimension((int) Math.round(DocumentConstraints.a4Width*4), (int) Math.round(DocumentConstraints.a4Height*4)));
        add(invoiceBody);

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
        invoiceBody.add(profileSection);

        // 3. invoice Header: select type, location & date
        JPanel invoiceHeaderContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        Dimension invoiceHeaderContainerSize = new Dimension(
            DocumentConstraints.a4Width*DocumentConstraints.previewRatio, 
            DocumentConstraints.lineHeight*DocumentConstraints.previewRatio
        );
        invoiceHeaderContainer.setPreferredSize(invoiceHeaderContainerSize);
        invoiceHeaderContainer.setMaximumSize(invoiceHeaderContainerSize);
        invoiceHeaderContainer.setMinimumSize(invoiceHeaderContainerSize);
        invoiceHeaderContainer.setBackground(Color.white);
        invoiceBody.add(invoiceHeaderContainer);

        JPanel invoiceHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Dimension invoiceHeaderSize = new Dimension(
            DocumentConstraints.lineWidth*DocumentConstraints.previewRatio, 
            DocumentConstraints.lineHeight*DocumentConstraints.previewRatio
        );
        invoiceHeader.setPreferredSize(invoiceHeaderSize);
        invoiceHeader.setMaximumSize(invoiceHeaderSize);
        invoiceHeader.setMinimumSize(invoiceHeaderSize);
        invoiceHeader.setBackground(Color.white);
        invoiceHeaderContainer.add(invoiceHeader);

        invoiceTypeSelector = new JComboBox<String>(invoiceTypes);
        invoiceTypeSelector.setBackground(Color.white);
        invoiceTypeSelector.setFont(StyleSheet.documentFont);
        invoiceHeader.add(invoiceTypeSelector);

        createdIn = new JLabel();
        createdIn.setFont(StyleSheet.documentFont);
        invoiceHeader.add(createdIn);

        invoicePlaceField = new JDynamicField(this, "[plaats]");
        invoicePlaceField.setFont(StyleSheet.documentFont);
        invoiceHeader.add(invoicePlaceField);

        onDate = new JLabel();
        onDate.setFont(StyleSheet.documentFont);
        invoiceHeader.add(onDate);

        invoiceDateField = new JDateField(dateFormat);
        invoiceDateField.setFont(StyleSheet.documentFont);
        invoiceHeader.add(invoiceDateField);

        // 4. CustomerManager
        customerSection = new JPanel();
        Dimension customerSize = new Dimension(
            DocumentConstraints.a4Width*DocumentConstraints.previewRatio, 
            (DocumentConstraints.customerHeight + DocumentConstraints.lineHeight)*DocumentConstraints.previewRatio
        );
        customerSection.setPreferredSize(customerSize);
        customerSection.setMaximumSize(customerSize);
        customerSection.setMinimumSize(customerSize);
        customerSection.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        invoiceBody.add(customerSection);

        // 5. Invoice number
        JPanel invoiceSelectContainer = new JPanel();
        invoiceBody.add(invoiceSelectContainer);
        invoiceSelectContainer.setBackground(Color.white);
        Dimension invoiceNumberSize = new Dimension(
            DocumentConstraints.a4Width*DocumentConstraints.previewRatio, 
            DocumentConstraints.invoiceNumberHeight*DocumentConstraints.previewRatio
        );
        invoiceSelectContainer.setPreferredSize(invoiceNumberSize);
        invoiceSelectContainer.setMaximumSize(invoiceNumberSize);
        invoiceSelectContainer.setMinimumSize(invoiceNumberSize);
        invoiceSelectContainer.setLayout(new BoxLayout(invoiceSelectContainer, BoxLayout.X_AXIS));

        JPanel invoiceNumberContainer = new JPanel();
        invoiceNumberContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        invoiceNumberContainer.setBackground(Color.white);
        invoiceSelectContainer.add(invoiceNumberContainer);
        
        JLabel paddingLeft = new JLabel();
        Dimension paddingSize = new Dimension(
            DocumentConstraints.invoiceNumberLeading*DocumentConstraints.previewRatio, 
            DocumentConstraints.invoiceNumberHeight*DocumentConstraints.previewRatio
        );
        paddingLeft.setPreferredSize(paddingSize);
        paddingLeft.setMaximumSize(paddingSize);
        paddingLeft.setMinimumSize(paddingSize);
        invoiceNumberContainer.add(paddingLeft);

        invoiceTypeCard = new JPanel();
        invoiceTypeCard.setLayout(crd);
        invoiceNumberContainer.add(invoiceTypeCard);
        invoiceTypeCard.setBackground(Color.white);

        JPanel positiveCard = new JPanel();
        positiveCard.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        invoiceNumberLabel = new JLabel();
        invoiceNumberLabel.setFont(StyleSheet.documentFont);
        positiveCard.add(invoiceNumberLabel);
        positiveCard.setBackground(Color.white);

        invoiceNumberField = new JDynamicField(this, "23001");
        invoiceNumberField.setFont(StyleSheet.documentFont);
        positiveCard.add(invoiceNumberField);

        JPanel negativeCard = new JPanel();
        negativeCard.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        creditNoteLabel = new JLabel();
        creditNoteLabel.setFont(StyleSheet.documentFont);
        negativeCard.add(creditNoteLabel);

        creditSelector = new JComboBox<String>();
        creditSelector.setFont(StyleSheet.documentFont);
        negativeCard.add(creditSelector);
        negativeCard.setBackground(Color.white);

        invoiceTypeCard.add("a", positiveCard);
        invoiceTypeCard.add("b", negativeCard);

        JPanel invoiceSelectorContainer = new JPanel();
        invoiceSelectContainer.add(invoiceSelectorContainer);
        invoiceSelectorContainer.setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        invoiceSelectorContainer.setBackground(Color.white);

        JLabel invoiceSelectorLabel = new JLabel("Open een factuur:");
        invoiceSelectorLabel.setFont(StyleSheet.wizardFont);
        invoiceSelectorContainer.add(invoiceSelectorLabel);
                
        invoiceSelector = new JComboBox<String>();
        invoiceSelector.setFont(StyleSheet.wizardFont);
        invoiceSelectorContainer.add(invoiceSelector);

        JLabel paddingRight = new JLabel();
        paddingSize = new Dimension(
            DocumentConstraints.baseMargin*DocumentConstraints.previewRatio, 
            DocumentConstraints.invoiceNumberHeight*DocumentConstraints.previewRatio
        );
        paddingRight.setPreferredSize(paddingSize);
        paddingRight.setMaximumSize(paddingSize);
        paddingRight.setMinimumSize(paddingSize);
        invoiceSelectorContainer.add(paddingRight);

        // 6. Invoice lines
        invoiceTableSection = new JPanel();
        Dimension invoiceLinesSize = new Dimension(
            DocumentConstraints.a4Width*DocumentConstraints.previewRatio, 
            DocumentConstraints.invoiceTable*DocumentConstraints.previewRatio
        );
        invoiceTableSection.setPreferredSize(invoiceLinesSize);
        invoiceTableSection.setMaximumSize(invoiceLinesSize);
        invoiceTableSection.setMinimumSize(invoiceLinesSize);
        invoiceTableSection.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        invoiceTableSection.setBackground(Color.white);
        invoiceBody.add(invoiceTableSection);

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
        invoiceBody.add(paymentContainer);

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
        invoiceTermField = new JDynamicField(payment1, "30");
        invoiceTermField.setFont(StyleSheet.documentFont);
        payment1.add(invoiceTermField);
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
        invoiceAccountField = new JDynamicField(payment2, "[rekeningnummer]");
        invoiceAccountField.setFont(StyleSheet.documentFont);
        payment2.add(invoiceAccountField);

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
