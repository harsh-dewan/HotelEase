package hpWebScraping;

import com.google.gson.Gson;

import hpFeatures.SearchFrequency;
import hpFeatures.SpellCheck;
import hpFeatures.WordCompletion;

import org.apache.poi.ss.formula.functions.Days;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import hpMain.HotelDetails;
import hpMain.HotelDetailsRating;

public class Crawl {
    //Creating a webdriver for chrome
    static WebDriver drv;
    static HashMap<String, Integer> month_days = new HashMap<>();
    static PriorityQueue<HotelDetails> costPQ = new PriorityQueue<HotelDetails>();
    static PriorityQueue<HotelDetailsRating> ratingPQ = new PriorityQueue<HotelDetailsRating>();
    
    private static final String DESTINATION_PATTERN = "^[a-zA-Z\\s'-]+$";
	private static final String DATE_PATTERN = "\\b(0?[1-9]|[12]\\d|3[01])\\s+(January|February|March|April|May|June|July|August|September|October|November|December|january|february|march|april|may|june|july|august|september|october|november|december)\\s+\\d{4}\\b";
	private static final String NUMBER_PATTERN = "^\\d+(\\.\\d+)?$"; // Integer or Decimal pattern
	 public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	static Scanner sc = new Scanner(System.in);
    public static DateTimeFormatter dt_formatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMMM yyyy")
            .toFormatter(Locale.ENGLISH);
    private static final String USER_INPUT_NUMBER_PATTERN = "^[0-6]+$";
    private static final String USER_INPUT_NUMBER_PATTERN_FOR_DESTINATION = "^[1-5]+$";


    public static boolean validatingPattern(String input, String currentPattern ) {
        final Pattern pattern = Pattern.compile(currentPattern);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
    
    public static boolean validatingDatePattern(String input, String currentPattern ) {
        final Pattern pattern = Pattern.compile(currentPattern);
        Matcher matcher = pattern.matcher(input);
        if(matcher.matches() == false)  return false;
        String[] tokens = input.split(" ");
        if( Integer.parseInt(tokens[0]) > 31 ) return false;
        if( tokens[1].equalsIgnoreCase("February") && Integer.parseInt(tokens[0]) > 29 ) return false;
        return true;
    }
    public static String UserInput_dataValidation( String instructionMessage, String patternType, String informationMessage) {
    	
    	String CurrentUserInput = null;
    	boolean userInputCorrect = false;
    	do {
        	System.out.println(instructionMessage);
        	try {
        		CurrentUserInput = sc.nextLine();
			} 
        	catch (Exception e) {
				e.printStackTrace();
			}
	        if ( CurrentUserInput != null && CurrentUserInput.length() > 0 && validatingPattern(CurrentUserInput,patternType)) {
	        	userInputCorrect = true;
	        }
	        else {
	        	userInputCorrect = false;
	        	System.out.println(ANSI_RED+ informationMessage +ANSI_RESET);
	        }
        } while ( CurrentUserInput == null || CurrentUserInput.isEmpty() || CurrentUserInput.length() == 0 || userInputCorrect == false );
    	
    	return CurrentUserInput;
    }
    
    public static long calculateDaysDifference(String checkInDate, String checkOutDate) {
        LocalDate date1_checkin = LocalDate.parse(checkInDate, dt_formatter);
        LocalDate date2_checkout = LocalDate.parse(checkOutDate, dt_formatter);
        // gives days count in difference
        long daysDifference_s = ChronoUnit.DAYS.between(date1_checkin, date2_checkout);
		return daysDifference_s;
        
    }
    static HashMap<String, Integer> month_priority = new HashMap<>();
    {
        month_days.put("january", 31);
        month_days.put("february", 28);
        month_days.put("march", 31);
        month_days.put("april", 30);
        month_days.put("may", 31);
        month_days.put("june", 30);
        month_days.put("july", 31);
        month_days.put("august", 31);
        month_days.put("september", 30);
        month_days.put("october", 31);
        month_days.put("november", 30);
        month_days.put("december", 31);
        month_priority.put("january", 1);
        month_priority.put("february", 2);
        month_priority.put("march", 3);
        month_priority.put("april", 4);
        month_priority.put("may", 5);
        month_priority.put("june", 6);
        month_priority.put("july", 7);
        month_priority.put("august", 8);
        month_priority.put("september", 9);
        month_priority.put("october", 10);
        month_priority.put("november", 11);
        month_priority.put("december", 12);
    }

    public static boolean exists(String xp) {
        try {
            drv.findElement(By.xpath(xp));
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    public static boolean stalewait(String stale) {
        try {
            Thread.sleep(1000);
            drv.findElement(By.xpath(stale));
        } catch (StaleElementReferenceException | InterruptedException e) {
            return true;
        }
        return false;
    }

    public static boolean is_date_valid(String from_d, String from_m, String from_y, String to_d, String to_m, String to_y){
        from_m = from_m.toLowerCase();
        to_m = to_m.toLowerCase();
        if(!from_y.equals(to_y)) return false;
        HashMap<String, Integer> month_days = new HashMap<>();
        HashMap<String, Integer> month_priority = new HashMap<>();
        month_days.put("january", 31);
        month_days.put("february", 28);
        month_days.put("march", 31);
        month_days.put("april", 30);
        month_days.put("may", 31);
        month_days.put("june", 30);
        month_days.put("july", 31);
        month_days.put("august", 31);
        month_days.put("september", 30);
        month_days.put("october", 31);
        month_days.put("november", 30);
        month_days.put("december", 31);
        month_priority.put("january", 1);
        month_priority.put("february", 2);
        month_priority.put("march", 3);
        month_priority.put("april", 4);
        month_priority.put("may", 5);
        month_priority.put("june", 6);
        month_priority.put("july", 7);
        month_priority.put("august", 8);
        month_priority.put("september", 9);
        month_priority.put("october", 10);
        month_priority.put("november", 11);
        month_priority.put("december", 12);

        int from = month_priority.get(from_m);
        int to = month_priority.get(to_m);

        if(to - from == 0) return true;
        else{
            int rem_from = month_days.get(from_m) - Integer.parseInt(from_d);
            int extra = 0;
            for(int i = from + 1; i < to; i++){
                for(Map.Entry<String, Integer> e: month_priority.entrySet()){
                    String month = e.getKey();
                    int priority = e.getValue();
                    if(priority == i){
                        extra += month_days.get(month);
                    }
                }
            }
            if(rem_from + Integer.parseInt(to_d) + extra > 90)  return false;
            return true;
        }
    }

    public static boolean trivago_date_helper(String d, String m, String y) throws InterruptedException {
        String m1 = "", m2 = "", y1 = "";
        List<WebElement> w = new ArrayList<>();
        m = m.toLowerCase();
        //handling invalid dates
        if(m.equals("february") && Integer.parseInt(d) > 29){
            System.out.println("Invalid date!");
            return false;
        }
        if(Integer.parseInt(d) > 31){
            System.out.println("Invalid date!");
            return false;
        }
        w = drv.findElements(By.xpath("//h3[@class='text-heading-s font-bold mx-3 pb-3 font-bold']"));
        String[] ss = w.get(0).getText().split(" ");
        m1 = ss[0].toLowerCase();
        y1 = ss[1];
        ss = w.get(1).getText().split(" ");
        m2 = ss[0].toLowerCase();
        while((!m1.equals(m) && !m2.equals(m)) || !y1.equals(y)) {
            if(exists("//button[@data-testid='calendar-button-next']")) {
                drv.findElement(By.xpath("//button[@data-testid='calendar-button-next']")).click();
            }
            else{
                System.out.println("Enter a recent date!");
                return false;
            }
            w = drv.findElements(By.xpath("//h3[@class='text-heading-s font-bold mx-3 pb-3 font-bold']"));
            ss = w.get(0).getText().split(" ");
            m1 = ss[0].toLowerCase();
            y1 = ss[1];
            ss = w.get(1).getText().split(" ");
            m2 = ss[0].toLowerCase();
        }
        if(d.charAt(0) == '0') {
            w = drv.findElements(By.xpath("//time[text()='"+d.charAt(1)+"']"));
        }
        else{
            w = drv.findElements(By.xpath("//time[text()='"+d+"']"));
        }

        //As there's 2 months visible simultaneously, we need to select the date from matching month
        //in case "to" date is 31st, w will have just one span element if the other month has <31 days
        if(w.size() == 1){
            w.get(0).click();
        }
        else {
            if (m1.equals(m)) {
                w.get(0).click();
            }
            if (m2.equals(m)) {
                w.get(1).click();
            }
        }
        return true;
    }

    public static boolean kayak_date_helper(String d, String m, String y) throws InterruptedException {
        String m1 = "", m2 = "", y1 = "";
        List<WebElement> w = new ArrayList<>();
        List<WebElement> w1 = new ArrayList<>();
        m = m.toLowerCase();
        //handling invalid dates
        if(m.equals("february") && Integer.parseInt(d) > 29){
            System.out.println("Invalid date!");
            return false;
        }
        if(Integer.parseInt(d) > 31){
            System.out.println("Invalid date!");
            return false;
        }
        w1 = drv.findElements(By.xpath("//div[@role='button' and text()="+d+"]"));
        w = drv.findElements(By.xpath("//caption[@class='w0lb w0lb-month-name']"));

        String[] ss = w.get(0).getText().split(" ");
        m1 = ss[0].toLowerCase();
        y1 = ss[1];
        ss = w.get(1).getText().split(" ");
        m2 = ss[0].toLowerCase();
        while((!m1.equals(m) && !m2.equals(m)) || !y1.equals(y)) {
            if(exists("//div[@role='button' and @aria-label='Next month']")) {
                drv.findElement(By.xpath("//div[@role='button' and @aria-label='Next month']")).click();
            }
            else{
                System.out.println("Enter a recent date!");
                return false;
            }
            w = drv.findElements(By.xpath("//caption[@class='w0lb w0lb-month-name']"));
            ss = w.get(0).getText().split(" ");
            m1 = ss[0].toLowerCase();
            y1 = ss[1];
            ss = w.get(1).getText().split(" ");
            m2 = ss[0].toLowerCase();
        }
        if(d.charAt(0) == '0') {
            w = drv.findElements(By.xpath("//div[@role='button' and text()="+d.charAt(1)+"]"));
        }
        else{
            w = drv.findElements(By.xpath("//div[@role='button' and text()="+d+"]"));
        }

        //As there's 2 months visible simultaneously, we need to select the date from matching month
        //in case "to" date is 31st, w will have just one span element if the other month has <31 days
        if(w.size() == 1){
            w.get(0).click();
        }
        else {
            if (m1.equals(m)) {
                w.get(0).click();
            }
            if (m2.equals(m)) {
                w.get(1).click();
            }
        }
        return true;
    }

    public static boolean booking_date_helper(String d, String m, String y) throws InterruptedException {
        String m1 = "", m2 = "", y1 = "";
        List<WebElement> w = new ArrayList<>();
        m = m.toLowerCase();
        //handling invalid dates
        if(m.equals("february") && Integer.parseInt(d) > 29){
            System.out.println("Invalid date!");
            return false;
        }
        if(Integer.parseInt(d) > 31){
            System.out.println("Invalid date!");
            return false;
        }
        w = drv.findElements(By.xpath("//h3[@class='e1eebb6a1e ee7ec6b631']"));
        String[] ss = w.get(0).getText().split(" ");
        m1 = ss[0].toLowerCase();
        y1 = ss[1];
        ss = w.get(1).getText().split(" ");
        m2 = ss[0].toLowerCase();
        while((!m1.equals(m) && !m2.equals(m)) || !y1.equals(y)) {
            if(exists("//button[@class='a83ed08757 c21c56c305 f38b6daa18 d691166b09 f671049264 deab83296e f4552b6561 dc72a8413c f073249358']")) {
                drv.findElement(By.xpath("//button[@class='a83ed08757 c21c56c305 f38b6daa18 d691166b09 f671049264 deab83296e f4552b6561 dc72a8413c f073249358']")).click();
            }
            else{
                System.out.println("Enter a recent date!");
                return false;
            }
            w = drv.findElements(By.xpath("//h3[@class='e1eebb6a1e ee7ec6b631']"));
            ss = w.get(0).getText().split(" ");
            m1 = ss[0].toLowerCase();
            y1 = ss[1];
            ss = w.get(1).getText().split(" ");
            m2 = ss[0].toLowerCase();
        }
        if(d.charAt(0) == '0') {
            w = drv.findElements(By.xpath("//span[text()='"+d.charAt(1)+"']"));
        }
        else{
            w = drv.findElements(By.xpath("//span[text()='"+d+"']"));
        }

        //As there's 2 months visible simultaneously, we need to select the date from matching month
        //in case "to" date is 31st, w will have just one span element if the other month has <31 days
        if(w.size() == 1){
            w.get(0).click();
        }
        else {
            if (m1.equals(m)) {
                w.get(0).click();
            }
            if (m2.equals(m)) {
                w.get(1).click();
            }
        }
        return true;
    }

    public static int trivago_crawl(String destination, String cD, String cM, String cY, String coD, String coM, String coY, int adults, int rooms, int children, ArrayList<Integer> childrenAge, int entryCounter) throws InterruptedException, IOException {
        drv = new ChromeDriver();
        drv.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        drv.get("https://www.trivago.com");
        drv.manage().window().maximize();
        Thread.sleep(1000);
        drv.findElement(By.xpath("//a[@href='/en-US']")).click();
        Thread.sleep(1000);
        drv.findElement(By.xpath("//input[@data-testid='search-form-destination']")).sendKeys(destination);
        Thread.sleep(1000);
        drv.findElement(By.xpath("//input[@data-testid='search-form-destination']")).sendKeys(Keys.ENTER);
        boolean var;
        //from
        var = trivago_date_helper(cD, cM, cY);
        if(!var) return 0;
        //to
        var = trivago_date_helper(coD, coM, coY);
        if(!var) return 0;

        drv.findElement(By.xpath("//input[@data-testid='adults-amount']")).sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Integer.toString(adults));
        drv.findElement(By.xpath("//input[@data-testid='children-amount']")).sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Integer.toString(children));
        drv.findElement(By.xpath("//input[@data-testid='rooms-amount']")).sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Integer.toString(rooms));
        for(int i = 0; i < children; i++) {
            if(!exists("(//select[@class='appearance-none h-10 border pl-4 pr-10 bg-no-repeat bg-right rounded-sm border-grey-300 bg-white'])["+ (i + 1) +"]")) drv.findElement(By.xpath("//button[@data-testid='search-form-guest-selector']")).click();
            WebElement child_age_dropdown = drv.findElement(By.xpath("(//select[@class='appearance-none h-10 border pl-4 pr-10 bg-no-repeat bg-right rounded-sm border-grey-300 bg-white'])[" + (i + 1) + "]"));
            Select child_age_selector = new Select(child_age_dropdown);
            child_age_selector.selectByIndex(childrenAge.get(i));
        }

        JavascriptExecutor js = (JavascriptExecutor) drv;
        js.executeScript("window.scrollTo(0, 0)");

        drv.findElement(By.xpath("//button[@data-testid='guest-selector-apply']")).click();

        //drv.findElement(By.xpath("//button[@data-testid='search-button']")).click();

        Thread.sleep(10000);
        while(stalewait("//button[@data-testid='item-name']"));

        List<WebElement> title = drv.findElements(By.xpath("//button[@data-testid='item-name']"));
        List<WebElement> distance = drv.findElements(By.xpath("//button[@data-testid='distance-label-section']"));
        List<WebElement> price = drv.findElements(By.xpath("//p[@itemprop='price']"));
        List<WebElement> rating = drv.findElements(By.xpath("//span[@itemprop='ratingValue']"));

        ArrayList<HotelDetails> hD = new ArrayList<>();

        for(int i=0; i<(title.size()/2) - 1; i++){
            HotelDetails h = new HotelDetails();
            h.hotelName = title.get(i).getText();
            h.hotelRating = rating.get(i).getText();
            h.cost = price.get(i).getText();
            String newCost = returnCostString(h.cost);
            h.cost = newCost;
            costPQ.add(h);
            hD.add(i, h);
            ratingPQ.add(new HotelDetailsRating(h));
        }
        
        int stop = hD.size() - 1;
        HotelDetails stophere = hD.get(stop);

        FileWriter f = new FileWriter("json_output/output_trivago.json");

        f.append("{\n");
        for(HotelDetails h: hD){
            f.append(" \"Hotel " + entryCounter++ + "\" : ");
            Gson g = new Gson();
            String json = g.toJson(h);
            f.append(json);
            if(h != stophere) f.append(",");
            f.write("\n");
        }
        f.append("}");
        f.close();
        
        drv.quit();
        
        return entryCounter;
    }

    public static int kayak_crawl(String destination, String cD, String cM, String cY, String coD, String coM, String coY, int adults, int rooms, int children, ArrayList<Integer> childrenAge, int entryCounter) throws InterruptedException, IOException {
        drv = new ChromeDriver();
        drv.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        drv.get("https://www.kayak.com");
        drv.manage().window().maximize();
        drv.findElement(By.xpath("//a[@href='/stays']")).click();
        drv.findElement(By.xpath("//input[@type='text' and @placeholder='Enter a city, hotel, airport, or landmark']")).sendKeys(destination);
        Thread.sleep(1000);
        drv.findElement(By.xpath("//input[@type='text' and @placeholder='Enter a city, hotel, airport, or landmark']")).sendKeys(Keys.ENTER);
        JavascriptExecutor js = (JavascriptExecutor) drv;
        js.executeScript("window.scrollTo(0, 0)");
        drv.findElement(By.xpath("//div[@role='button' and @aria-label='Start date']")).click();

        boolean var;
        //from
        var = kayak_date_helper(cD, cM, cY);
        if(!var) return 0;
        //to
        var = kayak_date_helper(coD, coM, coY);
        if(!var) return 0;

        drv.findElement(By.xpath("//button[@aria-label='Search']")).click();
        Thread.sleep(5000);
        ArrayList<String> handles = new ArrayList(drv.getWindowHandles());
        drv.switchTo().window(handles.get(1));
        drv.findElement(By.xpath("//div[@class='NITa NITa-roomsGuests NITa-hasValue NITa-mod-presentation-expanded']")).click();

        //room increment
        WebElement r = drv.findElement(By.xpath("(//button[@class='T_3c-button'])[2]"));
        for(int i=0; i<rooms-1; i++)  r.click();

        //adult decrement
        WebElement ad = drv.findElement(By.xpath("(//button[@class='T_3c-button'])[3]"));
        //adult increment
        WebElement ai = drv.findElement(By.xpath("(//button[@class='T_3c-button'])[4]"));
        if(adults == 1) ad.click();
        else if(adults == 2){}
        else{
            for(int i=0; i<adults-rooms; i++) ai.click();
        }

        //child increment
        WebElement c = drv.findElement(By.xpath("(//button[@class='T_3c-button'])[6]"));
        for(int i=0; i<children; i++)   c.click();

        for(int i=1; i<=children; i++){
            drv.findElement(By.xpath("(//div[@class='c9jHv-dropdownContainer'])["+i+"]")).click();
            //drv.findElement(By.xpath("(//div[@role='combobox'])["+i+"]")).click();
            Thread.sleep(1000);
            drv.findElement(By.xpath("(//li[@role='option'])["+(childrenAge.get(i-1)+1)+"]")).click();
        }

        drv.findElement(By.xpath("//span[text()='Update']")).click();

        drv.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        List<WebElement> title = drv.findElements(By.xpath("//div[@class='FLpo-hotel-name']"));
        List<WebElement> rating = drv.findElements(By.xpath("//div[@class = 'FLpo-score' or @class='FLpo-score FLpo-positive']"));
        List<WebElement> price = drv.findElements(By.xpath("//div[@data-target='price']"));

        ArrayList<HotelDetails> hD = new ArrayList<>();

        for(int i=0; i<(title.size()/2) - 1; i++){
            HotelDetails h = new HotelDetails();
            h.hotelName = title.get(i).getText();
            h.hotelRating = rating.get(i).getText();
            h.cost = price.get(i).getText();
            String newCost = returnCostString(h.cost);
            h.cost = newCost;
            costPQ.add(h);
            hD.add(i, h);
            ratingPQ.add(new HotelDetailsRating(h));
        }

        int stop = hD.size() - 1;
        HotelDetails stophere = hD.get(stop);

        FileWriter f = new FileWriter("json_output/output_kayak.json");

        f.append("{\n");
        for(HotelDetails h: hD){
            f.append(" \"Hotel " + entryCounter++ + "\" : ");
            Gson g = new Gson();
            String json = g.toJson(h);
            f.append(json);
            if(h != stophere) f.append(",");
            f.write("\n");
        }
        f.append("}");
        f.close();
        
        drv.quit();
        
        return entryCounter;
    }
    
    public static String returnCostString(String costString) {
    	String newString = "";
    	for(int i = 0 ;i  < costString.length(); i++ ) {
    		char ch = costString.charAt(i);
    		if( ( ch >= '0' && ch <= '9') || ch == '.' ) {
    			newString = newString + ch;
    		}
    	}
    	if( newString == "" ) newString = "0";
    	//System.out.println("newCost returned is: "+newString);
    	return newString;
    }

    public static int booking_crawl(String destination, String cD, String cM, String cY, String coD, String coM, String coY, int adults, int rooms, int children, ArrayList<Integer> childrenAge) throws InterruptedException, IOException {
        drv = new ChromeDriver();
        drv.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        drv.get("https://www.booking.com");
        drv.manage().window().maximize();
        if(exists("//button[@aria-label='Dismiss sign-in info.']")) {
            drv.findElement(By.xpath("//button[@aria-label='Dismiss sign-in info.']")).click();
        }
        drv.findElement(By.xpath("//input[@name='ss']")).sendKeys(destination);
        drv.findElement(By.xpath("//button[@data-testid='date-display-field-start']")).click();

        boolean var;
        //from
        var = booking_date_helper(cD, cM, cY);
        if(!var) return 0;
        //to
        var = booking_date_helper(coD, coM, coY);
        if(!var) return 0;

        drv.findElement(By.xpath("//button[@data-testid='occupancy-config']")).click();
        //adult increment
        WebElement adultIncrement = drv.findElement(By.xpath("/html/body/div[3]/div[2]/div/form/div[1]/div[3]/div/div/div/div/div[1]/div[2]/button[2]"));
        //adult decrement
        WebElement adultDecrement = drv.findElement(By.xpath("/html/body/div[3]/div[2]/div/form/div[1]/div[3]/div/div/div/div/div[1]/div[2]/button[1]"));
        if(adults == 1) adultDecrement.click();
        else if(adults == 2){}
        else {
            for (int i = 0; i < adults - 2; i++) adultIncrement.click();
        }
        //child increment
        WebElement childIncrement = drv.findElement(By.xpath("/html/body/div[3]/div[2]/div/form/div[1]/div[3]/div/div/div/div/div[2]/div[2]/button[2]"));
        if(children > 0) {
            if(children == 1){
                childIncrement.click();
                WebElement child_age_dropdown = drv.findElement(By.xpath("//select[@name='age']"));
                Select child_age_selector = new Select(child_age_dropdown);
                child_age_selector.selectByIndex(childrenAge.get(0) + 1);
            }
            else {
                for (int i = 0; i < children; i++) childIncrement.click();
                for (int i = 0; i < children; i++) {
                    int j = i + 1;
                    if(!exists("(//select[@class='ebf4591c8e'])["+j+"]")) drv.findElement(By.xpath("//button[@data-testid='occupancy-config']")).click();
                    WebElement child_age_dropdown = drv.findElement(By.xpath("(//select[@class='ebf4591c8e'])["+j+"]"));
                    Select child_age_selector = new Select(child_age_dropdown);
                    child_age_selector.selectByIndex(childrenAge.get(i) + 1);
                }
            }
        }
        //room increment
        if(children == 0){
            WebElement roomIncrement = drv.findElement(By.xpath("(//button[@class='a83ed08757 c21c56c305 f38b6daa18 d691166b09 ab98298258 deab83296e bb803d8689 f4d78af12a'])[3]"));
            if (rooms > 1) {
                for (int i = 0; i < rooms-1; i++) roomIncrement.click();
            }
        }
        else {
            if (!exists("/html/body/div[3]/div[2]/div/form/div[1]/div[3]/div/div/div/div/div[5]/div[2]/button[2]"))
                drv.findElement(By.xpath("//button[@data-testid='occupancy-config']")).click();
            WebElement roomIncrement = drv.findElement(By.xpath("/html/body/div[3]/div[2]/div/form/div[1]/div[3]/div/div/div/div/div[5]/div[2]/button[2]"));
            if (rooms > 1) {
                for (int i = 0; i < rooms-1; i++) roomIncrement.click();
            }
        }
        drv.findElement(By.xpath("//button[@type='submit']")).click();

//        if(exists("//button[@aria-label='Dismiss sign-in info.']")) {
//            drv.findElement(By.xpath("//button[@aria-label='Dismiss sign-in info.']")).click();
//        }
        
        while(stalewait("//div[@data-testid='title']"));
        
//        if(exists("//button[@aria-label='Dismiss sign-in info.']")) {
//            drv.findElement(By.xpath("//button[@aria-label='Dismiss sign-in info.']")).click();
//        }

        List<WebElement> title = drv.findElements(By.xpath("//div[@data-testid='title']"));
        List<WebElement> address = drv.findElements(By.xpath("//span[@data-testid='address']"));
        List<WebElement> distance = drv.findElements(By.xpath("//span[@data-testid='distance']"));
        List<WebElement> price = drv.findElements(By.xpath("//span[@data-testid='price-and-discounted-price']"));

        ArrayList<HotelDetails> hD = new ArrayList<>();

        for(int i=0; i<title.size()-1; i++){
            HotelDetails h = new HotelDetails();
            h.hotelName = title.get(i).getText();
            h.hotelLocation = address.get(i).getText();
//            String s = price.get(i).getText();
//            String temp = null;
//            Pattern p = Pattern.compile("\"cost\":\"\\w+\"");
//            Matcher m = p.matcher(s);
//            if(m.find()) {
//                temp = m.group();
//                temp = temp.replaceAll("\"cost\":\"", "");
//                temp = temp.replaceAll("\"", "");
//            }
            h.cost = price.get(i).getText();
            String newCost = returnCostString(h.cost);
            hD.add(i, h);
            h.cost = newCost;
            costPQ.add(h);
        }

        int entryCounter = 1;

        int stop = hD.size() - 1;
        HotelDetails stophere = hD.get(stop);

        FileWriter f = new FileWriter("json_output/output_booking.json");

        f.append("{\n");
        for(HotelDetails h: hD){
            f.append(" \"Hotel " + entryCounter++ + "\" : ");
            Gson g = new Gson();
            String json = g.toJson(h);
            f.append(json);
            if(h != stophere) f.append(",");
            f.write("\n");
        }
        f.append("}");
        f.close();

        drv.quit();
        
        return entryCounter;        
    }
   

    public static void crawlMain() throws Exception {
    	System.out.println("\nWelcome! This tool will help you get best deal on Hotels!");
		System.out.println("---------------------------------------------------------\n");

    	String destination = UserInput_dataValidation("Enter your destination: ",DESTINATION_PATTERN,"Invalid characters found in the destination.");
    	List<String> suggestions = WordCompletion.wordCompletion(destination.toLowerCase());
    	List<String> spellchecksuggestions = null;
    	if( suggestions.size() == 0 ) {
    		System.out.println("\nNo Suggestions from word completion, checking with spell check");
    		spellchecksuggestions = SpellCheck.spellCheckMain(destination.toLowerCase());
    		suggestions.addAll(spellchecksuggestions);
    	}
    	int max = 5;
    	if(suggestions.size() > 0) {
    		if( suggestions.size() >= max ) {
        		max = 5;
        	}
        	else {
        		max = suggestions.size();
        	}
    		System.out.println("\nBased on your destination, there are some suggestions: ");
    		int x = 0;
    		for(int i = 0; i < suggestions.size(); i++) {
    			if(x < max) {
    				System.out.println(i+1 + ": " + suggestions.get(i));
    			}
    			else {
    				break;
    			}
    			x++;
    		}
    		while(true) {
    			System.out.println("\nPlease choose the correct option for the destination to be selected: ");
    			Scanner usr_inp = new Scanner(System.in);
    			String optionFromUser = usr_inp.nextLine();
    			if( optionFromUser.equalsIgnoreCase("exit") ) break;
    			if( validatingDestinationInput(optionFromUser, max) == false ) continue;
        		int choice = Integer.parseInt(optionFromUser);
				destination = suggestions.get(choice - 1);
				System.out.println("\nYou have selected: " + destination+"\n");
				break;
    		}
    	}
    	else {
    		System.out.println("\nYour destination is: " + destination+"\n");
    	}
    	
		try {
    		BufferedWriter search_hist = new BufferedWriter(new FileWriter("Text_Files/search_history.txt", true));
    		search_hist.write(destination + "\n");
    		search_hist.close();
    	} catch(IOException e) {
    		//e.printStackTrace();
    	}
		
    	String checkin = null ;
    	String checkout = null;
	    String rooms = null;
        String children = null;
    	String childAge = null;
    	LocalDate currentDate = LocalDate.now();
        boolean checkInDateCorrect = false;
        boolean checkOutDateCorrect = false;
        boolean adultsInputCorrect = false;
        boolean childrenInputCorrect = false;
        boolean roomsInputCorrect = false;
        boolean childAgeInputCorrect = false;
        
        // enter checkIn date, checking date format and should be of today/future date
    	do {
        	System.out.println("Enter your check-in date within 90 days of current date(format accepted : 03 December 2023 or 3 December 2023): ");
        	try {
        		checkin = sc.nextLine();
			} catch (Exception e) {
				//e.printStackTrace();
			}
        	if ( checkin != null && checkin.length() > 0 && validatingDatePattern(checkin,DATE_PATTERN) && 
        		 ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(checkin, dt_formatter)) >= 0 && 
        		 ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(checkin, dt_formatter)) <= 90 ) 
        	{
        		checkInDateCorrect = true;  
        	}
        	else 
        	{
            	checkInDateCorrect = false;
            	System.out.println(ANSI_RED+ "Invalid date, Enter date format dd-MMMM-yyyy or today/future date." +ANSI_RESET); 
            }
        } while ( checkin == null || checkin.isEmpty() ||  checkin.length() == 0 || checkInDateCorrect == false);
    	
    	
    	// enter checkOut date, checking date format and should be of today/future date and days<90
    	do {
        	System.out.println("Enter your check-out date within 90 days of the check-in date (format accepted: 03 December 2023 or 3 December 2023): ");
        	try {
        		checkout = sc.nextLine();
			} catch (Exception e) {
				//e.printStackTrace();
			}
        	if ( checkout != null && checkout.length() > 0 && 
        		 validatingDatePattern(checkout,DATE_PATTERN) &&
        		 ChronoUnit.DAYS.between(LocalDate.parse(checkin, dt_formatter), LocalDate.parse(checkout, dt_formatter)) >= 0 && 
        		 ChronoUnit.DAYS.between(LocalDate.parse(checkin, dt_formatter), LocalDate.parse(checkout, dt_formatter)) <= 90  ) {
        		
        		 checkOutDateCorrect = true;
        	}
        	else {
        		checkOutDateCorrect = false;
            	System.out.println(ANSI_RED+ "Invalid date, Enter date format dd-MMMM-yyyy or today/future date." +ANSI_RESET); 
            }
        } while (checkout == null || checkout.isEmpty()  || checkout.length() == 0 || checkOutDateCorrect == false);
    	
    	
    	// adults count input 
    	String adults = null;
    	do {
        	System.out.println("Enter number of adult occupants, max adults allowed 6: ");
        	
        	try {
        		adults = sc.nextLine();
			} 
        	catch (Exception e) {
				//e.printStackTrace();
			}
            if ( adults != null && adults.length() > 0 && 
            	validatingPattern(adults,NUMBER_PATTERN) && 
            	Long.parseLong(adults) <= 6 && 
            	Long.parseLong(adults) > 0) {              	
            	adultsInputCorrect = true;
            }
            else {
            	adultsInputCorrect = false;
            	System.out.println(ANSI_RED+ "Invalid value. Enter positive integer between 1 and 6." +ANSI_RESET); 
            }
        } while ( adults == null || adults.length() == 0 || adults.isEmpty() || adultsInputCorrect == false);
    	
    	
    	long childrenCount = 0;
        // enter children
        do {
        	 System.out.println("Enter the number of children, max children allowed 6 ");
        	 try {
        		 children = sc.nextLine();
        	 }
        	 catch(Exception ex) {
        		 //ex.printStackTrace();
        	}
            if ( children != null && children.length() > 0 && 
            	 validatingPattern(children,NUMBER_PATTERN) && 
            	 Long.parseLong(children) <= 6 && 
            	 Long.parseLong(children) >= 0 ) {
            	 childrenCount = Long.parseLong(children);
                 childrenInputCorrect = true;
            }
            else {
            	
            	 childrenInputCorrect = false;
                	System.out.println(ANSI_RED+ "Invalid value. Enter positive integer between 1 and 6." +ANSI_RESET); 
            }
        } while ( children == null || children.length() == 0 || children.isEmpty() || childrenInputCorrect == false);


    	
    	ArrayList<Integer> childrenAge = new ArrayList<>();
    	if ( childrenCount > 0 ) System.out.println("Enter ages for all children(0 - 17 years)");
        for(int i = 0; i < Integer.parseInt(children); i++) {
        	childAgeInputCorrect = false;
        	do {
        		System.out.println("Please enter age for child "+(i+1) + " (0 - 17 years): ");
            	childAge = sc.nextLine();
        		if (childAge != null && childAge.length() > 0 && 
                    	validatingPattern(childAge, NUMBER_PATTERN) && 
                    	Long.parseLong(childAge) >= 0  &&
                    	Long.parseLong(childAge) <=  17) {
        			childAgeInputCorrect = true;
        			childrenAge.add(Integer.parseInt(childAge));
                }
        		else {
        			childAgeInputCorrect = false;
        			System.out.println(ANSI_RED+ "Invalid value. Enter positive integer between 0 and 17." +ANSI_RESET);
        		}
        		
        	}while( childAge == null || childAge.length() == 0 || childAge.isEmpty() == true || childAgeInputCorrect == false);
        }
	    
	    
	   //enter rooms details
    	do {
        	System.out.println("Enter number of rooms, max rooms allowed 6: ");
        	try {
        		rooms = sc.nextLine();
			} 
        	catch (Exception e) {
				//e.printStackTrace();
			}                    	
            if ( rooms != null && rooms.length() > 0 && 
            	validatingPattern(rooms,NUMBER_PATTERN) && 
            	Long.parseLong(rooms) <= 6 && 
            	Long.parseLong(rooms) >= 1 ) {
            	roomsInputCorrect = true;
            }
            else {
            	roomsInputCorrect = false;
            	System.out.println(ANSI_RED+ "Invalid value. Enter positive integer between 1 and 6." +ANSI_RESET); 
            }
        } while ( rooms == null || rooms.length() == 0 || rooms.isEmpty() || roomsInputCorrect == false);
    
                    String checkinDate[] = checkin.split(" ");
                    String checkoutDate[] = checkout.split(" ");
                    String cD = checkinDate[0], cM = checkinDate[1], cY = checkinDate[2], coD = checkoutDate[0], coM = checkoutDate[1], coY = checkoutDate[2];
                    System.out.println("Looking for deals on Booking...");
                    int booking = 0, kayak = 0, trivago = 0;
                    try {
                    	booking = booking_crawl(destination, cD, cM, cY, coD, coM, coY, Integer.parseInt(adults), Integer.parseInt(rooms), Integer.parseInt(children), childrenAge);
                    } catch(Exception e) {}
                    System.out.println("Looking for deals on Kayak...");
                    try {
                    	kayak = kayak_crawl(destination, cD, cM, cY, coD, coM, coY, Integer.parseInt(adults), Integer.parseInt(rooms), Integer.parseInt(children), childrenAge, booking);
                    } catch(Exception e) {}
                    System.out.println("Looking for deals on Trivago...");
                    try {
                    	trivago = trivago_crawl(destination, cD, cM, cY, coD, coM, coY, Integer.parseInt(adults), Integer.parseInt(rooms), Integer.parseInt(children), childrenAge, kayak);
                    } catch(Exception e) {}
         
                    ArrayList<HotelDetailsRating> ratingList = new ArrayList<HotelDetailsRating>();
                    ArrayList<HotelDetails> costList = new ArrayList<HotelDetails>();
        			while(ratingPQ.size() > 0 ) ratingList.add(ratingPQ.poll());
        			while(costPQ.size() > 0 )  costList.add(costPQ.poll());
                    boolean correctInput = false;
                    do {
                    	System.out.println();
                        System.out.println("Press 1 if you want results based on Hotel ratings");
                        System.out.println("Press 2 if you want results based on Hotel price");
                        System.out.println("Press 3 to exit");
                        String userInput = sc.nextLine();
                        if( validatingUserInput(userInput) == false ) {
                        	System.out.println("Please enter valid option!");
                        	continue;
                        }
                    	long optionChosen = Long.parseLong(userInput);
                    	if( optionChosen == 3 ) correctInput = true;
                    	if( optionChosen == 1 || optionChosen == 2 ) {
                    		System.out.println();
                    		System.out.println("Here are the best deals: ");
                    		System.out.println();
                    		
                    		if(optionChosen == 1 ) {
                    			if( ratingList.size() == 0 ) {
                    				System.out.println("	Sorry!, No Hotels found based on Rating!");
                    				System.out.println();
                    			}
                    			else {
                    				System.out.println();
                    				System.out.println("	Hotels based on Rating: ");
                        			System.out.println();
                        			for(int index= 0 ; index < ratingList.size(); index++ ) {
                        				HotelDetailsRating hd = ratingList.get(index);
                        				System.out.println("	Hotel Name: "+hd.hotelName);
                        				System.out.println("	Hotel Location: "+destination);
                        				System.out.println("	Hotel Cost: CAD "+hd.cost);
                        				System.out.println("	Hotel Rating: "+hd.hotelRating);
                        				System.out.println(" 	----------------------------------------------------------------");
                        				System.out.println();
                        			}
                    			}
                    		}
                    		if(optionChosen == 2){
                    			if( costList.size() == 0 ) {
                    				System.out.println("	Sorry!, No Hotels found based on Price!");
                    				System.out.println();
                    			}
                    			else {
                    				System.out.println();
                    				System.out.println("	Hotels based on Price: ");
                        			System.out.println();
                        			for(int index= 0 ; index < costList.size(); index++ ) {
                        				HotelDetails hd = costList.get(index);
                        				System.out.println("	Hotel Name: "+hd.hotelName);
                        				System.out.println("	Hotel Location: "+destination);
                        				System.out.println("	Hotel Cost: CAD "+hd.cost);
                        				System.out.println("	----------------------------------------------------------------");
                        				System.out.println();
                        			}
                    			}
                    		}
                    	}
                    	
                    }while(correctInput == false);
                    
                    return;
    }
    
    public static boolean validatingUserInput(String input ) {
        final Pattern pattern = Pattern.compile(USER_INPUT_NUMBER_PATTERN);
        Matcher matcher = pattern.matcher(input);
        if(matcher.matches() == false) return false;
        if( Long.parseLong(input) > 0 && Long.parseLong(input) <= 3 ) return true;
        return false;
    }
    
    public static boolean validatingDestinationInput(String input, int max) {
    	if( input == null || input.length() == 0 ) return false;
        final Pattern pattern = Pattern.compile(USER_INPUT_NUMBER_PATTERN_FOR_DESTINATION);
        Matcher matcher = pattern.matcher(input);
        if(matcher.matches() == false) return false;
        if( Long.parseLong(input) > 0 && Long.parseLong(input) <= max ) return true;
        return false;
    }
}