package be.planetegem.mammon.statics;

public class DocumentConstraints {
    // Summary:
    // Top margin 8
    // Profile Section 52 (4 top-margin + 32 logo + 4 margin + 8 details + 4 bottom-margin)
    // BillHeader 8
    // Margin 8
    // Customer Section 40
    // Invoice Number 16
    // Invoice payment: 16 + 11;


    // Main measurements
    static final public int a4Width = 210;
    static final public int a4Height = 297;
    static final public int baseMargin = 11;
    static final public int quarterLine = 2;
    static final public int halfLine = quarterLine*2;
    static final public int lineHeight = halfLine*2;
    static final public int softLine = quarterLine + halfLine;
    static final public int lineWidth = a4Width - 2*baseMargin;
    static final public int previewRatio = 4;

    // Profile constraints
    static final public int profileHeight = 56;
    static final public int logoWidth = 62;
    static final public int logoHeight = 32;

    // Bill header
    static final public int billHeaderHeight = 8;

    // Customer constraints: 8 top margin
    static final public int customerHeight = 40;
    static final public int customerLeadingWidth = 22;
    static final public int customerHeaderWidth = 16;
    static final public int customerBodyWidth = lineWidth - customerHeaderWidth - customerLeadingWidth;

    // Invoice number
    static final public int invoiceNumberLeading = baseMargin + customerLeadingWidth;
    static final public int invoiceNumberHeight = lineHeight*2;

    // Invoice payment
    static final public int invoicePaymentHeight = baseMargin + lineHeight*2;

    // Table takes up remainder of vertical space
    static final public int invoiceTable = a4Height - lineHeight*3 - profileHeight - customerHeight - invoiceNumberHeight - invoicePaymentHeight;

    // Table column widths = 188 total
    static final public int tDescription = 53;
    static final public int tDate = 30;
    static final public int tNumber = 30;
    static final public int tPrice = 30;
    static final public int tTotal = 45;
    
    static final public int tSubtotalLeft = 30;
    static final public int tSubtotalRight = 35;
    static final public int tSubtotalWidth = tSubtotalLeft + tSubtotalRight;
    static final public int tHeaderHeight = 10;

}
