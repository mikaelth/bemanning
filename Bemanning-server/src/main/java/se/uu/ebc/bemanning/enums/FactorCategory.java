package se.uu.ebc.bemanning.enums;

public enum FactorCategory {

	ADMIN("Administration"),
	DEVELOPMENT("Utveckling"),
	LECTURE("Föreläsning"),
	PRACTICAL("Laboration, Lektion"),
	EXCURSION("Exkursion, Litteraturseminarium"),
	SEMINAR("Presentation, Handledning");


	private final String displayName;
	
	private FactorCategory(final String displayName) {
		this.displayName = displayName;
	}
	
	public String displayName() { return displayName; }
	
    @Override 
    public String toString() { return displayName; }

}