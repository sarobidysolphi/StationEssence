package stationessenceswing;

public class Entretien {
    private String numEntr;
    private String numServ;
    private String immatriculation;
    private String nomClient;
    private String dateEntretien;

    public Entretien(String numEntr, String numServ, String immatriculation, String nomClient, String dateEntretien) {
        this.numEntr = numEntr;
        this.numServ = numServ;
        this.immatriculation = immatriculation;
        this.nomClient = nomClient;
        this.dateEntretien = dateEntretien;
    }

    public String getNumEntr() { return numEntr; }
    public String getNumServ() { return numServ; }
    public String getImmatriculation() { return immatriculation; }
    public String getNomClient() { return nomClient; }
    public String getDateEntretien() { return dateEntretien; }
}
