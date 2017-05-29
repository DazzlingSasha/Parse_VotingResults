package Dao;

import Model.TitleMeeting;
import Utility.CONST;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TitleMeetingsDao {
    private static final String ID = "id";
    private static final String ID_DATE = "id_date";
    private static final String NAME_TITLE = "name_title";
    private static final String RESULT = "result";
    private static final String COUNT_CONSONANTS = "count_consonants";
    private static final String COUNT_AGAINST = "count_against";
    private static final String COUNT_RESISTED = "count_resisted";
    private static final String COUNT_NOT_TAKE = "count_not_take";
    private static final String COUNT_ABSENT = "count_absent";
    private static final String TABLE_MEETINGS = "title_meetings";

    private Logger lgr = Logger.getLogger(VotingDate.class.getName());

    public void insert(Map<String, String> mapResults) {
        String query = "INSERT INTO title_meetings(id_date, name_title, result, count_consonants, count_against, count_resisted, count_not_take, count_absent) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = new PostgreSQLConnection().connection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, parsToInt(mapResults.get(CONST.VOTE_DATE)));
            pst.setString(2, mapResults.get(CONST.VOTE_NUMBER));
            pst.setString(3, mapResults.get(CONST.VOTE_RESULT));
            pst.setInt(4, parsToInt(mapResults.get(CONST.VOTE_POSITIVE)));
            pst.setInt(5, parsToInt(mapResults.get(CONST.VOTE_NEGATIVE)));
            pst.setInt(6, parsToInt(mapResults.get(CONST.VOTE_RELAX)));
            pst.setInt(7, parsToInt(mapResults.get(CONST.VOTE_NOT_TAKE)));
            pst.setInt(8, parsToInt(mapResults.get(CONST.VOTE_AWAY)));

            pst.executeUpdate();

        } catch (SQLException ex) {
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public int searchIdByTitle(String title) {
        String query = "SELECT id FROM title_meetings WHERE name_title = '" + title + "'";
        int id = 0;
        ArrayList<Integer> listId = new ArrayList<>();
        try (Connection con = new PostgreSQLConnection().connection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                listId.add(rs.getInt("id"));
                System.out.println(id);
            }

        } catch (SQLException ex) {
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        if (listId.isEmpty()) {
            lgr.log(Level.INFO, "List into "+TABLE_MEETINGS+" is empty");
            return -1;
        }
        if (listId.size() > 0) {
            lgr.log(Level.INFO, "List into "+TABLE_MEETINGS+" more that one write down");
        }

        return listId.get(0);
    }

    public List<TitleMeeting> selectAll() {
        String query = "SELECT * FROM " + TABLE_MEETINGS;

        ArrayList<TitleMeeting> list = new ArrayList<>();
        try (Connection con = new PostgreSQLConnection().connection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                TitleMeeting result = new TitleMeeting();
                result.setId(rs.getInt(ID));
                result.setIdDate(rs.getInt(ID_DATE));
                result.setNameTitle(rs.getString(NAME_TITLE));
                result.setResult(rs.getString(RESULT));
                result.setCountConsonants(rs.getInt(COUNT_CONSONANTS));
                result.setCountAgainst(rs.getInt(COUNT_AGAINST));
                result.setCountResisted(rs.getInt(COUNT_RESISTED));
                result.setCountNotTake(rs.getInt(COUNT_NOT_TAKE));
                result.setCountAbsent(rs.getInt(COUNT_ABSENT));
                list.add(result);
            }
        } catch (SQLException ex) {
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        if (list.isEmpty()) {
            lgr.log(Level.INFO, "List into " + TABLE_MEETINGS + " is empty");
        }

        return list;
    }

    private int parsToInt(String field) {
        int number = -1;
        try {
            number = Integer.parseInt(field);
        } catch (NumberFormatException ex) {
            Logger lgr = Logger.getLogger(VotingDate.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return number;
    }
}
