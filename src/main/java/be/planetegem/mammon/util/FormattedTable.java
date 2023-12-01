package be.planetegem.mammon.util;

import java.util.ArrayList;

import be.planetegem.mammon.statics.LanguageFile;

public class FormattedTable {
    public int lang;
    public ArrayList<String> headers, totalsColumn, subtotals;
    public ArrayList<FormattedCell> descriptionColumn, dateColumn, amountColumn, priceColumn;

    public boolean reverseVat = false;
    public ArrayList<String> reverseVatString;
    

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
    public void setTotalsColumn(ArrayList<String> totalsColumn){
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


    public int getTableSize(){
        return totalsColumn.size();
    }
    public FormattedTable(int lang) {
        this.lang = lang;
        setHeaders();

    }



}
