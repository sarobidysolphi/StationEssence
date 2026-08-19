package stationessenceswing;

public class Entree {
    private String numEntree;
    private String numProd;
    private int stockEntree;
    private String dateEntree;

    public Entree(String numEntree, String numProd, int stockEntree, String dateEntree) {
        this.numEntree = numEntree;
        this.numProd = numProd;
        this.stockEntree = stockEntree;
        this.dateEntree = dateEntree;
    }

    public String getNumEntree() { return numEntree; }
    public String getNumProd() { return numProd; }
    public int getStockEntree() { return stockEntree; }
    public String getDateEntree() { return dateEntree; }
}
