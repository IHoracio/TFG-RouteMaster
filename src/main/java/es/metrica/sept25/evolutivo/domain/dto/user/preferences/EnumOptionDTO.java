package es.metrica.sept25.evolutivo.domain.dto.user.preferences;

public class EnumOptionDTO {

	private String code;   // ES, EN, DARK
    private String label;  // Español, English, Oscuro

    public EnumOptionDTO(String code, String label) {
        this.code = code;
        this.label = label;
    }

	public String getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

    
}
