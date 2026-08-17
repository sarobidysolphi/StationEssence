package stationessenceswing;

import java.util.ArrayList;
import java.util.List;

public class DonneesMemoire {

    // --- PRODUIT ---
    public static class Produit {
        public int numProd;
        public String designation;
        public int stock;
        public int seuil;
        public int prixParLitre;

        public Produit(int num, String des, int st, int se, int pr) {
            this.numProd = num;
            this.designation = des;
            this.stock = st;
            this.seuil = se;
            this.prixParLitre = pr;
        }
    }

    // --- SERVICE ---
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

    // --- VENTE ---
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
            this.date = java.time.LocalDate.now().toString();
        }
    }

    // --- NOUVEAU : ENTREE (Stock) ---
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

    // --- NOUVEAU : ENTRETIEN ---
    public static class Entretien {
        public String nomClient;
        public String voiture;
        public int total;
        public String date;

        public Entretien(String nom, String voit, int tot, String d) {
            this.nomClient = nom;
            this.voiture = voit;
            this.total = tot;
            this.date = d;
        }
    }

    // --- LISTES STATIQUES EN MÉMOIRE ---
    public static List<Produit> listeProduits = new ArrayList<>();
    public static List<Service> listeServices = new ArrayList<>();
    public static List<Vente> historiqueVentes = new ArrayList<>();
    public static List<Entree> historiqueEntrees = new ArrayList<>();
    public static List<Entretien> historiqueEntretiens = new ArrayList<>();
    
    public static int recetteDuJour = 0;

    // --- INITIALISATION DES DONNÉES ---
    static {
        listeProduits.add(new Produit(1, "Essence", 530, 50, 5200));
        listeProduits.add(new Produit(2, "Gasoil", 245, 50, 4600));
        listeProduits.add(new Produit(3, "Pétrole", 90, 20, 3800));

        listeServices.add(new Service(1, "Lavage", 20000));
        listeServices.add(new Service(2, "Gonflage", 2000));
        listeServices.add(new Service(3, "Vidange", 35000));
        listeServices.add(new Service(4, "Graissage", 10000));
    }

    // --- MÉTHODES DE RÉCUPÉRATION ---
    public static List<Produit> chargerProduits() {
        return listeProduits;
    }

    public static List<Service> chargerServices() {
        return listeServices;
    }
        // --- MÉTHODE POUR LES STATISTIQUES (Graphique 5 mois) ---
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