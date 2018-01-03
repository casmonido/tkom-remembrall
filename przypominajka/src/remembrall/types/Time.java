package remembrall.types;

public class Time {
	int hour;
	int min;
	int sec;

	public Time(int hour, int min, int sec) {
		this.hour = hour;
		this.min = min;
		this.sec = sec;
	}
	
	public Time(int hour, int min) {
		this.hour = hour;
		this.min = min;
	}
	
	public Time(int hour) {
		this.hour = hour;
	}
}
