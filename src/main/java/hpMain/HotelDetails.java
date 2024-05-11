package hpMain;

public class HotelDetails implements Comparable<HotelDetails>{
	
    public String hotelName;
    public String hotelLocation;
    public String cost;
    public String hotelRating;
    
    
    public HotelDetails() {}
    

	public HotelDetails(String hotelName, String hotelLocation, String cost,
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
				+ " cost=" + cost + ", hotelRating=" + hotelRating + "]";
	}

	@Override
	public int compareTo(HotelDetails obj) {
		int cost1  = Integer.parseInt(this.cost);
		int cost2  = Integer.parseInt(obj.cost);
		if( cost1 < cost2 ) return -1;
		else if(cost1 > cost2 ) return 1;
		else return 0;
	}
}