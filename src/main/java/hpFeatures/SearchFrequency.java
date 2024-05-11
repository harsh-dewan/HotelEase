package hpFeatures;

import java.io.*;

import java.util.HashMap;

import java.util.Map;

import java.util.Scanner;
 
public class SearchFrequency {

    private static HashMap<String, Integer> numbersHashMap = new HashMap<>(); //used to store unique words found in the specified text files.

    private static HashMap<String, Integer> searchwordHashMap = new HashMap<>(); // used to store the search history(words entered by the user).
 
    private static final String DESTINATIONS_FILE_PATH = "Text_Files/destinations.txt";

    private static final String SEARCH_HISTORY_FILE_PATH = "Text_Files/search_history.txt";

    private static FileWriter searchFileWriter = null;
 
    public static void loadSearchHistory() {

    	File searchFile = new File(SEARCH_HISTORY_FILE_PATH);

    	BufferedReader readerTextFile = null;

		try {

			readerTextFile = new BufferedReader(new FileReader(searchFile));

			String line;

			while ((line = readerTextFile.readLine()) != null ) {

				//System.out.println("word read from search history: "+line.trim().toLowerCase());

				searchwordHashMap.put(line.trim().toLowerCase(), searchwordHashMap.getOrDefault(line.trim().toLowerCase(), 0)+1);

			}

		}

        catch (IOException e) {

			//e.printStackTrace();

		}

    	numbersHashMap.clear(); // Clear the numbers HashMap for each search
 
    	File destinationFile = new File(DESTINATIONS_FILE_PATH);

    	readerTextFile = null;

		try {

			readerTextFile = new BufferedReader(new FileReader(destinationFile));

			String line;

			while ((line = readerTextFile.readLine()) != null ) {

				//System.out.println("word read from destination: "+line.trim().toLowerCase());

				numbersHashMap.put(line.trim().toLowerCase(), 1);

			}

		}

        catch (IOException e) {

			//e.printStackTrace();

		}

    	return;

    }
 
    public static void saveSearchHistoryHoteLDetails() {

    	if( searchwordHashMap.isEmpty()) return ;

    	try {

    		searchFileWriter = new FileWriter(SEARCH_HISTORY_FILE_PATH);

        	for (Map.Entry<String,Integer> entryHP : searchwordHashMap.entrySet()) {  

//                //System.out.println("Key = " + entryHP.getKey() + 

//                                 ", Value = " + entryHP.getValue());

                searchFileWriter.write(entryHP.getKey()+"\n");

            } 

            searchFileWriter.close();

		} 

    	catch (IOException e) {

		}

    	return;

    }
 
    public static void SearchCountHotelDetails(String destination) throws Exception {
 
 
     // Checking Word in Web page words Dictionary (case-insensitive)

        if (numbersHashMap.containsKey(destination.toLowerCase())) {

            // Validation in the search History Dictionary

            if (searchwordHashMap.containsKey(destination.toLowerCase())) {

                int f = searchwordHashMap.get(destination.toLowerCase());

                System.out.println("The destination : '" + destination + "' is searched '" + f + "' times.");

            } else {

                System.out.println("The destination : '" + destination + "' is searched '0' time.");

            }

        } else {

            System.out.println("The given destination is not correct!");

        }
 
        // Save the updated search history

        saveSearchHistoryHoteLDetails();

    }
 
    public static void searchFrequencyMain() throws Exception {

        // Load existing search history

        loadSearchHistory();

        Scanner newScanner = new Scanner(System.in); // System.in is a standard input stream

        String c = "";

        while (!c.equals("exit")) {

            System.out.print("Enter your destination: (or type 'exit' to exit )");

            c = newScanner.nextLine();

            if( c.equalsIgnoreCase("exit")) return;

            SearchFrequency.SearchCountHotelDetails(c);

            c = "";

        }

    }

}