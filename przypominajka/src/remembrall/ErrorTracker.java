package remembrall;

public class ErrorTracker {

	private int totalErrors = 0;
	
	public void scanError(TextPos startPos, String errorMsg) {
		totalErrors++;
		System.out.println(startPos.toString() + ": " + errorMsg);
	}
	
	public void parseError(String errorMsg) {
		totalErrors++;
		System.out.println(errorMsg);
	}
	
	public int getErrorsNum() {
		return totalErrors;
	}
}
