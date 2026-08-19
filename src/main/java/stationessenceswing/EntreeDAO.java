package stationessenceswing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntreeDAO {

    public static List<Entree> getAll() {
        List<Entree> liste = new ArrayList<>();
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT e.numEntree, p.Design, e.stockEntree, e.dateEntree " +
                 "FROM ENTREE e JOIN PRODUIT p ON e.numProd = p.numProd ORDER BY e.dateEntree DESC")) {
            while (rs.next()) {
                liste.add(new Entree(
                    rs.getString("numEntree"),
                    rs.getString("Design"),
                    rs.getInt("stockEntree"),
                    rs.getString("dateEntree")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static boolean ajouter(Entree e, String numProd) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO ENTREE (numEntree, numProd, stockEntree, dateEntree) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, e.getNumEntree());
            ps.setString(2, numProd);
            ps.setInt(3, e.getStockEntree());
            ps.setString(4, e.getDateEntree());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                ProduitDAO.ajouterStock(numProd, e.getStockEntree());
            }
            return ok;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean supprimer(String numEntree) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM ENTREE WHERE numEntree=?")) {
            ps.setString(1, numEntree);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String genererId() {
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM ENTREE")) {
            if (rs.next()) {
                return "E" + String.format("%03d", rs.getInt(1) + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "E001";
    }
}
