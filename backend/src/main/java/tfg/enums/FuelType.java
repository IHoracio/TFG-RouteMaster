package tfg.enums;

public enum FuelType {
	ALL("ALL"),
    GASOLINE_95("GASOLINE_95"),
    GASOLINE_98("GASOLINE_98"),
    DIESEL_PREMIUM("DIESEL_PREMIUM"),
    DIESEL("DIESEL"),
    GLP("GLP");
	
	private final String displayName;
	
	FuelType(String displayName) {
		this.displayName = displayName;
	}

    public String getDisplayName() {
        return displayName;
    }
	
}
