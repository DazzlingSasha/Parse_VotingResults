package Dao;

import Model.Depute;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeputiesDao {
    private static final String ID = "id";
    private static final String NAME_DEPUTY = "name_deputy";
    private static final String TABLE_DEPUTIES = "DEPUTIES";

    private Logger lgr = Logger.getLogger(VotingDate.class.getName());

    public void insert(Map<String, String> mapOfDep) {
        String query = "INSERT INTO "+TABLE_DEPUTIES+" ("+NAME_DEPUTY+") VALUES(?)";
        try (Connection con = new PostgreSQLConnection().connection();
             PreparedStatement pst = con.prepareStatement(query)) {

            for (Map.Entry<String,String> map: mapOfDep.entrySet()) {
                pst.setString(1, map.getKey());
                pst.executeUpdate();
            }

        } catch (SQLException ex) {
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    public void insertOne(String name) {
        String query = "INSERT INTO "+TABLE_DEPUTIES+" ("+NAME_DEPUTY+") VALUES(?)";
        try (Connection con = new PostgreSQLConnection().connection();
             PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, name);
                pst.executeUpdate();
        } catch (SQLException ex) {
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public int searchIdByName(String name) {
        String query = "SELECT id FROM "+TABLE_DEPUTIES+" WHERE "+NAME_DEPUTY+" = '" + name +"'";
        int id = 0;
        ArrayList<Integer> listId = new ArrayList<>();
        try (Connection con = new PostgreSQLConnection().connection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                listId.add(rs.getInt("id"));
            }

        } catch (SQLException ex) {
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        if (listId.isEmpty()) {
            lgr.log(Level.INFO, "List into "+TABLE_DEPUTIES+" is empty");
            return -1;
        }
        if (listId.size() > 0) {
            lgr.log(Level.INFO, "List into "+TABLE_DEPUTIES+" more that one write down");
        }

        return listId.get(0);
    }

    public List<Depute> selectAll() {
        String query = "SELECT * FROM "+TABLE_DEPUTIES;

        ArrayList<Depute> list = new ArrayList<>();
        try (Connection con = new PostgreSQLConnection().connection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query))
        {

            while (rs.next()) {
                Depute result = new Depute();
                result.setId(rs.getInt(ID));
                result.setName(rs.getString(NAME_DEPUTY));
                list.add(result);
            }
        } catch (SQLException ex) {
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        if (list.isEmpty()) {
            lgr.log(Level.INFO, "List into "+TABLE_DEPUTIES+" is empty");
        }

        return list;
    }

}
