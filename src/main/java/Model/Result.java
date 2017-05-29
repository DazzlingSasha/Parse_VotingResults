package Model;

public class Result {
    private int id;
    private int idTitle;
    private int idDeputy;
    private String resultAnswer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTitle() {
        return idTitle;
    }

    public void setIdTitle(int idTitle) {
        this.idTitle = idTitle;
    }

    public int getIdDeputy() {
        return idDeputy;
    }

    public void setIdDeputy(int idDeputy) {
        this.idDeputy = idDeputy;
    }

    public String getResultAnswer() {
        return resultAnswer;
    }

    public void setResultAnswer(String resultAnswer) {
        this.resultAnswer = resultAnswer;
    }

    @Override
    public String toString() {
        return "Result № " + id +
                "( idTitle: " + idTitle +
                ", idDeputy: " + idDeputy +
                ", resultAnswer: " + resultAnswer + " )";
    }
}
