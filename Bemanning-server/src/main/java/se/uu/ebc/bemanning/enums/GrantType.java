package se.uu.ebc.bemanning.enums;

public enum GrantType
{
	Ordinarie(true),
	Efterskott(true),
	Faktura(false),
	Justering(true),
	Överföring(false);
	
	private final boolean inGrantSummary;

	private GrantType(final boolean inGrantSummary){
		this.inGrantSummary = inGrantSummary;
	}

	public boolean includeInSummary(){
		return this.inGrantSummary;
	}
}