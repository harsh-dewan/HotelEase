package hpWebCrawling;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import hpConstants.HotelPriceAnalysisConstants;
 
 
public class HTMLParser {
	
	private HashSet<String> allLinksToParseSet = null;
	private FileWriter urlFileWriter=null, linkTextFileWriter = null;
	private String urlTextFilesFolder = HotelPriceAnalysisConstants.projectTextFilesFolder;
	private HashSet<String> stopWordsSet = null;
	private String allLinksTextFiles = HotelPriceAnalysisConstants.projectTextFilesFolder+"Links_and_TextFiles.txt";
	
	
	public HTMLParser() {}
	
	public HTMLParser(HashSet<String> links) {
		this.allLinksToParseSet = links;
		this.stopWordsSet = new HashSet<String>();
	}
	
	public void generateTextFromHTML() {
		
		if( allLinksToParseSet.isEmpty() ) {
			System.out.println("	Set containing Urls is empty, PLease try again");
			return;
		}
		int fileCounter = 1;
		Iterator<String> setIterator = allLinksToParseSet.iterator();
		String siteIdentifier="";
        try {
			linkTextFileWriter = new FileWriter(allLinksTextFiles);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		while( setIterator.hasNext() ) {
			try {
				String linkTaken = setIterator.next();
				if( linkTaken.contains(HotelPriceAnalysisConstants.wordBooking) ) siteIdentifier = HotelPriceAnalysisConstants.bookingWordUpperCase;
				else if( linkTaken.contains(HotelPriceAnalysisConstants.wordKayak) ) siteIdentifier = HotelPriceAnalysisConstants.kayakWordUpperCase;
				else siteIdentifier = HotelPriceAnalysisConstants.trivagoWordUpperCase;
		        String newFileName = urlTextFilesFolder + siteIdentifier + "_Text_File"+"_" + fileCounter + ".txt";
		        System.out.println("	Converting to text file for: " +linkTaken + " fileName created: "+newFileName);
		        Document document = Jsoup.connect(linkTaken).get();
		        if( document == null  ) {
		        	System.out.println("document is null");
		        }
		        urlFileWriter = new FileWriter(newFileName);
		        String textFileData = document.text();
		        urlFileWriter.write(textFileData);
		        urlFileWriter.close();
		        linkTextFileWriter.write(linkTaken + " " + newFileName + "\n");
		        System.out.println();
		        siteIdentifier="";
			}
			catch(Exception exception ) {
				System.out.println("Exception while converting htlm to text. "+exception.getMessage());
				exception.printStackTrace();
				
			}
			fileCounter++;
	    }
		try {
			linkTextFileWriter.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	//read the stop words from file and put in set
	public void readAllStopWords() {
		
		System.out.println("	PopulatingStop Words to Set!!\n");
		String stopWordsFileName = this.urlTextFilesFolder+HotelPriceAnalysisConstants.stopWordsFile;
		String line = "";
		int stopWordCount = 0;
		try {
			FileReader newFile = new FileReader(stopWordsFileName);
			BufferedReader bufferReaderFile = new BufferedReader(newFile);
			while ((line  = bufferReaderFile.readLine()) != null) {
				if( !stopWordsSet.contains(line))
				{
					 //System.out.println("stop word read from list: "+line.toLowerCase());
					 stopWordsSet.add(line.trim().toLowerCase());
					 stopWordCount+=1;
				}
		    }
			newFile.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		if( stopWordsSet.size() == 0 ) return;
		System.out.println("	Total stop words read: "+stopWordCount);
		return;
	}
	
	
	//convert all  the text files to word dictionary using pattern recognition using regular expression
	public void createLocalDictionary() {
		/*
		 * read all the text files
		 * filter the English words and exclude the stop words
		 * create anew file and write it in.
		 */
		System.out.println();
		System.out.println("	Creating Local Dictionary");
		File textFilesFolder = new File(urlTextFilesFolder);
		File[] listOfAllFiles = textFilesFolder.listFiles();
		String lineRead = "", wordRead = "";
		HashSet<String> alreadyIncludedWordSet = new HashSet<String>();
		ArrayList<String> listOfWords = new ArrayList<String>();
		String regexForEnglishWords = HotelPriceAnalysisConstants.regulaExpForEngWords;
	    Pattern patternHTMLParser = Pattern.compile(regexForEnglishWords);
	    Matcher matcherHTMLParser = null;
	    String dictionaryFileName = urlTextFilesFolder + HotelPriceAnalysisConstants.dictionaryFileName;
	    try {
			FileWriter wordsWriter  = new FileWriter(dictionaryFileName);
			for (int i = 0; i < listOfAllFiles.length; i++) {
				  if (listOfAllFiles[i].isFile() && listOfAllFiles[i].getName().contains("Text_File") ) 
				  {
					    System.out.println("		File read for creating dictionary: " + listOfAllFiles[i].getAbsolutePath());
					    ArrayList<String> wordList = new ArrayList<String>();
					    String filePath = listOfAllFiles[i].getAbsolutePath().toString();
			            BufferedReader readerTextFile = new BufferedReader(new FileReader(filePath));
			            String line;
			            while ((line = readerTextFile.readLine()) != null) {
			                String[] words = line.split("\\s+"); // Split the line into words
			                for (String word : words) wordList.add(word);
			            }
			            readerTextFile.close();
						if( wordList != null && wordList.size() > 0 )  {
						    for(int j = 0 ; j < wordList.size(); j++ ) 
						    {
						    	//fetch only English words
						    	wordRead = wordList.get(j);
						    	matcherHTMLParser = patternHTMLParser.matcher(wordRead);
						    	if( matcherHTMLParser.find() ) {
						    		wordRead = wordRead.trim().toLowerCase();
						    		if( !stopWordsSet.contains(wordRead) && 
						    			!alreadyIncludedWordSet.contains(wordRead)) 
						    		{
						    			listOfWords.add(wordRead);
						    			wordsWriter.write(wordRead+"\n");
						    			alreadyIncludedWordSet.add(wordRead);
						    		}
									else if(stopWordsSet.contains(wordRead)) 
									{
									  //System.out.println("Skipping sop word: "+wordRead); 
									}
						    	}
						    }							
						}
				  }
			}
			wordsWriter.close();
		}
	    catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	

}