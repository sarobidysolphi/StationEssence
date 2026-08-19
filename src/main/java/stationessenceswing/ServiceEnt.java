package stationessenceswing;

public class ServiceEnt {
    private String numServ;
    private String service;
    private int prix;

    public ServiceEnt(String numServ, String service, int prix) {
        this.numServ = numServ;
        this.service = service;
        this.prix = prix;
    }

    public String getNumServ() { return numServ; }
    public String getService() { return service; }
    public int getPrix() { return prix; }
}
