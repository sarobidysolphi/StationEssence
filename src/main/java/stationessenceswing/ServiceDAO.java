package stationessenceswing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    public static List<ServiceEnt> getAll() {
        List<ServiceEnt> liste = new ArrayList<>();
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM SERVICE")) {
            while (rs.next()) {
                liste.add(new ServiceEnt(rs.getString("numServ"), rs.getString("service"), rs.getInt("prix")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static ServiceEnt getById(String numServ) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM SERVICE WHERE numServ = ?")) {
            ps.setString(1, numServ);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ServiceEnt(rs.getString("numServ"), rs.getString("service"), rs.getInt("prix"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean ajouter(ServiceEnt s) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO SERVICE (numServ, service, prix) VALUES (?, ?, ?)")) {
            ps.setString(1, s.getNumServ());
            ps.setString(2, s.getService());
            ps.setInt(3, s.getPrix());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean modifier(String ancienId, ServiceEnt s) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE SERVICE SET numServ=?, service=?, prix=? WHERE numServ=?")) {
            ps.setString(1, s.getNumServ());
            ps.setString(2, s.getService());
            ps.setInt(3, s.getPrix());
            ps.setString(4, ancienId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean supprimer(String numServ) {
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM SERVICE WHERE numServ=?")) {
            ps.setString(1, numServ);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String genererId() {
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM SERVICE")) {
            if (rs.next()) {
                return "S" + String.format("%03d", rs.getInt(1) + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "S001";
    }
}
