package Model;

public class TitleMeeting {
    private int id;
    private int idDate;
    private String nameTitle;
    private String result;
    private int countConsonants;
    private int countAgainst;
    private int countResisted;
    private int countNotTake;
    private int countAbsent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDate() {
        return idDate;
    }

    public void setIdDate(int idDate) {
        this.idDate = idDate;
    }

    public String getNameTitle() {
        return nameTitle;
    }

    public void setNameTitle(String nameTitle) {
        this.nameTitle = nameTitle;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCountConsonants() {
        return countConsonants;
    }

    public void setCountConsonants(int countConsonants) {
        this.countConsonants = countConsonants;
    }

    public int getCountAgainst() {
        return countAgainst;
    }

    public void setCountAgainst(int countAgainst) {
        this.countAgainst = countAgainst;
    }

    public int getCountResisted() {
        return countResisted;
    }

    public void setCountResisted(int countResisted) {
        this.countResisted = countResisted;
    }

    public int getCountNotTake() {
        return countNotTake;
    }

    public void setCountNotTake(int countNotTake) {
        this.countNotTake = countNotTake;
    }

    public int getCountAbsent() {
        return countAbsent;
    }

    public void setCountAbsent(int countAbsent) {
        this.countAbsent = countAbsent;
    }

    @Override
    public String toString() {
        return "TitleMeeting{" +
                "id=" + id +
                ", idDate=" + idDate +
                ", nameTitle='" + nameTitle + '\'' +
                ", result='" + result + '\'' +
                ", countConsonants=" + countConsonants +
                ", countAgainst=" + countAgainst +
                ", countResisted=" + countResisted +
                ", countNotTake=" + countNotTake +
                ", countAbsent=" + countAbsent +
                '}';
    }
}
