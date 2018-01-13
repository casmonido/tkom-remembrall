package remembrall.types;

public class Datetime {
	Long year;
	Long month;
	Long day;
	Long hour;
	Long min;
	Long sec;
	private int importantPart;
	
	public Datetime(Long day, Long month, Long year) {
		this.day = day;
		this.month = month;
		this.year = year;
		importantPart = 3;
	}
	
	public Datetime(Long day, Long month, Long year, Time t) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.hour = t.hour;
		this.min = t.min;
		this.sec = t.sec;
		importantPart = 6;
	}
	
	public Datetime(Long day, Long month, Long year, Long hour) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.hour = hour;
		importantPart = 4;
	}
	
	public Datetime(Long day, Long month, Long year, Long hour, Long min) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.hour = hour;
		this.min = min;
		importantPart = 5;
	}
}
