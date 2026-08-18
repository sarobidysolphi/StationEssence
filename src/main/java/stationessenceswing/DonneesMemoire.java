package stationessenceswing;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DonneesMemoire {

    public static final DateTimeFormatter FORMAT_DATE_FR = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("fr", "FR"));

    public static String aujourdHui() {
        return LocalDate.now().format(FORMAT_DATE_FR);
    }

    public static class Produit {
        public int numProd;
        public String designation;
        public String type;
        public int stock;
        public int seuil;
        public int prixParLitre;

        public Produit(int num, String des, String type, int st, int se, int pr) {
            this.numProd = num;
            this.designation = des;
            this.type = type;
            this.stock = st;
            this.seuil = se;
            this.prixParLitre = pr;
        }

        public String getStatut() {
            if (stock <= 0) return "RUPTURE";
            if (stock <= seuil) return "FAIBLE";
            return "OK";
        }
    }

    public static class Service {
        public int numSery;
        public String nom;
        public int prix;

        public Service(int num, String n, int p) {
            this.numSery = num;
            this.nom = n;
            this.prix = p;
        }
    }

    public static class Vente {
        public String nomClient;
        public String produit;
        public int litres;
        public int montant;
        public String date;

        public Vente(String client, String prod, int lit, int mont) {
            this.nomClient = client;
            this.produit = prod;
            this.litres = lit;
            this.montant = mont;
            this.date = aujourdHui();
        }
    }

    public static class Entree {
        public String produit;
        public int quantite;
        public String date;

        public Entree(String prod, int qte, String d) {
            this.produit = prod;
            this.quantite = qte;
            this.date = d;
        }
    }

    public static class Entretien {
        public String nomClient;
        public String voiture;
        public String services;
        public int total;
        public String date;

        public Entretien(String nom, String voit, String services, int tot, String d) {
            this.nomClient = nom;
            this.voiture = voit;
            this.services = services;
            this.total = tot;
            this.date = d;
        }
    }

    public static List<Produit> listeProduits = new ArrayList<>();
    public static List<Service> listeServices = new ArrayList<>();
    public static List<Vente> historiqueVentes = new ArrayList<>();
    public static List<Entree> historiqueEntrees = new ArrayList<>();
    public static List<Entretien> historiqueEntretiens = new ArrayList<>();

    public static int recetteDuJour = 0;

    static {
        listeProduits.add(new Produit(1, "Essence", "Essence", 530, 50, 5200));
        listeProduits.add(new Produit(2, "Gasoil", "Gasoil", 245, 50, 4600));
        listeProduits.add(new Produit(3, "Pétrole", "Pétrole", 90, 20, 3800));

        listeServices.add(new Service(1, "Lavage", 20000));
        listeServices.add(new Service(2, "Gonflage", 2000));
        listeServices.add(new Service(3, "Vidange", 35000));
        listeServices.add(new Service(4, "Graissage", 10000));
    }

    public static List<Produit> chargerProduits() {
        return listeProduits;
    }

    public static List<Service> chargerServices() {
        return listeServices;
    }

    public static int[] getRecettes5DerniersMois() {
        int[] recettes = new int[5];
        int totalActuel = recetteDuJour;
        if (totalActuel == 0) {
            recettes[0] = 0; recettes[1] = 0; recettes[2] = 0;
            recettes[3] = 0; recettes[4] = 0;
        } else {
            recettes[4] = (int)(totalActuel * 0.4);
            recettes[3] = (int)(totalActuel * 0.3);
            recettes[2] = (int)(totalActuel * 0.15);
            recettes[1] = (int)(totalActuel * 0.1);
            recettes[0] = (int)(totalActuel * 0.05);
        }
        return recettes;
    }
}
