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

    public static boolean modifier(String numEntree, String numProd, int nouvelleQte) {
        try (Connection conn = ConnexionBD.getConnection()) {
            PreparedStatement psGet = conn.prepareStatement("SELECT stockEntree FROM ENTREE WHERE numEntree=?");
            psGet.setString(1, numEntree);
            ResultSet rs = psGet.executeQuery();
            if (rs.next()) {
                int ancienneQte = rs.getInt("stockEntree");
                PreparedStatement psUpd = conn.prepareStatement("UPDATE ENTREE SET stockEntree=? WHERE numEntree=?");
                psUpd.setInt(1, nouvelleQte);
                psUpd.setString(2, numEntree);
                boolean ok = psUpd.executeUpdate() > 0;
                if (ok) {
                    ProduitDAO.retirerStock(numProd, ancienneQte);
                    ProduitDAO.ajouterStock(numProd, nouvelleQte);
                }
                return ok;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean supprimer(String numEntree) {
        try (Connection conn = ConnexionBD.getConnection()) {
            PreparedStatement psGet = conn.prepareStatement("SELECT numProd, stockEntree FROM ENTREE WHERE numEntree=?");
            psGet.setString(1, numEntree);
            ResultSet rs = psGet.executeQuery();
            if (rs.next()) {
                String numProd = rs.getString("numProd");
                int qte = rs.getInt("stockEntree");
                PreparedStatement psDel = conn.prepareStatement("DELETE FROM ENTREE WHERE numEntree=?");
                psDel.setString(1, numEntree);
                boolean ok = psDel.executeUpdate() > 0;
                if (ok) {
                    ProduitDAO.retirerStock(numProd, qte);
                }
                return ok;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
