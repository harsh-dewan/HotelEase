package hpMain;

public  class HotelDetailsRating implements Comparable<HotelDetailsRating>{
	
    public String hotelName;
    public String hotelLocation;
    public String cost;
    public String hotelRating;
    
    
    public HotelDetailsRating() {}
    
    public HotelDetailsRating(HotelDetails object) {
    	this.hotelName = object.hotelName;
    	this.hotelLocation = object.hotelLocation;
    	this.cost = object.cost;
    	this.hotelRating  = object.hotelRating;
    }
    

	public HotelDetailsRating(String hotelName, String hotelLocation, String cost,
			String hotelRating) {
		super();
		this.hotelName = hotelName;
		this.hotelLocation = hotelLocation;
		this.cost = cost;
		this.hotelRating = hotelRating;
	}
	

	@Override
	public String toString() {
		return "HotelDetails [hotelName=" + hotelName + ", hotelLocation=" + hotelLocation + ", "
				+ "cost=" + cost + ", hotelRating=" + hotelRating + "]";
	}


	@Override
	public int compareTo(HotelDetailsRating obj) {
		double rating1  = Double.parseDouble(this.hotelRating);
		double rating2  = Double.parseDouble(obj.hotelRating);
		if( rating1 > rating2 ) return -1;
		else if(rating1 < rating2 ) return 1;
		else return 0;
	}
	
	

}