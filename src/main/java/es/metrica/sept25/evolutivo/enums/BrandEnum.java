package es.metrica.sept25.evolutivo.enums;

public enum BrandEnum {
	REPSOL("Repsol"),
    CEPSA("Cepsa"),
    BP("BP"),
    SHELL("Shell"),
    GALP("Galp"),
    PETRONOR("Petronor"),
    CAMPSA("Campsa"),
    HIFILL("Hifill"),
    AVIA("Avia"),
    LUKOIL("Lukoil"),
    EROSKI("Eroski"),
    DIA("Dia"),
    CARREFOUR("Carrefour"),
    ALDI("Aldi"),
    MERCADONA("Mercadona"),
    ECO("Eco"),
    TOTAL("Total"),
    Q8("Q8"),
    ESSO("Esso"),
    MOBIL("Mobil");

    private final String displayName;

    BrandEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
