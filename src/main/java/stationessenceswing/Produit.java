package stationessenceswing;

public class Produit {
    private String numProd;
    private String designation;
    private int stock;

    public Produit(String numProd, String designation, int stock) {
        this.numProd = numProd;
        this.designation = designation;
        this.stock = stock;
    }

    public String getNumProd() { return numProd; }
    public String getDesignation() { return designation; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getStatut() {
        if (stock <= 0) return "RUPTURE";
        if (stock < 10) return "FAIBLE";
        return "OK";
    }
}
