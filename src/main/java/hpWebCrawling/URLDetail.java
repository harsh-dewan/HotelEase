package hpWebCrawling;

public class URLDetail {
	
	private String webpageCategory;
	private String webpageLanguage;	
	private String url;	
	private String webpageTitle;

	public URLDetail(String webpageCategory, String webpageLanguage, String url, String webpageTitle) {
		
		this.webpageCategory = webpageCategory;
		this.webpageLanguage = webpageLanguage;
		this.url = url;
		this.webpageTitle = webpageTitle;
	}

	@Override
	public String toString() {
		return "URLDetail [webpageCategory=" + webpageCategory + ", webpageLanguage=" + webpageLanguage + ", url=" + url
				+ ", webpageTitle=" + webpageTitle + "]";
	}

}
