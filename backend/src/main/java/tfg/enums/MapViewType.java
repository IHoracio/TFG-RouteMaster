package tfg.enums;

public enum MapViewType {
	MAP("MAP"),
    MAP_RELIEF("MAP_RELIEF"),
    SATELLITE("SATELLITE");
    
	private final String displayName;
	
	MapViewType(String displayName) {
		this.displayName = displayName;
	}

    public String getDisplayName() {
        return displayName;
    }
    
}
