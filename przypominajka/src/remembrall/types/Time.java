package remembrall.types;

public class Time {
	Long hour;
	Long min; //czasem -1
	Long sec;

	public Time(Long hour, Long min, Long sec) {
		this.hour = hour;
		this.min = min;
		this.sec = sec;
	}
	
	public Time(Long hour, Long min) {
		this.hour = hour;
		this.min = min;
	}
	
	public Time(Long hour) {
		this.hour = hour;
	}
}
