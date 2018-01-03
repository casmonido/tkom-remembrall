package remembrall.types;

public class Datetime {
	int year;
	int month;
	int day;
	int hour;
	int min;
	int sec;
	
	public Datetime(int day, int month, int year) {
		this.day = day;
		this.month = month;
		this.year = year;
	}
	
	public Datetime(int day, int month, int year, Time t) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.hour = t.hour;
		this.min = t.min;
		this.sec = t.sec;
	}
	
	public Datetime(int day, int month, int year, int hour) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.hour = hour;
	}
	
	public Datetime(int day, int month, int year, int hour, int min) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.hour = hour;
		this.min = min;
	}
}
