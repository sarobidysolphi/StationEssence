package stationessenceswing;

import java.util.ArrayList;
import java.util.List;

public class DonneesMemoire {

    // --- CLASSE PRODUIT ---
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

    // --- CLASSE SERVICE ---
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

    // --- CLASSE VENTE (Pour l'historique) ---
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
            this.date = java.time.LocalDate.now().toString(); // Date du jour
        }
    }

    // --- LISTES STATIQUES EN MÉMOIRE ---
    public static List<Produit> listeProduits = new ArrayList<>();
    public static List<Service> listeServices = new ArrayList<>();
    public static List<Vente> historiqueVentes = new ArrayList<>(); // Nouveau !
    
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
    
        // Méthode pour notifier qu'une vente a eu lieu
    public static void ajouterVente(DonneesMemoire.Vente vente) {
        historiqueVentes.add(vente);
        recetteDuJour += vente.montant;
    }
    
        // Méthode pour générer des données de recettes pour les 5 derniers mois (pour le graphique)
    public static int[] getRecettes5DerniersMois() {
        // Dans une vraie base de données, on ferait une requête SQL ici.
        // Pour l'instant, on simule des données.
        // Mois : [Mois 5, Mois 4, Mois 3, Mois 2, Mois 1] -> Du plus vieux au plus récent
        int[] recettes = new int[5];
        
        // On prend la recette totale actuelle, et on répartit fictivement sur 5 mois
        // (juste pour que le graphique ait des chiffres à afficher)
        int totalActuel = recetteDuJour;
        if (totalActuel == 0) {
            // S'il n'y a pas de recette, on met des valeurs aléatoires pour montrer que ça marche
            recettes[0] = 0;
            recettes[1] = 0;
            recettes[2] = 0;
            recettes[3] = 0;
            recettes[4] = 0;
        } else {
            // Répartition réaliste (le mois le plus récent a le plus de recettes)
            recettes[4] = (int)(totalActuel * 0.4); // Mois 1 (le plus récent)
            recettes[3] = (int)(totalActuel * 0.3); // Mois 2
            recettes[2] = (int)(totalActuel * 0.15); // Mois 3
            recettes[1] = (int)(totalActuel * 0.1);  // Mois 4
            recettes[0] = (int)(totalActuel * 0.05); // Mois 5 (le plus vieux)
        }
        return recettes;
    }
}