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
	
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
	        return false;
	    }
	    if (!Time.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
	    final Time other = (Time) obj;
	    if (this.hour == other.hour && this.min == other.min &&
	    		this.sec == other.sec) 
	        return false;
	    return true;
	}

	@Override
	public int hashCode() {
	    int hash = 3;
	    hash = (int) (53 * hash + this.hour);
	    hash = (int) (53 * hash + this.min);
	    hash = (int) (53 * hash + this.sec);
	    return hash;
	}
}	
