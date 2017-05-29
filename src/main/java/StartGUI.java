import Dao.DeputiesDao;
import Dao.ResultDao;
import Dao.TitleMeetingsDao;
import Model.Depute;
import Model.Result;
import Model.TitleMeeting;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartGUI {
    private TableView<Depute> table = new TableView<>();
    private Stage primaryStage;
    public StartGUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void start() throws Exception {

        primaryStage.setTitle("Voting results");
        BorderPane borderPane = new BorderPane();

        borderPane.setTop(getGridPaneTop());
        borderPane.setCenter(getGridPaneCenter());

        Scene scene = new Scene(borderPane, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Node getGridPaneTop() {
        GridPane gridTop = new GridPane();
        DeputiesDao deputies = new DeputiesDao();

        final Label label = new Label("Choose one of the deputies:");
        label.setFont(new Font("Arial", 20));
        label.setPadding(new Insets(0, 0, 10, 0));

        GridPane.setConstraints(label, 0, 0);

        ComboBox<Depute> comboBoxDep = new ComboBox<>();

        List<Depute> listDep = deputies.selectAll();
        Map<Integer, Integer> mapOwner = new HashMap<>();

        for (Depute dep : listDep) {
            comboBoxDep.getItems().add(dep);
            mapOwner.put(dep.getId(), 0);
        }
//        comboBoxDep.setValue(listDep.get(0));
        GridPane.setConstraints(comboBoxDep, 0, 1);

        Button button = new Button("Search");

        button.setOnAction(e -> {
            int idDep = comboBoxDep.getSelectionModel().getSelectedItem().getId();
            System.out.println(idDep + "ddddddd combo");
            getInfluenceDep(idDep, mapOwner);

            for (int i = 0; i < listDep.size(); i++) {
                listDep.get(i).setInfluence(mapOwner.get(listDep.get(i).getId()));
            }

            viewTableInfluenceDep(listDep);
        });

        GridPane.setConstraints(button, 1, 1);
        gridTop.getChildren().addAll(comboBoxDep, button, label);
        gridTop.setPadding(new Insets(10, 0, 0, 10));
        return gridTop;
    }



    private Node getGridPaneCenter() {
        GridPane grid = new GridPane();
        final Label label = new Label("List depute:");
        label.setFont(new Font("Arial", 20));
        label.setPadding(new Insets(10, 0, 0, 10));

        final VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);


        GridPane.setConstraints(label, 1, 15);
        GridPane.setConstraints(vbox, 1, 20);

        grid.getChildren().addAll(label, vbox);

        return grid;
    }

    private void getInfluenceDep(int idDep, Map<Integer, Integer> mapOwner) {
        ResultDao result = new ResultDao();
        TitleMeetingsDao meetingsDao = new TitleMeetingsDao();
        List<TitleMeeting> meetingList = meetingsDao.selectAll();

        for (int i = 0; i < meetingList.size(); i++) {
            int idTitle = meetingList.get(i).getId();

            String resultDep = result.foundResult(idTitle, idDep);
            List<Result> allResultOneTitle = result.selectAllByTitle(idTitle);

//            System.out.println("title: " + idTitle);
//            System.out.println("result: " + resultDep);
            for (int j = 0; j < allResultOneTitle.size(); j++) {

                if (resultDep.equalsIgnoreCase(allResultOneTitle.get(j).getResultAnswer()) && allResultOneTitle.get(j).getIdDeputy() != idDep) {
                    mapOwner.put(allResultOneTitle.get(j).getIdDeputy(), mapOwner.get(allResultOneTitle.get(j).getIdDeputy()) + 1);
                }
            }
        }
//        for (Map.Entry<Integer, Integer> map : mapOwner.entrySet()) {
//            System.out.println(map.getKey() + " " + map.getValue());
//        }
    }

    private void viewTableInfluenceDep(List<Depute> listDep) {
        TableColumn<Depute, Integer> serialNumCol = new TableColumn<>("№");
        TableColumn<Depute, String> nameDeputeCol = new TableColumn<>("Name depute");
        TableColumn<Depute, Integer> influenceCol = new TableColumn<>("Influence");

        final ObservableList<Depute> data = FXCollections.observableArrayList(listDep);
        serialNumCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        serialNumCol.setMinWidth(50);
        nameDeputeCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameDeputeCol.setMinWidth(300);
        influenceCol.setCellValueFactory(new PropertyValueFactory<>("influence"));
        influenceCol.setMinWidth(50);

        influenceCol.setSortType(TableColumn.SortType.DESCENDING);
        table.setItems(data);
        table.getColumns().addAll(serialNumCol, nameDeputeCol, influenceCol);
        table.getSortOrder().add(influenceCol);
    }

}

