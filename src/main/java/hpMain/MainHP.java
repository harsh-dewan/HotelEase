package hpMain;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hpFeatures.FrequencyCount;
import hpFeatures.InvertedIndexTrie;
import hpFeatures.PageRanking;
import hpFeatures.SearchFrequency;
import hpFeatures.SpellCheck;
import hpWebCrawling.LinkCrawler;
import hpWebScraping.Crawl;
import zmq.ZError.IOException;

public class MainHP {
	
	private static final String USER_INPUT_NUMBER_PATTERN = "^[0-6]+$";

	public static void main(String[] args)  {
		
		System.out.println("\nWelcome to HotelEase \n\nPlease select an option from the following: ");
		
		//Scanner userInput = null;
		int option, exit = 0;
		while(true) {
			System.out.println("\n1. Do you want to parse the websites?");
			System.out.println("2. Do you want to see the best deals?");
			System.out.println("3. Do you want to see the search history?");
			System.out.println("4. Do you want to see frequently occuring words along with where they are stored?");
			System.out.println("5. Page Ranking");
			System.out.println("6. Exit");
			Scanner userInput = new Scanner(System.in);
			String optionSelected = userInput.nextLine();
			if( validatingUserInput(optionSelected) == false ) {
				System.out.println();
				System.out.println("	You have entered wrong option, Please enter valid option");
				continue;
			}
			option = Integer.parseInt(optionSelected);
			switch(option) {
		
				case 1:
						LinkCrawler siteCrawler = new LinkCrawler();
						siteCrawler.startCrawling();	
						break;
					
				 case 2: 
					 	try { 
							Crawl.crawlMain(); 
						} 
						catch (Exception e) {}	
					 	break;
					
				 case 3:
					 	try {
					 		SearchFrequency.searchFrequencyMain();
					 	} catch (Exception e) {}
					 	break;
					 	
				 case 4:
					 	try {
					 		File freq = new File("Text_Files/Internal_Dictionary.txt");
					 		FrequencyCount.HomeEaseRBT_parseReader(freq);
					 		InvertedIndexTrie.main();
					 	} catch(Exception exception) {}
					 	break;
					 	
				 case 5:
					 	PageRanking.pageRankMain();
					 	break;
					 	
				 case 6:
					 	exit = 1;
					 	System.out.println("-----------------THANKS-----------------------");
					 	break;
					 	
				default:
					    System.out.println("	Please enter correct input again");
					    break;
			}
			if(exit == 1) {
				break;
			}
			//userInput.close();
		}
	}
	
    public static boolean validatingUserInput(String input ) {
        final Pattern patternUserInput = Pattern.compile(USER_INPUT_NUMBER_PATTERN);
        Matcher matcherUserInput = patternUserInput.matcher(input);
        if(matcherUserInput.matches() == false) return false;
        if( Long.parseLong(input) > 0 && Long.parseLong(input) <= 6 ) return true;
        return false;
    }
}
