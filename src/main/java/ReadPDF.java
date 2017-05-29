import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import Dao.VotingDate;
import Utility.CONST;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import static Utility.CONST.*;

public class ReadPDF extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        new StartGUI(primaryStage).start();
    }

    public static final String MESSAGE = "Specify the path to the folder. " +
            "Example: C:\\VotingBMR_Back-end nom";

    public static void main(String[] args) {

        List<String> allFiles = processFilesFromFolder(); // working search files into folder
        VotingDate votingDate = new VotingDate();
//        File file = new File("D:\\MyWorkOnJava\\Parse_VotingResults\\files for parsing\\Результат поіменного голосування_18.10.pdf");
        PDDocument document = null;
        try {
            for (String oneFile : allFiles) {
                System.out.println(oneFile);
                File file = new File(oneFile);
                document = PDDocument.load(file);

                int noOfPages = document.getNumberOfPages(); // count the size pages
                System.out.println("Pages detected: " + noOfPages);
                PDFTextStripper stripper = new PDFTextStripper();

                for (int nextPage = 0; nextPage < noOfPages; nextPage++) {
                    stripper.setStartPage(nextPage);
                    stripper.setEndPage(nextPage + 1);

                    String textOnePage = stripper.getText(document);

                    int beginInFoundPage = textOnePage.lastIndexOf(SEARCH_START_AT_PAGE) + SEARCH_START_AT_PAGE.length();


                    if (beginInFoundPage != -1) {
                        String dataDoc = getDataValue(textOnePage);
                        int id = votingDate.searchIdByName(dataDoc);

                        ParsingFile parsingFile = new ParsingFile();
                        String titleDoc = parsingFile.parseResults(textOnePage, CONST.VOTE_NUMBER, CONST.VOTE_TITLE_END, false).replaceFirst(" ", "");
                        System.out.println(id + " id " + titleDoc);
                        if (id == -1) {
                            votingDate.insert(dataDoc, titleDoc);
                            id = votingDate.searchIdByName(dataDoc);
                            parsingFile.start(oneFile, id);
                        } else {
                            break;
                        }
                    }
                }
            }
            launch(args);
            document.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
    private static String getDataValue(String textOnePage) {
        int startSearch = textOnePage.lastIndexOf(CONST.START_SEARCH_DATA) + CONST.START_SEARCH_DATA.length();
        int endSearch = textOnePage.lastIndexOf(CONST.VOTE_TITLE);
        String searchString = "";

        if (startSearch == -1) {
            return searchString;
        }

        searchString = (endSearch == -1) ? textOnePage.substring(startSearch) : textOnePage.substring(startSearch, endSearch);

        String searchDataString = searchString.replace("\r\n", "").replace(" ", "");

        StringBuilder builder = new StringBuilder();
        String dataResult;

        for (int i = 0; i < searchDataString.length(); i++) {
            builder.append(searchDataString.charAt(i));
            if (!(searchDataString.charAt(i) >= 48 && searchDataString.charAt(i) <= 57) && !(searchDataString.charAt(i) == 46)) {
                builder = new StringBuilder();
            }
        }

        if (builder.length() != 8) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
            Date date = null;

            try {
                date = dateFormat.parse(builder.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy");
            dataResult = simpleDateFormat.format(date);
        } else {
            dataResult = builder.toString();
        }
        return dataResult;
    }


    private static String ParseResults(String textOnePage, String startPosition, String endPosition) {
        int startSearch = textOnePage.lastIndexOf(startPosition) + startPosition.length();
        int endSearch = textOnePage.lastIndexOf(endPosition);
        String searchString = "";

        if (startSearch == -1) {
            return searchString;
        }

        searchString = (endSearch == -1) ? textOnePage.substring(startSearch) : textOnePage.substring(startSearch, endSearch);

        return searchString
                .replace("-", "")
                .replace("\"", "")
                .replace(":", "")
                .replace("\r\n", "");
    }

    public static List<String> processFilesFromFolder() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<String> allFiles = new ArrayList<>();
        boolean isInterrupt = true;
        String getAnswerUser;

        while (isInterrupt) {
            try {
                System.out.println(MESSAGE);
                getAnswerUser = br.readLine();
                File file = new File(getAnswerUser.replace("\"", "\\"));

                File[] folderEntries = file.listFiles();

                for (File entry : folderEntries) {
                    String onlyPDF = entry.toString();
                    if (onlyPDF.lastIndexOf(".pdf") > 0) {
                        allFiles.add(entry.toString());
                    }
                }

                if (allFiles.isEmpty()) {
                    System.out.println("There are no files with the extension .PDF in this folder!");
                } else {
                    br.close();
                    isInterrupt = false;
                }
            } catch (IOException | NullPointerException e) {
                System.out.println("You didn't correctly write down in console!");
            }
        }

        return allFiles;
    }


}
