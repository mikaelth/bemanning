package se.uu.ebc.bemanning.enums;

public enum CourseGroup {

	BASE("Baskurser"),
	ADVANCED("Påbyggnadskurser"),
	KNO("KNO-kurser"),
	CROSS("Tvärvetenskapliga kurser"),
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
	LLL("Livslångt lärande"),
	PROGRAM("Programansvar");


	private final String displayName;
	
	private CourseGroup(final String displayName) {
		this.displayName = displayName;
	}
	
	public String displayName() { return displayName; }

	/**
	 * Resolves a {@link CourseGroup} from its display name (the value stored in
	 * the {@code COURSE.COURSE_GROUP} column, e.g. "Baskurser"). Returns
	 * {@code null} if the value is null or does not match any group.
	 */
	public static CourseGroup fromDisplayName(String displayName) {
		if (displayName == null) {
			return null;
		}
		for (CourseGroup group : values()) {
			if (group.displayName.equals(displayName)) {
				return group;
			}
		}
		return null;
	}

    @Override 
    public String toString() { return displayName; }

}