package remembrall.types;

public class Datetime {
	Long year;
	Long month;
	Long day;
	Long hour;
	Long min;
	Long sec;
	
	public Datetime(Long day, Long month, Long year) {
		this.day = day;
		this.month = month;
		this.year = year;
	}
	
	public Datetime(Long day, Long month, Long year, Time t) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.hour = t.hour;
		this.min = t.min;
		this.sec = t.sec;
	}
	
	public Datetime(Long day, Long month, Long year, Long hour) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.hour = hour;
	}
	
	public Datetime(Long day, Long month, Long year, Long hour, Long min) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.hour = hour;
		this.min = min;
	}
}
