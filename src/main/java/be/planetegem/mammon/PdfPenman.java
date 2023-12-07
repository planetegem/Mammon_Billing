package be.planetegem.mammon;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import be.planetegem.mammon.db.DbConsole;
import be.planetegem.mammon.statics.DocConstraints;
import be.planetegem.mammon.statics.LanguageFile;
import be.planetegem.mammon.statics.StyleSheet;
import be.planetegem.mammon.util.FormattedCell;
import be.planetegem.mammon.util.FormattedInvoice;
import be.planetegem.mammon.util.ResizedImage;

public class PdfPenman {
    private Mammon parent;
    private DbConsole db;

    private String pdfPath;
    private PDDocument file;
    private PDPageContentStream ctx;

    public static final int CENTER = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;


    // select path for pdf
    public int setPdfPath(String invoiceNumber){
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Opslaan als pdf");

        fc.setFileFilter(new FileFilter() {
            public String getDescription() {
                return "PDF Documents (*.pdf)";
            }
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".pdf");
                }
            }
        });
        fc.setAcceptAllFileFilterUsed(false);
        fc.setSelectedFile(new File(invoiceNumber + ".pdf"));

        int selection = fc.showSaveDialog(this.parent);

        if (selection == JFileChooser.APPROVE_OPTION) {
            File save = fc.getSelectedFile();
            this.pdfPath = save.getAbsolutePath();
            db.logEvent("User selected path: " + pdfPath);

            // correct file name if necessary
            if (!save.getName().toLowerCase().endsWith(".pdf")){
                this.pdfPath += ".pdf";
                db.logEvent("Path name corrected to: " + pdfPath);
            }
        } else {
            db.logEvent("User cancelled operation");
        }
        return selection;
    }

    // Main method: generates pdf from a formattedinvoice object 
    public void makePdf(FormattedInvoice invoice){
        // step 1: start PDDocument & content stream
        file = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        
        file.addPage(page);
        try {
            ctx = new PDPageContentStream(file, page);
            db.logEvent("PDF content stream started");
        } catch (IOException e){
             db.logEvent("Exception while starting content stream: " + e.getMessage());
        }

        // positioning values: keeps track of pointer, starting in top left corner
        float xPos = 0;
        float yPos = DocConstraints.a4Height*DocConstraints.pdfRatio;

        // Step 2: profile section
        float imgWidth, imgHeight;
        boolean logoFailed = false;

        // Add logo
        if (invoice.hasLogo){
            try {
                // Calculate size of image
                Dimension imgDimension = ResizedImage.fitImage(
                    ImageIO.read(new File(invoice.logoValue)), 
                    DocConstraints.logoWidth, 
                    DocConstraints.logoHeight
                );

                imgWidth = (float) imgDimension.getWidth()*DocConstraints.pdfRatio;
                imgHeight = (float) imgDimension.getHeight()*DocConstraints.pdfRatio;
                xPos = (DocConstraints.a4Width*DocConstraints.pdfRatio - imgWidth)*0.5f;
                yPos = (DocConstraints.a4Height - DocConstraints.baseMargin)*DocConstraints.pdfRatio - imgHeight;

                PDImageXObject img = PDImageXObject.createFromFile(invoice.logoValue, file);
                ctx.drawImage(img, xPos, yPos, imgWidth, imgHeight);
                db.logEvent("Logo printed to pdf from path " + invoice.logoValue);
            } catch (IOException e){
                db.logEvent("Exception printing profile logo to pdf: " + e.getMessage());
                logoFailed = true;
            }
        } 
        // Add ad hoc logo if necessary
        if (!invoice.hasLogo || logoFailed) {
            db.logEvent("Generating ad hoc logo");
            try {
                PDFont font = PDType1Font.HELVETICA_BOLD;
                int fontSize = 28;
                ctx.beginText();
                ctx.setFont(font, fontSize);

                // figure out size to determine offsets
                imgWidth = font.getStringWidth(invoice.logoValue.toUpperCase()) / 1000 * fontSize;
                imgHeight = (font.getFontDescriptor().getFontBoundingBox().getHeight() / 1600 * fontSize);
                xPos = ((DocConstraints.a4Width*DocConstraints.pdfRatio - imgWidth)*0.5f);
                yPos = (DocConstraints.a4Height - DocConstraints.baseMargin)*DocConstraints.pdfRatio - imgHeight;

                ctx.newLineAtOffset(xPos, yPos);               
                ctx.showText(invoice.logoValue.toUpperCase());
                ctx.endText();
            } catch (IOException e){
                db.logEvent("Exception generating ad hoc logo: " + e.getMessage());
            }
        }

        // Add profile string
        try {
            PDType0Font font = PDType0Font.load(file, new File(StyleSheet.fontPath));
            int fontSize = 11; 
            db.logEvent("Printing profile string: " + invoice.profileString);           
            String nextLine = "";

            // determine size and location
            float strWidth = font.getStringWidth(invoice.profileString) / 1000 * fontSize;
            String details = invoice.profileString;

            // if too wide, split of last portion
            if (strWidth > DocConstraints.lineWidth*DocConstraints.pdfRatio){
                String[] temp = details.split("\u2981");
                nextLine = temp[temp.length - 1];
                details = details.replace(nextLine, "");                
                imgWidth = font.getStringWidth(details) / 1000 * fontSize;
                db.logEvent("String too wide; splitting off " + nextLine);
            }            
            xPos = (DocConstraints.a4Width*DocConstraints.pdfRatio - strWidth)*0.5f;
            imgHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize*0.8f;
            yPos -= imgHeight + DocConstraints.quarterLine*DocConstraints.pdfRatio;

            ctx.beginText();
            ctx.setFont(font, fontSize);           
            ctx.newLineAtOffset(xPos, yPos);               
            ctx.showText(details);
            ctx.endText();

            if (nextLine.length() > 0){
                imgWidth = font.getStringWidth(nextLine) / 1000 * fontSize;
                xPos = (DocConstraints.a4Width*DocConstraints.pdfRatio - imgWidth)*0.5f;

                ctx.beginText();
                ctx.newLineAtOffset(xPos, yPos - imgHeight);               
                ctx.showText(nextLine);
                ctx.endText();
            }
        } catch (IOException e){
            db.logEvent("Exception printing profile details: " + e.getMessage());
        }

        // Step 3: invoice header
        db.logEvent("Printing invoice header: " + invoice.ivHeader);

        try {
            PDType0Font font = PDType0Font.load(file, new File(StyleSheet.fontPath));
            int fontSize = 11;

            xPos = DocConstraints.baseMargin*DocConstraints.pdfRatio;
            yPos = (DocConstraints.a4Height - DocConstraints.ivHeaderY)*DocConstraints.pdfRatio;

            ctx.beginText();
            ctx.setFont(font, fontSize);           
            ctx.newLineAtOffset(xPos, yPos);
            ctx.showText(invoice.ivHeader);
            ctx.endText();

        } catch (IOException e){
            db.logEvent("Exception printing invoice header to pdf: " + e.getMessage());
        }

        // Step 4: customer section
        db.logEvent("Printing customer: " + invoice.customerLines.get(0));

        try {
            PDType0Font font = PDType0Font.load(file, new File(StyleSheet.fontPath));
            int fontSize = 11;

            xPos = (DocConstraints.baseMargin + DocConstraints.customerLeadingWidth)*DocConstraints.pdfRatio;
            yPos = (DocConstraints.a4Height - DocConstraints.customerY)*DocConstraints.pdfRatio;
            float lineHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize * 0.6f;

            ctx.beginText();
            ctx.setFont(font, fontSize);           
            ctx.newLineAtOffset(xPos, yPos);
            ctx.showText(invoice.customerHeader);
            ctx.endText();

            xPos += DocConstraints.customerHeaderWidth*DocConstraints.pdfRatio;
            for (String line: invoice.customerLines){
                db.logEvent("Printing line to pdf: " + line);
                ctx.beginText();
                ctx.newLineAtOffset(xPos, yPos);
                ctx.showText(line);
                ctx.endText();
                yPos -= lineHeight;
            }
        } catch (IOException e){
            db.logEvent("Exception printing customer to pdf: " + e.getMessage());
        }

        // Step 5: invoice number
        db.logEvent("Printing Invoice number: " + invoice.ivNumber);

        try {
            PDType0Font font = PDType0Font.load(file, new File(StyleSheet.fontPath));
            int fontSize = 11;

            xPos = (DocConstraints.baseMargin + DocConstraints.customerLeadingWidth)*DocConstraints.pdfRatio;
            yPos = (DocConstraints.a4Height - DocConstraints.invoiceNumberY)*DocConstraints.pdfRatio;

            ctx.beginText();
            ctx.setFont(font, fontSize);           
            ctx.newLineAtOffset(xPos, yPos);
            ctx.showText(invoice.ivNumber);
            ctx.endText();

        } catch (IOException e){
            db.logEvent("Exception printing invoice number to pdf: " + e.getMessage());
        }
        
        int pageCount = 1;
        // step 6: invoice table
        db.logEvent("Printing table");
        if (invoice.multiDescriptionColumn.get(1).size() > 0){
            invoice.applyMergedCellPatch();
        }

        try {
            xPos = DocConstraints.tDescriptionX*DocConstraints.pdfRatio;
            yPos = (DocConstraints.a4Height - DocConstraints.tHeaderY)*DocConstraints.pdfRatio;
            float cellWidth = DocConstraints.tDescription*DocConstraints.pdfRatio;
            float cellHeight = DocConstraints.tHeaderHeight*DocConstraints.pdfRatio;

            drawCell(invoice.headers.get(0), xPos, yPos, cellWidth, cellHeight, CENTER, false);
            cellHeight = DocConstraints.softLine*DocConstraints.pdfRatio;
            for (FormattedCell cell : invoice.multiDescriptionColumn.get(0)){
                drawCell(cell.string, xPos, yPos - (cell.y + cell.height)*cellHeight, cellWidth, cellHeight*cell.height, CENTER, true);
            }

            xPos = DocConstraints.tDateX*DocConstraints.pdfRatio;
            cellWidth = DocConstraints.tDate*DocConstraints.pdfRatio;
            cellHeight = DocConstraints.tHeaderHeight*DocConstraints.pdfRatio;
            drawCell(invoice.headers.get(1), xPos, yPos, cellWidth, cellHeight, CENTER, false);
            cellHeight = DocConstraints.softLine*DocConstraints.pdfRatio;
            for (FormattedCell cell : invoice.multiDateColumn.get(0)){
                drawCell(cell.string, xPos, yPos - (cell.y + cell.height)*cellHeight, cellWidth, cellHeight*cell.height, CENTER, false);
            }

            xPos = DocConstraints.tAmountX*DocConstraints.pdfRatio;
            cellWidth = DocConstraints.tNumber*DocConstraints.pdfRatio;
            cellHeight = DocConstraints.tHeaderHeight*DocConstraints.pdfRatio;
            drawCell(invoice.headers.get(2), xPos, yPos, cellWidth, cellHeight, CENTER, false);
            cellHeight = DocConstraints.softLine*DocConstraints.pdfRatio;
            for (FormattedCell cell : invoice.multiAmountColumn.get(0)){
                drawCell(cell.string, xPos, yPos - (cell.y + cell.height)*cellHeight, cellWidth, cellHeight*cell.height, CENTER, false);
            }

            xPos = DocConstraints.tPriceX*DocConstraints.pdfRatio;
            cellWidth = DocConstraints.tPrice*DocConstraints.pdfRatio;
            cellHeight = DocConstraints.tHeaderHeight*DocConstraints.pdfRatio;
            drawCell(invoice.headers.get(3), xPos, yPos, cellWidth, cellHeight, CENTER, false);
            cellHeight = DocConstraints.softLine*DocConstraints.pdfRatio;
            for (FormattedCell cell : invoice.multiPriceColumn.get(0)){
                drawCell(cell.string, xPos, yPos - (cell.y + cell.height)*cellHeight, cellWidth, cellHeight*cell.height, CENTER, false);
            }

            xPos = DocConstraints.tTotalX*DocConstraints.pdfRatio;
            cellWidth = DocConstraints.tTotal*DocConstraints.pdfRatio;
            cellHeight = DocConstraints.tHeaderHeight*DocConstraints.pdfRatio;
            drawCell(invoice.headers.get(4), xPos, yPos, cellWidth, cellHeight, CENTER, false);
            cellHeight = DocConstraints.softLine*DocConstraints.pdfRatio;
            for (FormattedCell cell : invoice.multiTotalsColumn.get(0)){
                drawCell(cell.string, xPos, yPos - (cell.y + cell.height)*cellHeight, cellWidth, cellHeight*cell.height, RIGHT, false);
            }

            yPos -= invoice.getTableSize(invoice.multiDescriptionColumn.get(0))*cellHeight;
            yPos -= DocConstraints.halfLine*DocConstraints.pdfRatio;

            // check if there is a 2nd page
            if (invoice.multiDescriptionColumn.get(1).size() > 0){
                PDType0Font font = PDType0Font.load(file, new File(StyleSheet.fontPath));
                int fontSize = 11;
                String pageFooter = LanguageFile.page[invoice.lang] + "1/2";

                float lineHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize *0.6f ;
                yPos = DocConstraints.baseMargin*DocConstraints.pdfRatio + lineHeight;

                float lineWidth = font.getStringWidth(pageFooter) / 1000 * fontSize;
                xPos = (DocConstraints.a4Width*DocConstraints.pdfRatio - lineWidth)*0.5f;

                ctx.beginText();
                ctx.newLineAtOffset(xPos, yPos);
                ctx.showText(pageFooter);
                ctx.endText();

                // add new page
                PDPage newPage = new PDPage(PDRectangle.A4);
                file.addPage(newPage);
                ctx.close();
                ctx = new PDPageContentStream(file, newPage);
                db.logEvent("Started 2nd page");
                pageCount++;

                xPos = DocConstraints.tDescriptionX*DocConstraints.pdfRatio;
                yPos = (DocConstraints.a4Height - DocConstraints.baseMargin*2)*DocConstraints.pdfRatio;
                cellWidth = DocConstraints.tDescription*DocConstraints.pdfRatio;
                cellHeight = DocConstraints.tHeaderHeight*DocConstraints.pdfRatio;
                yPos -= cellHeight;

                drawCell(invoice.headers.get(0), xPos, yPos, cellWidth, cellHeight, CENTER, false);
                cellHeight = DocConstraints.softLine*DocConstraints.pdfRatio;
                for (FormattedCell cell : invoice.multiDescriptionColumn.get(1)){
                    drawCell(cell.string, xPos, yPos - (cell.y + cell.height)*cellHeight, cellWidth, cellHeight*cell.height, CENTER, true);
                }

                xPos = DocConstraints.tDateX*DocConstraints.pdfRatio;
                cellWidth = DocConstraints.tDate*DocConstraints.pdfRatio;
                cellHeight = DocConstraints.tHeaderHeight*DocConstraints.pdfRatio;
                drawCell(invoice.headers.get(1), xPos, yPos, cellWidth, cellHeight, CENTER, false);
                cellHeight = DocConstraints.softLine*DocConstraints.pdfRatio;
                for (FormattedCell cell : invoice.multiDateColumn.get(1)){
                    drawCell(cell.string, xPos, yPos - (cell.y + cell.height)*cellHeight, cellWidth, cellHeight*cell.height, CENTER, false);
                }

                xPos = DocConstraints.tAmountX*DocConstraints.pdfRatio;
                cellWidth = DocConstraints.tNumber*DocConstraints.pdfRatio;
                cellHeight = DocConstraints.tHeaderHeight*DocConstraints.pdfRatio;
                drawCell(invoice.headers.get(2), xPos, yPos, cellWidth, cellHeight, CENTER, false);
                cellHeight = DocConstraints.softLine*DocConstraints.pdfRatio;
                for (FormattedCell cell : invoice.multiAmountColumn.get(1)){
                    drawCell(cell.string, xPos, yPos - (cell.y + cell.height)*cellHeight, cellWidth, cellHeight*cell.height, CENTER, false);
                }

                xPos = DocConstraints.tPriceX*DocConstraints.pdfRatio;
                cellWidth = DocConstraints.tPrice*DocConstraints.pdfRatio;
                cellHeight = DocConstraints.tHeaderHeight*DocConstraints.pdfRatio;
                drawCell(invoice.headers.get(3), xPos, yPos, cellWidth, cellHeight, CENTER, false);
                cellHeight = DocConstraints.softLine*DocConstraints.pdfRatio;
                for (FormattedCell cell : invoice.multiPriceColumn.get(1)){
                    drawCell(cell.string, xPos, yPos - (cell.y + cell.height)*cellHeight, cellWidth, cellHeight*cell.height, CENTER, false);
                }

                xPos = DocConstraints.tTotalX*DocConstraints.pdfRatio;
                cellWidth = DocConstraints.tTotal*DocConstraints.pdfRatio;
                cellHeight = DocConstraints.tHeaderHeight*DocConstraints.pdfRatio;
                drawCell(invoice.headers.get(4), xPos, yPos, cellWidth, cellHeight, CENTER, false);
                cellHeight = DocConstraints.softLine*DocConstraints.pdfRatio;
                for (FormattedCell cell : invoice.multiTotalsColumn.get(1)){
                    drawCell(cell.string, xPos, yPos - (cell.y + cell.height)*cellHeight, cellWidth, cellHeight*cell.height, RIGHT, false);
                }

                yPos -= invoice.getTableSize(invoice.multiDescriptionColumn.get(1))*cellHeight;
                yPos -= DocConstraints.halfLine*DocConstraints.pdfRatio;
            }



            // subtotals
            cellHeight = DocConstraints.softLine*DocConstraints.pdfRatio;
            yPos -= DocConstraints.softLine*DocConstraints.pdfRatio;
            xPos = DocConstraints.subtotalLeftX*DocConstraints.pdfRatio;
            cellWidth = DocConstraints.tSubtotalLeft*DocConstraints.pdfRatio;
            drawCell(invoice.subtotals.get(0), xPos, yPos, cellWidth, cellHeight, LEFT, false);

            xPos = DocConstraints.subtotalRightX*DocConstraints.pdfRatio;
            cellWidth = DocConstraints.tSubtotalRight*DocConstraints.pdfRatio;
            drawCell(invoice.subtotals.get(1), xPos, yPos, cellWidth, cellHeight, RIGHT, false);

            yPos -= DocConstraints.softLine*DocConstraints.pdfRatio;
            xPos = DocConstraints.subtotalLeftX*DocConstraints.pdfRatio;
            cellWidth = DocConstraints.tSubtotalLeft*DocConstraints.pdfRatio;
            drawCell(invoice.subtotals.get(2), xPos, yPos, cellWidth, cellHeight, LEFT, false);

            xPos = DocConstraints.subtotalRightX*DocConstraints.pdfRatio;
            cellWidth = DocConstraints.tSubtotalRight*DocConstraints.pdfRatio;
            drawCell(invoice.subtotals.get(3), xPos, yPos, cellWidth, cellHeight, RIGHT, false);

            if (invoice.reverseVat){
                PDType0Font font = PDType0Font.load(file, new File(StyleSheet.fontPath));
                int fontSize = 11;
                String str = invoice.reverseVatString.get(0);
                float strHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize *0.5f ;
                float strWidth = font.getStringWidth(str) / 1000 * fontSize;
                yPos -= DocConstraints.softLine*DocConstraints.pdfRatio;
                xPos = (DocConstraints.a4Width - DocConstraints.baseMargin)*DocConstraints.pdfRatio - strWidth;
                ctx.beginText();
                ctx.newLineAtOffset(xPos, yPos);
                ctx.showText(str);
                ctx.endText();

                fontSize = 8;
                ctx.setFont(font, fontSize);
                str = invoice.reverseVatString.get(1);
                strWidth = font.getStringWidth(str) / 1000 * fontSize;
                yPos -= strHeight;
                xPos = (DocConstraints.a4Width - DocConstraints.baseMargin)*DocConstraints.pdfRatio - strWidth;
                ctx.beginText();
                ctx.newLineAtOffset(xPos, yPos);
                ctx.showText(str);
                ctx.endText();

                str = invoice.reverseVatString.get(2);
                strWidth = font.getStringWidth(str) / 1000 * fontSize;
                yPos -= strHeight;
                xPos = (DocConstraints.a4Width - DocConstraints.baseMargin)*DocConstraints.pdfRatio - strWidth;
                ctx.beginText();
                ctx.newLineAtOffset(xPos, yPos);
                ctx.showText(str);
                ctx.endText();
                fontSize = 12;
                ctx.setFont(font, fontSize);
            }


            yPos -= DocConstraints.halfLine*DocConstraints.pdfRatio;
            yPos -= DocConstraints.softLine*DocConstraints.pdfRatio;
            xPos = DocConstraints.subtotalLeftX*DocConstraints.pdfRatio;
            cellWidth = DocConstraints.tSubtotalLeft*DocConstraints.pdfRatio;
            drawCell(invoice.subtotals.get(4), xPos, yPos, cellWidth, cellHeight, LEFT, false);

            xPos = DocConstraints.subtotalRightX*DocConstraints.pdfRatio;
            cellWidth = DocConstraints.tSubtotalRight*DocConstraints.pdfRatio;
            drawCell(invoice.subtotals.get(5), xPos, yPos, cellWidth, cellHeight, RIGHT, false);

        } catch (IOException e){
            db.logEvent("Exception printing table to pdf: " + e.getMessage());
        }

        // Step 7: footer 
        db.logEvent("Printing footer");

        try {
            PDType0Font font = PDType0Font.load(file, new File(StyleSheet.fontPath));
            int fontSize = 11;

            float lineHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize *0.6f ;
            if (pageCount == 1){
                yPos = DocConstraints.baseMargin*DocConstraints.pdfRatio + lineHeight;
            } else {
                yPos -= DocConstraints.baseMargin*DocConstraints.pdfRatio*2;
            }

            float lineWidth = font.getStringWidth(invoice.footer.get(0)) / 1000 * fontSize;
            xPos = (DocConstraints.a4Width*DocConstraints.pdfRatio - lineWidth)*0.5f;

            ctx.beginText();
            ctx.newLineAtOffset(xPos, yPos);
            ctx.showText(invoice.footer.get(0));
            ctx.endText();
            yPos -= lineHeight;

            lineWidth = font.getStringWidth(invoice.footer.get(1)) / 1000 * fontSize;
            xPos = (DocConstraints.a4Width*DocConstraints.pdfRatio - lineWidth)*0.5f;

            ctx.beginText();
            ctx.newLineAtOffset(xPos, yPos);
            ctx.showText(invoice.footer.get(1));
            ctx.endText();

            // page count
            if (pageCount > 1){
                String pageFooter = LanguageFile.page[invoice.lang] + pageCount + "/" + pageCount;
                lineHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize *0.6f ;
                yPos = DocConstraints.baseMargin*DocConstraints.pdfRatio + lineHeight;
                lineWidth = font.getStringWidth(pageFooter) / 1000 * fontSize;
                xPos = (DocConstraints.a4Width*DocConstraints.pdfRatio - lineWidth)*0.5f;

                ctx.beginText();
                ctx.newLineAtOffset(xPos, yPos);
                ctx.showText(pageFooter);
                ctx.endText();
            }

        } catch (IOException e){
            db.logEvent("Exception printing footer to pdf: " + e.getMessage());
        }      
    
        // Last step: saving the file
        try {
            ctx.close();
            file.save(pdfPath);
            db.logEvent("File saved");
            file.close();
        } catch (IOException e){
            db.logEvent("Error saving file: " + e.getMessage());
        }
    }

    // Helper function: draw table cell
    private void drawCell(String str, float xPos, float yPos, float cellWidth, float cellHeight, int ALIGNMENT, boolean splittable) throws IOException {

        ctx.setStrokingColor(Color.black);
        ctx.addRect(xPos, yPos, cellWidth, cellHeight);
        ctx.stroke();

        PDType0Font font = PDType0Font.load(file, new File(StyleSheet.fontPath));
        int fontSize = 11;
        ctx.setFont(font, fontSize);

        float strHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize *0.6f ;
        float strWidth = font.getStringWidth(str) / 1000 * fontSize;
        ArrayList<String> lines = new ArrayList<String>();
        int lineNumber = 1;
        
        if (splittable && strWidth >= cellWidth){
            String[] words = str.split(" ");
            String testString = "";
            
            lines.add("");
            
            // count total lines & prepare arraylist of lines
            for (String word : words){
                testString += word + " ";
                strWidth = font.getStringWidth(testString) / 1000 * fontSize;

                if (strWidth >= cellWidth){
                    testString = word + " ";
                    lineNumber++;
                    lines.add("");
                }
                lines.set(lineNumber - 1, testString);
            }
            // recalculate height
            strHeight = strHeight*lineNumber;
        } else {
            lines.add(str);
        }

        float lineHeight = strHeight/lineNumber;
        float margin = (cellHeight - strHeight + lineHeight*0.5f)*0.5f;
        float strY = yPos + margin + lineHeight*(lineNumber - 1);

        for (String line : lines){
            // Center alignment is default
            float lineWidth = font.getStringWidth(line) / 1000 * fontSize;
            float strX = xPos + (cellWidth - lineWidth)*0.5f;
            if (ALIGNMENT == LEFT){
                strX = xPos;
            } else if (ALIGNMENT == RIGHT){
                strX = xPos + (cellWidth - lineWidth);
            } 
            
            ctx.beginText();
            ctx.newLineAtOffset(strX, strY);
            ctx.showText(line);
            ctx.endText();
            strY -= lineHeight;
        }
    }
    
    public PdfPenman(Mammon parent){
        this.parent = parent;
        this.db = parent.getDb();
    }
}
