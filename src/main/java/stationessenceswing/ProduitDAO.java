package stationessenceswing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    public static List<Produit> getAll() {
        List<Produit> liste = new ArrayList<>();
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM PRODUIT")) {
            while (rs.next()) {
                liste.add(new Produit(rs.getString("numProd"), rs.getString("Design"), rs.getInt("stock")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static Produit getById(String numProd) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM PRODUIT WHERE numProd = ?")) {
            ps.setString(1, numProd);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Produit(rs.getString("numProd"), rs.getString("Design"), rs.getInt("stock"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean ajouter(Produit p) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO PRODUIT (numProd, Design, stock) VALUES (?, ?, ?)")) {
            ps.setString(1, p.getNumProd());
            ps.setString(2, p.getDesignation());
            ps.setInt(3, p.getStock());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean modifier(String ancienId, Produit p) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE PRODUIT SET numProd=?, Design=?, stock=? WHERE numProd=?")) {
            ps.setString(1, p.getNumProd());
            ps.setString(2, p.getDesignation());
            ps.setInt(3, p.getStock());
            ps.setString(4, ancienId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean supprimer(String numProd) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM PRODUIT WHERE numProd=?")) {
            ps.setString(1, numProd);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean ajouterStock(String numProd, int quantite) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE PRODUIT SET stock = stock + ? WHERE numProd = ?")) {
            ps.setInt(1, quantite);
            ps.setString(2, numProd);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean retirerStock(String numProd, int quantite) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE PRODUIT SET stock = stock - ? WHERE numProd = ?")) {
            ps.setInt(1, quantite);
            ps.setString(2, numProd);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Produit> getAlertes() {
        List<Produit> liste = new ArrayList<>();
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM PRODUIT WHERE stock < 10")) {
            while (rs.next()) {
                liste.add(new Produit(rs.getString("numProd"), rs.getString("Design"), rs.getInt("stock")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
}
