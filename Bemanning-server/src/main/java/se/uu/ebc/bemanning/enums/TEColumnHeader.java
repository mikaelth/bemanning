package se.uu.ebc.bemanning.enums;

public enum TEColumnHeader {

	ACTIVITY("Moment","Reason"),
	STAFF("Personal","Staff"),
	TIME("Längd","Length"),
	COURSE("Kurs","Course");


	private final String seName;
	private final String enName;
	
	private TEColumnHeader(final String seName, final String enName) {
		this.seName = seName;
		this.enName = enName;
	}
	
	public String seName() { return seName; }
	public String enName() { return enName; }
	
    @Override 
    public String toString() { return seName; }

}