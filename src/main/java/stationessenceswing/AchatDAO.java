package stationessenceswing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AchatDAO {

    public static List<Achat> getAll() {
        List<Achat> liste = new ArrayList<>();
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT a.numAchat, p.Design, a.nomClient, a.nbrLitre, a.dateAchat " +
                 "FROM ACHAT a JOIN PRODUIT p ON a.numProd = p.numProd ORDER BY a.dateAchat DESC")) {
            while (rs.next()) {
                liste.add(new Achat(
                    rs.getString("numAchat"),
                    rs.getString("Design"),
                    rs.getString("nomClient"),
                    rs.getInt("nbrLitre"),
                    rs.getString("dateAchat")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static boolean ajouter(Achat a, String numProd) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO ACHAT (numAchat, numProd, nomClient, nbrLitre, dateAchat) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, a.getNumAchat());
            ps.setString(2, numProd);
            ps.setString(3, a.getNomClient());
            ps.setInt(4, a.getNbrLitre());
            ps.setString(5, a.getDateAchat());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                ProduitDAO.retirerStock(numProd, a.getNbrLitre());
            }
            return ok;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean supprimer(String numAchat) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM ACHAT WHERE numAchat=?")) {
            ps.setString(1, numAchat);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getRecetteTotale() {
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT COALESCE(SUM(a.nbrLitre * p.stock), 0) FROM ACHAT a JOIN PRODUIT p ON a.numProd = p.numProd")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getRecetteDuJour() {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT COALESCE(SUM(a.nbrLitre * p.stock), 0) FROM ACHAT a JOIN PRODUIT p ON a.numProd = p.numProd WHERE a.dateAchat = CURDATE()")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<String[]> getTop5Clients() {
        List<String[]> liste = new ArrayList<>();
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT a.nomClient, COALESCE(SUM(a.nbrLitre * p.stock), 0) as total " +
                 "FROM ACHAT a JOIN PRODUIT p ON a.numProd = p.numProd " +
                 "GROUP BY a.nomClient ORDER BY total DESC LIMIT 5")) {
            while (rs.next()) {
                liste.add(new String[]{rs.getString("nomClient"), String.valueOf(rs.getInt("total"))});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static List<String[]> getRecettes5Mois() {
        List<String[]> liste = new ArrayList<>();
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT MONTH(a.dateAchat) as mois, COALESCE(SUM(a.nbrLitre * p.stock), 0) as total " +
                 "FROM ACHAT a JOIN PRODUIT p ON a.numProd = p.numProd " +
                 "WHERE a.dateAchat >= DATE_SUB(CURDATE(), INTERVAL 5 MONTH) " +
                 "GROUP BY MONTH(a.dateAchat) ORDER BY mois")) {
            while (rs.next()) {
                liste.add(new String[]{String.valueOf(rs.getInt("mois")), String.valueOf(rs.getInt("total"))});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static List<Achat> rechercherParClient(String nom) {
        List<Achat> liste = new ArrayList<>();
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT a.numAchat, p.Design, a.nomClient, a.nbrLitre, a.dateAchat " +
                 "FROM ACHAT a JOIN PRODUIT p ON a.numProd = p.numProd WHERE a.nomClient LIKE ?")) {
            ps.setString(1, "%" + nom + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(new Achat(
                    rs.getString("numAchat"),
                    rs.getString("Design"),
                    rs.getString("nomClient"),
                    rs.getInt("nbrLitre"),
                    rs.getString("dateAchat")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static String genererId() {
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM ACHAT")) {
            if (rs.next()) {
                return "A" + String.format("%03d", rs.getInt(1) + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "A001";
    }
}
