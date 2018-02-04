package se.uu.ebc.bemanning.enums;

public enum CourseGroup {

	BASE("Baskurser"),
	ADVANCED("Påbyggnadskurser"),
	KNO("KNO-kurser"),
	SUMMER("Sommarkurser"),
	ENGINEER("Ingenjörskurser"),
	MISC("Övrigt"),
	RESEARCH("Forskarskola"),
	DEFREEPROJ("Examensarbeten"),
	BASEYEAR("Basåret"),
	TEACHERS("Lärarkurser"),
	EXTERNAL("Externa kurser"),
	GOTLAND("Gotlandskurser"),
	ENVIRIONMENT("Miljövetenskap"),
	LIBART("Liberal Arts"),
	PROGRAM("Programansvar");


	private final String displayName;
	
	private CourseGroup(final String displayName) {
		this.displayName = displayName;
	}
	
	public String displayName() { return displayName; }
	
    @Override 
    public String toString() { return displayName; }

}