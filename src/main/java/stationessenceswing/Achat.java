package stationessenceswing;

public class Achat {
    private String numAchat;
    private String numProd;
    private String nomClient;
    private int nbrLitre;
    private String dateAchat;

    public Achat(String numAchat, String numProd, String nomClient, int nbrLitre, String dateAchat) {
        this.numAchat = numAchat;
        this.numProd = numProd;
        this.nomClient = nomClient;
        this.nbrLitre = nbrLitre;
        this.dateAchat = dateAchat;
    }

    public String getNumAchat() { return numAchat; }
    public String getNumProd() { return numProd; }
    public String getNomClient() { return nomClient; }
    public int getNbrLitre() { return nbrLitre; }
    public String getDateAchat() { return dateAchat; }
}
