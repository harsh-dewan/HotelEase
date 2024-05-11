package hpFeatures;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hpConstants.HotelPriceAnalysisConstants;

public class PageRanking {

    private static String textFileFolderstg = "Text_Files\\";
    private static String allLinksTextFilestg = HotelPriceAnalysisConstants.projectTextFilesFolder+"Links_and_TextFiles.txt";
    private static HashMap<String, String> linkDatatg = null;

    private static final String PAGERANK_DESTINATION_PATTERNtg = "^[a-zA-Z\\s'-]+$";

    public static boolean validatePageRankInputtg(String userInput) {
        final Pattern patternInput = Pattern.compile(PAGERANK_DESTINATION_PATTERNtg);
        Matcher matcherInput = patternInput.matcher(userInput);
        return matcherInput.matches();
    }

    public static void pageRankMain() {

        try {
            linkDatatg = new HashMap<String, String>();
            File filetg=new File(allLinksTextFilestg);
            FileReader frtg=new FileReader(filetg);
            BufferedReader brtg=new BufferedReader(frtg);
            String linetg;
            while((linetg=brtg.readLine())!=null)
            {
                if( linetg != null && linetg.length() > 0 ) {
                    String[] linkarrtg = linetg.split(" ");
                    linkDatatg.put(linkarrtg[1], linkarrtg[0]);
                }
            }
            frtg.close();
        }
        catch(Exception extg) {
            //extg.printStackTrace();
        }
        if( linkDatatg.size() == 0 ) {
            System.out.println("No files found for page ranking!!");
            return;
        }
        String[] fileNamestg = new String[linkDatatg.size()];
        int index = 0;
        for (Map.Entry<String, String> set : linkDatatg.entrySet()) {
            fileNamestg[index++] = set.getKey();
        }
        boolean wordNotFoundtg = true;
        // created scanner obj for input
        Scanner sca = new Scanner(System.in);
        while( true ) {

            System.out.print("\n	Enter the word to rank pages (enter * to exit): ");
            String userInputtg = sca.nextLine();
            if (userInputtg.equals("*")) {
                System.out.println("	Exiting...");
                break;
            }
            if( validatePageRankInputtg(userInputtg) == false ) {
                System.out.println();
                System.out.println("	Invalid input, please enter english word only. ");
                continue;
            }
            String wordtg = userInputtg.toLowerCase();
            // fileNameMapStoreNameAsStringAndWordFrequencyInInteger
            HashMap<String, Integer> pagestg = new HashMap<>();
            int maxFreqtg = 0;
            try {
                // CountWordFrequencyInEachFileAndStoreInPagesTreeMap
                for (String filepathtg : fileNamestg) {
                    if( filepathtg != null && filepathtg.length() > 0 ) {
                        String fileName = getFileNameWithoutExtensiontg(filepathtg);
                        BufferedReader readertg = new BufferedReader(new FileReader(filepathtg));
                        String linetg="";
                        int frequencytg = 0;
                        while ((linetg= readertg.readLine()) != null) {
                            frequencytg += countWordFrequencytg(linetg.toLowerCase(), wordtg);
                        }
                        pagestg.put(fileName, frequencytg);
                        maxFreqtg = Integer.max(maxFreqtg, frequencytg);
                        readertg.close();

                    }
                }
                if( maxFreqtg == 0 ) {
                	System.out.println();
                    System.out.println("	Word not found on the pages. Please try again");

                }

                if( maxFreqtg != 0) {

                    // ConvertMapToListOfEntriesAndSortBasedOnFrequency
                    List<Map.Entry<String, Integer>> sortedPagestg = new ArrayList<>(pagestg.entrySet());
                    sortedPagestg.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

                    // DisplayResultsWithRanks

                    System.out.println("	Page Ranking based on word frequency:");

                    int ranktg = 1;
                    System.out.println();
                    for (Map.Entry<String, Integer> entrytg : sortedPagestg) {

                        if( entrytg.getValue() >= 1 ) {
                            System.out.println(
                                    "		Rank " + ranktg + ": " + linkDatatg.get("Text_Files/" + entrytg.getKey() + ".txt") + " - " + entrytg.getValue() + " occurrences");
                            ranktg++;
                        }
                    }
                }
            } catch (IOException etg) {
                //etg.printStackTrace();
            }

        }
    }

    // FunctionToCountWordFrequencyIn_a_Line
    private static int countWordFrequencytg(String linetg, String wordtg) {
        return linetg.split(wordtg, -1).length - 1;
    }

    // ThisFunctionExtractsThFileNameWithoutTheExtensionFromTheGiven
    // FilePath
    private static String getFileNameWithoutExtensiontg(String filePathtg) {
        if( filePathtg == null || filePathtg.length() == 0 ) {
            return filePathtg;
        }
        String fileNametg = new File(filePathtg).getName(); // GetFileNameWithExtension
        int dotIndex = fileNametg.lastIndexOf('.');
        if (dotIndex == -1) {
            return fileNametg; // NoExtensionFound
        } else {
            return fileNametg.substring(0, dotIndex); // RemoveExtension
        }
    }

}