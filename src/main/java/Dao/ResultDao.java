package Dao;

import Model.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResultDao {
    public static final String ID = "id";
    public static final String ID_TITLE = "id_title";
    public static final String ID_DEPUTY = "id_deputy";
    public static final String RESULT_ANSWER = "result_answer";
    public static final String TABLE_RESULT = "RESULT";

    private Logger lgr = Logger.getLogger(VotingDate.class.getName());

    public void insert(int idTitle, int idDep, String answer) {
        String query = "INSERT INTO "+TABLE_RESULT+" ("+ID_TITLE+", "+ID_DEPUTY+", "+RESULT_ANSWER+") VALUES (?, ?, ?)";

        try (Connection con = new PostgreSQLConnection().connection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, idTitle);
            pst.setInt(2, idDep);
            pst.setString(3, answer);
            pst.executeUpdate();

        } catch (SQLException ex) {
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public List<Result> selectAll() {
        String query = "SELECT * FROM "+TABLE_RESULT;

        ArrayList<Result> list = new ArrayList<>();
        try (Connection con = new PostgreSQLConnection().connection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query))
        {
            addToList(list, rs);
        } catch (SQLException ex) {
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        if (list.isEmpty()) {
            lgr.log(Level.INFO, "List into "+TABLE_RESULT+" is empty");
        }

        return list;
    }

    public List<Result> selectAllByTitle(int idTitle) {
        String query = "SELECT * FROM "+TABLE_RESULT+" WHERE "+ID_TITLE+ " = '"+idTitle+"'";

        ArrayList<Result> list = new ArrayList<>();
        try (Connection con = new PostgreSQLConnection().connection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query))
        {
            addToList(list, rs);
        } catch (SQLException ex) {
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        if (list.isEmpty()) {
            lgr.log(Level.INFO, "List into "+TABLE_RESULT+" is empty");
        }

        return list;
    }

    public String foundResult(int idTitle, int idDep) {
        String query = "SELECT * FROM "+TABLE_RESULT+" WHERE "+ID_TITLE+ " = "+idTitle+" AND " + ID_DEPUTY+ " = "+idDep;

        ArrayList<Result> list = new ArrayList<>();
        try (Connection con = new PostgreSQLConnection().connection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query))
        {
            addToList(list, rs);
        } catch (SQLException ex) {
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        if (list.isEmpty()) {
            lgr.log(Level.INFO, "List into "+TABLE_RESULT+" is empty");
        }
        if (list.size() > 0) {
            lgr.log(Level.INFO, "List into "+TABLE_RESULT+" more that one write down");
        }
        return list.get(0).getResultAnswer();
    }

    private void addToList(ArrayList<Result> list, ResultSet rs) throws SQLException {
        while (rs.next()) {
            Result result = new Result();
            result.setId(rs.getInt(ID));
            result.setIdTitle(rs.getInt(ID_TITLE));
            result.setIdDeputy(rs.getInt(ID_DEPUTY));
            result.setResultAnswer(rs.getString(RESULT_ANSWER));
            list.add(result);
        }
    }
}
