package tfg.enums;

public enum FuelType {
	ALL("ALL"),
    GASOLINE("GASOLINE"),
    DIESEL("DIESEL");
	
	private final String displayName;
	
	FuelType(String displayName) {
		this.displayName = displayName;
	}

    public String getDisplayName() {
        return displayName;
    }
	
}
