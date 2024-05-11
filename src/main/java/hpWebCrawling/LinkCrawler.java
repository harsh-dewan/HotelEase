package hpWebCrawling;
 
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import hpConstants.HotelPriceAnalysisConstants;
 
		
		/*
		 * This class parses the website and fetch all the web links present on the site and store them in a set.
		 * The set of the urls to be parsed are passed to HTMLParser class.
		 */
public class LinkCrawler {
	
	private ArrayList<String> urlsToCrawl;
	private static HashSet<String> uniqueLinksSet = null; //for english language links
	private static HashSet<String> totalLinksSet = null;  // for all links
	private static int depthOfCrawling = HotelPriceAnalysisConstants.depthOfCrawling;
    private static String projectTextFilesFolder = HotelPriceAnalysisConstants.projectTextFilesFolder;
	private Connection connection = null;
	private FileWriter validUrlsFileWriter=null,urlJSONFileWriter=null;
	private static int totalReadLinks=0;
	private Gson gsonObj  = null;
	
 
	public LinkCrawler() {
		this.urlsToCrawl = new ArrayList<String>();
		this.uniqueLinksSet = new HashSet<String>();
		this.totalLinksSet = new HashSet<String>();
		gsonObj = new GsonBuilder().disableHtmlEscaping().create();
	}
	
	
	public void crawlWebsite(String url, int level, String websiteName) {
		
		if( level >= depthOfCrawling ) return;
		try {
			String language="", webpageTitle="";
			String titleRegExStr = "<title>([\\s\\S]*?)</title>";
    		String langRegExStr = "lang=\"([\\s\\S]*?)\"";
    		Pattern titlePattern = Pattern.compile(titleRegExStr);
			Pattern langpattern = Pattern.compile(langRegExStr);
			Matcher titleMatcher = null, langMatcher = null;
			connection = Jsoup.connect(url);
			String docString = "";
            Elements allLinks = connection.get().select(HotelPriceAnalysisConstants.anchorTagName);
            int count =0;
            System.out.println("		Total number of links fetched from the webpage: "+allLinks.size() + "\n");
            
            for (Element link : allLinks) {
            	
                String newLink = link.absUrl(HotelPriceAnalysisConstants.hrefTagName);
                //System.out.println("Count: " +count++);//  + " newLink: "+newLink);
                
                if( newLink.contains("#") ) newLink = newLink.replace(newLink.substring(newLink.indexOf("#")), "");
                if( newLink.startsWith(HotelPriceAnalysisConstants.httpString)  ) 
                {
                	newLink = newLink.split("\\?")[0];
                	Connection conn = Jsoup.connect(newLink).ignoreHttpErrors(true).ignoreContentType(true);
                	if( conn.execute().statusCode() != HotelPriceAnalysisConstants.httpStatusCode_OK ) continue;
                	docString = conn.get().toString();
                	titleMatcher =  titlePattern.matcher(docString);
        			langMatcher = langpattern.matcher(docString);
        			language="";webpageTitle="";
        			if( titleMatcher.find() && langMatcher.find() ) 
        			{
        				webpageTitle =  titleMatcher.group(1).trim();
        				language = langMatcher.group(1).trim();
        			}
        			else 
        			{
                    	language = conn.get().select(HotelPriceAnalysisConstants.htmlTagName).attr(HotelPriceAnalysisConstants.languageTagName);
                    	webpageTitle = conn.get().select(HotelPriceAnalysisConstants.titleTageName).text();        				
        			}
        			if( !totalLinksSet.contains(newLink)  ) {
        				URLDetail urlObject = new URLDetail(websiteName, language, newLink, webpageTitle);
            			//System.out.println("	gsonObject: "+urlObject.toString());
                        gsonObj.toJson(urlObject, urlJSONFileWriter);
                        urlJSONFileWriter.write("\n");
                        totalLinksSet.add(newLink);
        			}
                	if( (language.equalsIgnoreCase(HotelPriceAnalysisConstants.englishLangCodeCanada)  || 
                		 language.equalsIgnoreCase(HotelPriceAnalysisConstants.englishLangCodeUK) )    &&
                		!uniqueLinksSet.contains(newLink) ) 
                	{
                		    totalReadLinks+=1;
	                		uniqueLinksSet.add(newLink);
	                		validUrlsFileWriter.write(newLink+"\n");
	                		System.out.println("		Link Fetched from "+ websiteName + " : " + newLink);
	                		crawlWebsite(newLink, level+1,websiteName);
                	}
                	
                }
                
            }
		}
		catch(Exception e) {
			System.out.println("Exception while crawling: "+url);
			System.out.println("Exception message: "+e.getMessage());
			e.printStackTrace();
		}
		return;
	}
	
	
	public void crawlHotelSites()  {
		
		if( urlsToCrawl.size() == 0 ) {
			System.out.println("	List of Urls is Empty, exiting");
			return;
		}
		String urlName = "", validUrlsFileName = projectTextFilesFolder;
		String urlJSONFileName=projectTextFilesFolder+"Urls.json";
		try {
			urlJSONFileWriter = new FileWriter(urlJSONFileName);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		for(int index = 0; index < urlsToCrawl.size(); index++ ) {
			
			urlName =  urlsToCrawl.get(index);
			String websiteName = "";
			if( urlName.contains(HotelPriceAnalysisConstants.wordBooking) )  {
				validUrlsFileName = validUrlsFileName + HotelPriceAnalysisConstants.bookingTextFileName;
				websiteName = HotelPriceAnalysisConstants.wordBooking;
			}
			else if( urlName.contains(HotelPriceAnalysisConstants.wordKayak) ) {
				validUrlsFileName = validUrlsFileName + HotelPriceAnalysisConstants.kayakTextFileName;
				websiteName = HotelPriceAnalysisConstants.wordKayak;
			}
			else {
				validUrlsFileName = validUrlsFileName + HotelPriceAnalysisConstants.trivagoTextFileName;
				websiteName = HotelPriceAnalysisConstants.wordTrivago;
			}
			try {
				System.out.println();
				System.out.println("	Crawling the links present on the "+ websiteName + " page-\n");
				validUrlsFileWriter = new FileWriter(validUrlsFileName);
				crawlWebsite(urlName, 0, websiteName);
				validUrlsFileWriter.close();
				
				validUrlsFileName = projectTextFilesFolder;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			urlJSONFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("	Total Unique Links fetched: "+uniqueLinksSet.size());
		System.out.println();

		//program to traverse set and generate webpages text files
		HTMLParser htmlParser = new HTMLParser(uniqueLinksSet);
		htmlParser.generateTextFromHTML();
		htmlParser.readAllStopWords();
		htmlParser.createLocalDictionary();
		return;
	}
	
	
	public void startCrawling() {
		
		
		
		urlsToCrawl.add("https://www.trivago.ca/");
		urlsToCrawl.add("https://www.ca.kayak.com/stays");
		urlsToCrawl.add("https://www.booking.com/");
		System.out.println();
		System.out.println("	Program to parse websites and retreive data\n");
		System.out.println("	Program will crawl 3 websites: ");
		System.out.println("		1. Trivago:     https://www.trivago.ca/");
		System.out.println("		2. Kayak: 	https://www.ca.kayak.com/stays");
		System.out.println("		3. Booking.com: https://www.booking.com/\n");
		try {
			crawlHotelSites();
		} 
		catch (Exception e) {
			System.out.println("Error in Crwaling the sites: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println();
		System.out.println("	Program Completed!!");
		return;
	}
	
}