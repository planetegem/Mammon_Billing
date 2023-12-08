package be.planetegem.mammon.ivf;

import java.util.ArrayList;
import java.util.HashMap;

import be.planetegem.mammon.statics.DocConstraints;
import be.planetegem.mammon.statics.LanguageFile;
import be.planetegem.mammon.util.FormattedCell;

public class FormattedInvoice {
    
    public int lang;

    // Profile
    public boolean hasLogo = true; // keeps track of need for ad hoc logo
    public String logoValue = ""; // either path to image or company name
    public String profileString = ""; // combine all profile details into 1 string

    public void setProfile(HashMap<String, String> profile){
        // Set logo values                 
        this.logoValue = profile.get("LOGOPATH");
        if (logoValue.equals("")){
            this.logoValue = profile.get("COMPANYNAME");
            this.hasLogo = false;
        }

        // get details in string
        String unicodeSymbol = " \u2981 ";
        
        if (!profile.get("LOGOPATH").equals("") && profile.get("FAMILYNAME").equals("")){
            this.profileString += profile.get("COMPANYNAME");
            this.profileString += unicodeSymbol;
        } else if (!profile.get("FAMILYNAME").equals("")){
            this.profileString += profile.get("FIRSTNAME") + " " + profile.get("FAMILYNAME");
            this.profileString += unicodeSymbol;
        }

        // Address label
        this.profileString += profile.get("STREETNAME") + " " + profile.get("HOUSENUMBER");
        if (!profile.get("BOXNUMBER").equals("")){
            this.profileString += " " + profile.get("BOXNUMBER");
        }
        this.profileString += ", " + profile.get("POSTALCODE") + " " + profile.get("PLACENAME");
        if (profile.get("COUNTRYNAME").equals("België")){
            this.profileString += " - " + LanguageFile.belgium[lang];
        } else {
            this.profileString += " - " + profile.get("COUNTRYNAME");
        }
        this.profileString += unicodeSymbol;

        // Vat label
        this.profileString += LanguageFile.vat[lang] + " " + profile.get("VATNUMBER");

        if (!profile.get("ACCOUNTNUMBER").equals("")){
            this.profileString += unicodeSymbol;
            this.profileString += "IBAN " + profile.get("ACCOUNTNUMBER");
        }
    }

    // customer
    public String customerHeader = "";
    public ArrayList<String> customerLines = new ArrayList<String>();

    public void setCustomer(HashMap<String, String> customer){
        this.customerHeader = LanguageFile.to[this.lang];
    
        // Check if contact person was provided
        if (!customer.get("FAMILYNAME").equals("")){
            this.customerLines.add(customer.get("FIRSTNAME") + " " + customer.get("FAMILYNAME"));
        }
        // Company name immediately beneath & empty line
        this.customerLines.add(customer.get("COMPANYNAME"));
        this.customerLines.add("");
        // Address
        String address = customer.get("STREETNAME") + " " + customer.get("HOUSENUMBER");
        if (!customer.get("BOXNUMBER").equals("")){
            address += " " + customer.get("BOXNUMBER");
        }
        this.customerLines.add(address);
        this.customerLines.add(customer.get("POSTALCODE") + " " + customer.get("PLACENAME") + " - " + customer.get("COUNTRYNAME"));
        this.customerLines.add("");

        // Vat number: compare customertype, potentially add 2nd line
        if (!customer.get("VATNUMBER").equals("")){
            customerLines.add(LanguageFile.vat[lang] + " " + customer.get("VATNUMBER")); 
            if (!customer.get("VATNUMBER2").equals("")){
                customerLines.add(customer.get("VATNUMBER2"));
            }
        }
    }

    // invoice details
    public String ivHeader = "";
    public String ivNumber = "";
    public ArrayList<String> footer = new ArrayList<String>();
    public boolean reverseVat = false;
    public ArrayList<String> reverseVatString;

    public void setInvoiceDetails(HashMap<String, String> invoiceDetails){
        this.ivHeader = invoiceDetails.get("invoiceType") + " " + LanguageFile.created[lang] + " ";
        this.ivHeader += invoiceDetails.get("invoicePlace") + " " + LanguageFile.on[lang] + " ";
        this.ivHeader += invoiceDetails.get("invoiceDate");

        if (invoiceDetails.get("realType").equals("POSITIVE")){
            this.ivNumber += LanguageFile.invoiceNr[lang] + " "; 
        } else if (invoiceDetails.get("realType").equals("NEGATIVE")){
            this.ivNumber += LanguageFile.creditNote[lang] + " "; 
        }
        this.ivNumber += invoiceDetails.get("invoiceNumber");

        if (invoiceDetails.get("vatType").equals("0%")){
            this.reverseVat = true;
            this.reverseVatString = new ArrayList<String>();
            reverseVatString.add(LanguageFile.reverseCharge[lang]);
            reverseVatString.add("(Intracommunautaire dienstverrichting niet onderworpen ");
            reverseVatString.add("aan Belgische btw art. 21, § 2 van het Wbtw)");
        }

        String footerLine = LanguageFile.footer1[lang];
        footerLine += " " + invoiceDetails.get("invoiceTerm") + " ";
        footerLine += LanguageFile.footer2[lang];
        this.footer.add(footerLine);
        footerLine = "IBAN " + invoiceDetails.get("invoiceAccount");
        this.footer.add(footerLine);
    }

    // invoice table
    public ArrayList<String> headers, subtotals;
    public ArrayList<FormattedCell> descriptionColumn, dateColumn, amountColumn, priceColumn, totalsColumn;
    public ArrayList<ArrayList<FormattedCell>> multiDescriptionColumn, multiDateColumn, multiAmountColumn, multiPriceColumn, multiTotalsColumn;
    private int pageLimit = 18;
    public int lineCount;

    public void setDescriptionColumn(ArrayList<FormattedCell> descriptionColumn){
        this.descriptionColumn = descriptionColumn;
        this.multiDescriptionColumn = splitColumn(descriptionColumn);
    }
    public void setDateColumn(ArrayList<FormattedCell> dateColumn){
        this.dateColumn = dateColumn;
        this.multiDateColumn = splitColumn(dateColumn);
    }
    public void setAmountColumn(ArrayList<FormattedCell> amountColumn){
        this.amountColumn = amountColumn;
        this.multiAmountColumn = splitColumn(amountColumn);
    }
    public void setPriceColumn(ArrayList<FormattedCell> priceColumn){
        this.priceColumn = priceColumn;
        this.multiPriceColumn = splitColumn(priceColumn);
    }
    public void setTotalsColumn(ArrayList<FormattedCell> totalsColumn){
        this.totalsColumn = totalsColumn;
        this.multiTotalsColumn = splitColumn(totalsColumn);
    }
    public void setSubtotals(ArrayList<String> subtotals){
        this.subtotals = subtotals;
    }
    public void setTableHeaders(){
        this.headers = new ArrayList<String>();
        this.headers.add(LanguageFile.descr[lang]);
        this.headers.add(LanguageFile.date[lang]);
        this.headers.add(LanguageFile.amount[lang]);
        this.headers.add(LanguageFile.price[lang]);
        this.headers.add(LanguageFile.lTotal[lang]);
    }
    public void setLineCount(int lineCount){
        this.lineCount = lineCount;
    }
    public void applyMergedCellPatch(){
        // compare last row of first page with first row of 2nd page
        boolean patchNeeded = true;

        ArrayList<FormattedCell> page1 = multiDescriptionColumn.get(0);
        ArrayList<FormattedCell> page2 = multiDescriptionColumn.get(1);
        if (!page1.get(page1.size() - 1).string.equals(page2.get(0).string)){
            patchNeeded = false;
        }
        page1 = multiDateColumn.get(0);
        page2 = multiDateColumn.get(1);
        if (!page1.get(page1.size() - 1).string.equals(page2.get(0).string)){
            patchNeeded = false;
        }
        page1 = multiAmountColumn.get(0);
        page2 = multiAmountColumn.get(1);
        if (!page1.get(page1.size() - 1).string.equals(page2.get(0).string) || !page1.get(page1.size() - 1).mergedCell){
            patchNeeded = false;
        }
        page1 = multiPriceColumn.get(0);
        page2 = multiPriceColumn.get(1);
        if (!page1.get(page1.size() - 1).string.equals(page2.get(0).string)){
            patchNeeded = false;
        }
        page1 = multiTotalsColumn.get(0);
        page2 = multiTotalsColumn.get(1);
        if (!page1.get(page1.size() - 1).string.equals(page2.get(0).string) || !page1.get(page1.size() - 1).mergedCell){
            patchNeeded = false;
        }
        
        if (patchNeeded){
            // if patch is needed, check height difference
            int diff = multiTotalsColumn.get(0).get(page1.size() - 1).height;
            int lineCount = multiDescriptionColumn.get(1).get(0).lineCount(DocConstraints.tDescription*DocConstraints.previewRatio);
            
            int diff2;
            if (lineCount > diff + multiDescriptionColumn.get(1).get(0).height){
                diff2 = lineCount - multiDescriptionColumn.get(1).get(0).height;
            } else {
                diff2 = diff;
            }

            // add this height to first cells of page2
            multiDescriptionColumn.get(1).get(0).height += diff2;
            multiDateColumn.get(1).get(0).height += diff2;
            multiAmountColumn.get(1).get(0).height += diff2;
            multiPriceColumn.get(1).get(0).height += diff2;
            multiTotalsColumn.get(1).get(0).height += diff2;

            // move all subsequent cells down
            ArrayList<FormattedCell> currentColumn = multiDescriptionColumn.get(1);
            for (int i = 1; i < currentColumn.size(); i++){
                currentColumn.get(i).y += diff2;
            }
            currentColumn = multiDateColumn.get(1);
            for (int i = 1; i < currentColumn.size(); i++){
                currentColumn.get(i).y += diff2;
            }
            currentColumn = multiAmountColumn.get(1);
            for (int i = 1; i < currentColumn.size(); i++){
                currentColumn.get(i).y += diff2;
            }
            currentColumn = multiPriceColumn.get(1);
            for (int i = 1; i < currentColumn.size(); i++){
                currentColumn.get(i).y += diff2;
            }
            currentColumn = multiTotalsColumn.get(1);
            for (int i = 1; i < currentColumn.size(); i++){
                currentColumn.get(i).y += diff2;
            }

            // Clean up last cells of page 1
            currentColumn = multiTotalsColumn.get(0);
            currentColumn.get(currentColumn.size() - 1).height -= diff;
            if (currentColumn.get(currentColumn.size() - 1).height <= 0){
                currentColumn.remove(currentColumn.size() - 1);
            }
            currentColumn = multiPriceColumn.get(0);
            currentColumn.get(currentColumn.size() - 1).height -= diff;
            if (currentColumn.get(currentColumn.size() - 1).height <= 0){
                currentColumn.remove(currentColumn.size() - 1);
            }
            currentColumn = multiAmountColumn.get(0);
            currentColumn.get(currentColumn.size() - 1).height -= diff;
            if (currentColumn.get(currentColumn.size() - 1).height <= 0){
                currentColumn.remove(currentColumn.size() - 1);
            }
            currentColumn = multiDateColumn.get(0);
            currentColumn.get(currentColumn.size() - 1).height -= diff;
            if (currentColumn.get(currentColumn.size() - 1).height <= 0){
                currentColumn.remove(currentColumn.size() - 1);
            }
            currentColumn = multiDescriptionColumn.get(0);
            currentColumn.get(currentColumn.size() - 1).height -= diff;
            if (currentColumn.get(currentColumn.size() - 1).height <= 0){
                currentColumn.remove(currentColumn.size() - 1);
            } else {
                // check if height needs to be extended for text wrap
                FormattedCell currentCell = currentColumn.get(currentColumn.size() - 1);
                lineCount = currentCell.lineCount(DocConstraints.tDescription*DocConstraints.previewRatio);
                if (lineCount > currentCell.height){
                    diff = lineCount - currentCell.height;   
                    currentCell.height += diff;

                    currentColumn = multiDateColumn.get(0);
                    currentColumn.get(currentColumn.size() - 1).height += diff;
                    currentColumn = multiAmountColumn.get(0);
                    currentColumn.get(currentColumn.size() - 1).height += diff;
                    currentColumn = multiPriceColumn.get(0);
                    currentColumn.get(currentColumn.size() - 1).height += diff;
                    currentColumn = multiTotalsColumn.get(0);
                    currentColumn.get(currentColumn.size() - 1).height += diff;
                }
            }
        }
    }

    public ArrayList<ArrayList<FormattedCell>> splitColumn(ArrayList<FormattedCell> column){
        ArrayList<ArrayList<FormattedCell>> splitColumn = new ArrayList<ArrayList<FormattedCell>>();
        splitColumn.add(new ArrayList<FormattedCell>());
        splitColumn.add(new ArrayList<FormattedCell>());

        // determine pageLimit
        if (this.reverseVat){
            pageLimit = 13;
        } else {
            pageLimit = 15;
        }
        if (this.lineCount > pageLimit){
            pageLimit += 3;
            if (this.reverseVat){
                pageLimit += 2;
            }
        }
        for (FormattedCell cell : column){
            if (cell.y > pageLimit){
                cell.y -= pageLimit;
                splitColumn.get(1).add(cell);
            } else if (cell.y + cell.height > pageLimit){
                    
                // split cell
                int newHeight = cell.y + cell.height - pageLimit;
                FormattedCell newCell = new FormattedCell(0, newHeight, cell.string);
                splitColumn.get(1).add(newCell);
                if (cell.height - newHeight > 0){
                    cell.height -= newHeight;
                    splitColumn.get(0).add(cell);
                }
            } else {
                splitColumn.get(0).add(cell);
            }
        }
        return splitColumn;
    }
    public int getTableSize(ArrayList<FormattedCell> page){
        return page.get(page.size() - 1).y + page.get(page.size() - 1).height;
    }
    public FormattedInvoice(int lang) {
        this.lang = lang;
    }
}
