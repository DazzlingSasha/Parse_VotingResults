package Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class VotingDate {


    public void insert(String date, String title) {
        try (Connection con = new PostgreSQLConnection().connection();
             PreparedStatement pst = con.prepareStatement("INSERT INTO VOTING_DATE(DATE_VOTING, TITLE) VALUES(?, ?)")) {

//            for (int i = 1; i <= 1000; i++) {
                pst.setString(1, date);
                pst.setString(2, title);
                pst.executeUpdate();
//            }

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(VotingDate.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    public int searchIdByName(String date) {
        String query = "SELECT id FROM voting_date WHERE date_voting = '" + date+"'";
        int id = 0;
        ArrayList<Integer> listId = new ArrayList<>();
        try (Connection con = new PostgreSQLConnection().connection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while ( rs.next() ) {
                listId.add(rs.getInt("id"));
                System.out.println(id);
            }

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(VotingDate.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        if(listId.isEmpty()){
            return -1;
        }
        if(listId.size() > 0 ){

        }

        return listId.get(0);
    }
}
