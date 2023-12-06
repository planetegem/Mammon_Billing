package be.planetegem.mammon.util;

import java.util.ArrayList;

import be.planetegem.mammon.statics.LanguageFile;

public class FormattedInvoice {
    public int lang;
    public ArrayList<String> headers, subtotals;
    public ArrayList<FormattedCell> descriptionColumn, dateColumn, amountColumn, priceColumn, totalsColumn;
    public int lineCount;

    public boolean reverseVat = false;
    public ArrayList<String> reverseVatString;

    public boolean multiPage = false;
    public ArrayList<ArrayList<FormattedCell>> multiDescriptionColumn, multiDateColumn, multiAmountColumn, multiPriceColumn, multiTotalsColumn;
    private int pageLimit = 18;

    public void splitTable(){
         if (this.lineCount > pageLimit){
            this.multiPage = true;

            multiDescriptionColumn = splitColumn(descriptionColumn);
            multiDateColumn = splitColumn(dateColumn);
            multiAmountColumn = splitColumn(amountColumn);
            multiPriceColumn = splitColumn(priceColumn);
            multiTotalsColumn = splitColumn(totalsColumn);
         }
    }
    public ArrayList<ArrayList<FormattedCell>> splitColumn(ArrayList<FormattedCell> column){
        ArrayList<ArrayList<FormattedCell>> splitColumn = new ArrayList<ArrayList<FormattedCell>>();
        splitColumn.add(new ArrayList<FormattedCell>());
        splitColumn.add(new ArrayList<FormattedCell>());

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

    public void setDescriptionColumn(ArrayList<FormattedCell> descriptionColumn){
        this.descriptionColumn = descriptionColumn;
    }
    public void setDateColumn(ArrayList<FormattedCell> dateColumn){
        this.dateColumn = dateColumn;
    }
    public void setAmountColumn(ArrayList<FormattedCell> amountColumn){
        this.amountColumn = amountColumn;
    }
    public void setPriceColumn(ArrayList<FormattedCell> priceColumn){
        this.priceColumn = priceColumn;
    }
    public void setTotalsColumn(ArrayList<FormattedCell> totalsColumn){
        this.totalsColumn = totalsColumn;
    }
    public void setSubtotals(ArrayList<String> subtotals){
        this.subtotals = subtotals;
    }
    public void setReverseVat(){
        this.reverseVat = true;
        this.reverseVatString = new ArrayList<String>();
        reverseVatString.add(LanguageFile.reverseCharge[lang]);
        reverseVatString.add("(Intracommunautaire dienstverrichting niet onderworpen ");
        reverseVatString.add("aan Belgische btw art. 21, § 2 van het Wbtw)");
    }
    private void setHeaders(){
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
    public int getTableSize(){
        return lineCount;
    }

    // Profile detailsµ
    public boolean hasLogo = true;
    public String logoValue = "";
    public String profileString = "";


    public void setProfile(){

    }

    public FormattedInvoice(int lang) {
        this.lang = lang;
        setHeaders();
    }
}
