import Dao.DeputiesDao;
import Dao.ResultDao;
import Dao.TitleMeetingsDao;
import Model.Depute;
import Utility.CONST;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static Utility.CONST.*;

public class ParsingFile {
//    public static void main(String[] args) {
//        ParsingFile p = new ParsingFile();
//        p.start("C:\\Users\\Konfetka\\Downloads\\VotingBMR_Back-end nom\\Результат поіменного голосування_03.11.2016.pdf", 2);
//    }

    public void start(String nameFile, int idVotingDate) {
        DeputiesDao deputiesDao = new DeputiesDao();
        TitleMeetingsDao titleMeetingsDao = new TitleMeetingsDao();
        ResultDao resultDao = new ResultDao();

        List<Depute> listDep = deputiesDao.selectAll();

        File file = new File(nameFile);
        PDDocument document;
        try {

            document = PDDocument.load(file);
            int noOfPages = document.getNumberOfPages(); // count the size pages
            System.out.println("Pages detected: " + noOfPages);
            PDFTextStripper stripper = new PDFTextStripper();

            int pageCount = 1;
            for (int nextPage = 1; nextPage <= noOfPages; nextPage++) {
                stripper.setStartPage(nextPage);
                stripper.setEndPage(pageCount);
                System.out.println("Download... The page was uploading: " + nextPage);
                String textOnePage = stripper.getText(document);
//                System.out.println(textOnePage);
                if (!(textOnePage.contains(CONST.VOTE_END_TYPE))) {
                    while (!(textOnePage.contains(CONST.VOTE_END_TYPE))) {
                        stripper.setEndPage(++pageCount);
                        textOnePage = stripper.getText(document);
//                        System.out.println("Download... The page was uploading@1: " + nextPage);
                    }
                    textOnePage = stripper.getText(document);
                }


//---------------------------------------------------------------- search and found deputies
                Map<String, String> depAndAnswer = parsingPageDep(textOnePage);

                if (nextPage == 1) {
                    insertInDBDepIfNot(deputiesDao, listDep, depAndAnswer);
                }
//-----------------------------------------------------------------------------------------
//                System.out.println("Download... The page was uploading@2: " + nextPage);
//--------------------------------------------------------------------------------- insert and get id by page

                Map<String, String> everythingAboutTitle = parsingMeetings(textOnePage);
                everythingAboutTitle.put(VOTE_DATE, String.valueOf(idVotingDate));
                titleMeetingsDao.insert(everythingAboutTitle);
//                for (Map.Entry<String, String> map : everythingAboutTitle.entrySet()) {
//                    System.out.println(" >>>>>>>>>> " + map.getKey() + " " + map.getValue());
//                }

                int idTitle = titleMeetingsDao.searchIdByTitle(everythingAboutTitle.get(VOTE_NUMBER));

//-----------------------------------------------------------------------------------------

//--------------------------------------------------------------------------------- insert in bd result
                List<Depute> searchIdDep = deputiesDao.selectAll();
                for (Map.Entry<String, String> map : depAndAnswer.entrySet()) {
                    int idDep = 0;
                    for(Depute dep : searchIdDep){
                        if(dep.getName().equalsIgnoreCase(map.getKey())){
                            idDep = dep.getId();
                            break;
                        }
                    }
                    resultDao.insert(idTitle, idDep, map.getValue());
                }

//-----------------------------------------------------------------------------------------

                nextPage = pageCount;


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertInDBDepIfNot(DeputiesDao deputiesDao, List<Depute> listDep, Map<String, String> depAndAnswer) {
        if (listDep.isEmpty()) {
            deputiesDao.insert(depAndAnswer);
        }

        for (Map.Entry<String, String> map : depAndAnswer.entrySet()) {
            if (deputiesDao.searchIdByName(map.getKey()) == -1) {
                deputiesDao.insertOne(map.getKey());
            }
        }
    }

    private Map<String, String> parsingMeetings(String textOnePage) {
        Map<String, String> mapResults = new HashMap<>();

        mapResults.put(CONST.VOTE_POSITIVE, parseResults(textOnePage, CONST.VOTE_POSITIVE, CONST.VOTE_NEGATIVE, true));
        mapResults.put(CONST.VOTE_NEGATIVE, parseResults(textOnePage, CONST.VOTE_NEGATIVE, CONST.VOTE_RELAX, true));
        mapResults.put(CONST.VOTE_RELAX, parseResults(textOnePage, CONST.VOTE_RELAX, CONST.VOTE_NOT_TAKE, true));
        mapResults.put(CONST.VOTE_NOT_TAKE, parseResults(textOnePage, CONST.VOTE_NOT_TAKE, CONST.VOTE_AWAY, true));
        mapResults.put(CONST.VOTE_AWAY, parseResults(textOnePage, CONST.VOTE_AWAY, CONST.VOTE_RESULT, true));
        mapResults.put(CONST.VOTE_RESULT, parseResults(textOnePage, CONST.VOTE_RESULT, CONST.VOTE_END_TYPE, false).replace(" ", ""));
        mapResults.put(CONST.NAME_TITLE, parseResults(textOnePage, CONST.VOTE_TITLE, CONST.VOTE_NUMBER, false));
        mapResults.put(CONST.VOTE_NUMBER, parseResults(textOnePage, CONST.VOTE_NUMBER, CONST.VOTE_TITLE_END, false).replaceFirst(" ", ""));

        return mapResults;
    }

    public static String parseResults(String textOnePage, String startPosition, String endPosition, boolean number) {
        int startSearch = textOnePage.lastIndexOf(startPosition) + startPosition.length();
        int endSearch = textOnePage.lastIndexOf(endPosition);

        String searchString = "";

        if (startSearch == -1) {
            return searchString;
        }

        searchString = (endSearch == -1) ? textOnePage.substring(startSearch) : textOnePage.substring(startSearch, endSearch);
        if (number) {
            StringBuilder sb = new StringBuilder();
            for (char index : searchString.toCharArray()) {
                if (index >= 48 && index <= 57) {
                    sb.append(index);
                }
            }
            return sb.toString();
        }
        return searchString
                .replace("-", "")
                .replace("\"", "")
                .replace(":", "")
                .replace("\r\n", "");
    }

    //answer about everything depts
    private Map<String, String> parsingPageDep(String textOnePage) {
        int beginInFoundPage = textOnePage.lastIndexOf(SEARCH_START_AT_PAGE) + SEARCH_START_AT_PAGE.length();
        int endInFoundPage = textOnePage.lastIndexOf(SEARCH_END_AT_PAGE);

        String textBetweenBeginAndEnd = textOnePage.substring(beginInFoundPage, endInFoundPage);

        List<String> listOfFoundDeputy = new ArrayList<>();
        int numberOfDeputy = 1;
        int stepStart;
        int stepEnd;
        boolean isInterrupt = true;

        while (isInterrupt) {
            stepStart = textBetweenBeginAndEnd.indexOf(String.valueOf(numberOfDeputy++));
            stepEnd = textBetweenBeginAndEnd.indexOf(String.valueOf(numberOfDeputy));
            if (stepStart != 0 && stepEnd != 0 && stepEnd != -1) {
                listOfFoundDeputy.add(textBetweenBeginAndEnd.substring(stepStart, stepEnd));
                textBetweenBeginAndEnd = textBetweenBeginAndEnd.substring(stepEnd - 1);
            }
            if (stepEnd == -1) {
                isInterrupt = false;
                listOfFoundDeputy.add(textBetweenBeginAndEnd.substring(stepStart).trim());
            }
        }

        Map<String, String> mapOfDep = new HashMap<>();
        for (String deputy : listOfFoundDeputy) {
            String cleanDep = deputy.replace("\r\n", " ");
            String[] splitDepArray = cleanDep.split(" ");
            StringBuilder sb = new StringBuilder();
            if (splitDepArray.length > 3 && splitDepArray.length != 6) {
                for(int i = 1; i < splitDepArray.length-1; i++){
                    sb.append(splitDepArray[i]);
                }
                mapOfDep.put(sb.toString(), splitDepArray[splitDepArray.length-1]);
            } else {
                mapOfDep.put(splitDepArray[1], splitDepArray[2]);
            }
        }

        return mapOfDep;
    }


}
