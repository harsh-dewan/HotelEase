package hpFeatures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class TrieNodetg {
    TrieNodetg[] childrentg;
    boolean isEndOfWordtg;

    TrieNodetg() {
        childrentg = new TrieNodetg[26]; // AssumingEnglishAlphabets
        isEndOfWordtg = false; //IsNotTheEndOfWordByDefault WillBeChengedIfTheWordIsCompleted
    }
}

public class SpellCheck {

    private static TrieNodetg roottg = new TrieNodetg();

    public static void main(String[] args) {
        return;
    }

    public static List spellCheckMain(String wordtg) {

        loadWordsIntoTrietg("Text_Files/destinations.txt"); //TakingDestinationsFromTheFileInWhichDataIsStored
        String wordToChecktg;

        // StringUserInput;
        char choicetg;
        do {
            wordToChecktg = wordtg;
            choicetg = wordToChecktg.charAt(0);
            if (choicetg != '*') {
                choicetg = '*';

                boolean isWordInTrie = searchInTrietg(wordToChecktg); //CallingMethodHere
                if (isWordInTrie) {
                    //Systemoutprintln( wordToChecktg + "' isavalidword.");
                    return new ArrayList<String>();
                } else {
                    List<String> suggestionstg = suggestCorrectionstg(wordToChecktg);
                    if (suggestionstg.isEmpty()) {
                        System.out.println("\nNo suggestions found for '" + wordToChecktg + "'");
                    }
                    return suggestionstg;  //ReturningTheSuggestedWord
                }
            }

        } while (choicetg != '*'); //IfTheUserWantsToExit
        return new ArrayList<String>();

    }

    public static void loadWordsIntoTrietg(String filePathtg) {
        try (BufferedReader brtg = new BufferedReader(new FileReader(new File(filePathtg)))) {
            String line;
            while ((line = brtg.readLine()) != null) {
                String[] words = line.split("\\s+"); // SplitBySpaces
                for (String word : words) {
                    word = word.replaceAll("[^a-zA-Z]", "").toLowerCase().trim();
                    if (!word.isEmpty()) {
                        insertIntoTrietg(word);
                    }
                }
            }
        } catch (IOException etg) {
            //etg.printStackTrace();
        }
    }

    public static void insertIntoTrietg(String word) { //TheMethodToInsertDestinationNamesIntoTheTrieDataStructure
        TrieNodetg currenttg = roottg;
        for (char chtg : word.toCharArray()) {
            int indextg = chtg - 'a';
            if (currenttg.childrentg[indextg] == null) {
                currenttg.childrentg[indextg] = new TrieNodetg();
            }
            currenttg = currenttg.childrentg[indextg];
        }
        currenttg.isEndOfWordtg = true;
    }

    public static boolean searchInTrietg(String word) {
        TrieNodetg currenttg = roottg;
        for (char chtg : word.toCharArray()) {
            int indextg = chtg - 'a';
            if (currenttg.childrentg[indextg] == null) {
                return false; // WordNotFoundInTrieDataStructure
            }
            currenttg = currenttg.childrentg[indextg];
        }
        return currenttg.isEndOfWordtg; // CheckIfTheLastNodeMarksTheEndOf_a_ValidWord
    }

    public static List<String> suggestCorrectionstg(String wordtg) {
        List<String> suggestionstg = new ArrayList<>();
        suggestCorrectionsUtiltg(roottg, wordtg, new StringBuilder(), suggestionstg, 2); //ChangeTheMaximumDistanceAsAndIfNeeded
        return suggestionstg;
    }

    private static void suggestCorrectionsUtiltg(TrieNodetg nodetg, String wordtg, StringBuilder currentWordtg,
                                                 List<String> suggestionstg, int maxDistancetg) {

        if (currentWordtg.length() > wordtg.length() + maxDistancetg) {
            return;
        }

        if (nodetg.isEndOfWordtg && currentWordtg.toString().length() >= wordtg.length() - maxDistancetg
                && currentWordtg.toString().length() <= wordtg.length() + maxDistancetg ) {
            int disttg = calculateEditDistancetg(currentWordtg.toString(), wordtg);
            if (disttg <= maxDistancetg) {
                suggestionstg.add(currentWordtg.toString());
            }
        }

        for (int itg = 0; itg < 26; itg++) {
            if (nodetg.childrentg[itg] != null) {
                char chtg = (char) (itg + 'a');
                currentWordtg.append(chtg);
                suggestCorrectionsUtiltg(nodetg.childrentg[itg], wordtg, currentWordtg, suggestionstg, maxDistancetg);
                currentWordtg.setLength(currentWordtg.length() - 1);
            }
        }
    }

    private static int calculateEditDistancetg(String wordOne, String wordTwo) {
        int mtg = wordOne.length();
        int ntg = wordTwo.length();

        int[][] eddp = new int[mtg + 1][ntg + 1];

        for (int itg = 0; itg <= mtg; itg++) {
            for (int jtg = 0; jtg <= ntg; jtg++) {
                if (itg == 0) {
                    eddp[itg][jtg] = jtg;
                } else if (jtg == 0) {
                    eddp[itg][jtg] = itg;
                } else if (wordOne.charAt(itg - 1) == wordTwo.charAt(jtg - 1)) {
                    eddp[itg][jtg] = eddp[itg - 1][jtg - 1];
                } else {
                    eddp[itg][jtg] = 1 + Math.min(eddp[itg][jtg - 1], Math.min(eddp[itg - 1][jtg], eddp[itg - 1][jtg - 1]));
                }
            }
        }
        return eddp[mtg][ntg];
    }
}
//ThisCodeIsWrittenForCheckingIfTheWordThatIsEnteredByTheUserMatchesA