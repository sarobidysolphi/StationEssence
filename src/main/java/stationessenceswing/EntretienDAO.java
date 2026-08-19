package stationessenceswing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntretienDAO {

    public static List<Entretien> getAll() {
        List<Entretien> liste = new ArrayList<>();
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT e.numEntr, s.service, e.Immatriculation_voiture, e.nomClient, e.dateEntretien " +
                 "FROM ENTRETIEN e JOIN SERVICE s ON e.numServ = s.numServ ORDER BY e.dateEntretien DESC")) {
            while (rs.next()) {
                liste.add(new Entretien(
                    rs.getString("numEntr"),
                    rs.getString("service"),
                    rs.getString("Immatriculation_voiture"),
                    rs.getString("nomClient"),
                    rs.getString("dateEntretien")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static boolean ajouter(Entretien ent, String numServ) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO ENTRETIEN (numEntr, numServ, Immatriculation_voiture, nomClient, dateEntretien) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, ent.getNumEntr());
            ps.setString(2, numServ);
            ps.setString(3, ent.getImmatriculation());
            ps.setString(4, ent.getNomClient());
            ps.setString(5, ent.getDateEntretien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean modifier(String numEntr, String numServ, String immatriculation, String nomClient) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE ENTRETIEN SET numServ=?, Immatriculation_voiture=?, nomClient=? WHERE numEntr=?")) {
            ps.setString(1, numServ);
            ps.setString(2, immatriculation);
            ps.setString(3, nomClient);
            ps.setString(4, numEntr);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean supprimer(String numEntr) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM ENTRETIEN WHERE numEntr=?")) {
            ps.setString(1, numEntr);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String genererId() {
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MAX(CAST(SUBSTRING(numEntr, 3) AS UNSIGNED)) FROM ENTRETIEN")) {
            if (rs.next()) {
                int max = rs.getInt(1);
                return "ET" + String.format("%03d", max + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ET001";
    }
}
