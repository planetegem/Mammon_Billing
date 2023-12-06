package be.planetegem.mammon.statics;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class DocConstraints {
    
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

    // Pdf measurements
    static final public float pdfRatio = PDRectangle.A4.getWidth()/a4Width;
    static final public float ivHeaderY = lineHeight + profileHeight + lineHeight;
    static final public float customerY = ivHeaderY + lineHeight*2;
    static final public float invoiceNumberY = customerY + customerHeight + lineHeight;
    static final public float tHeaderY = invoiceNumberY + lineHeight + tHeaderHeight;
    static final public float tDescriptionX = baseMargin;
    static final public float tDateX = tDescriptionX + tDescription;
    static final public float tAmountX = tDateX + tDate;
    static final public float tPriceX = tAmountX + tNumber;
    static final public float tTotalX = tPriceX + tPrice;
    static final public float subtotalLeftX = a4Width - baseMargin - tSubtotalWidth;
    static final public float subtotalRightX = a4Width - baseMargin - tSubtotalRight;
}
