package tfg.entity.gasolinera;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tfg.entity.user.User;

@Entity
@Table(name = "user_saved_gas_stations")
public class UserSavedGasStation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String alias;
	
	@Column
    private String googlePlaceId;

    @Column
    private String selectedAddress;

    @Column
    private Double selectedLat;

    @Column
    private Double selectedLng;

	@ManyToOne
	@JsonBackReference("user-gasStations")
	private User user;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private Gasolinera gasolinera;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Gasolinera getGasolinera() {
		return gasolinera;
	}

	public void setGasolinera(Gasolinera gasolinera) {
		this.gasolinera = gasolinera;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGooglePlaceId() {
		return googlePlaceId;
	}

	public void setGooglePlaceId(String googlePlaceId) {
		this.googlePlaceId = googlePlaceId;
	}

	public String getSelectedAddress() {
		return selectedAddress;
	}

	public void setSelectedAddress(String selectedAddress) {
		this.selectedAddress = selectedAddress;
	}

	public Double getSelectedLat() {
		return selectedLat;
	}

	public void setSelectedLat(Double selectedLat) {
		this.selectedLat = selectedLat;
	}

	public Double getSelectedLng() {
		return selectedLng;
	}

	public void setSelectedLng(Double selectedLng) {
		this.selectedLng = selectedLng;
	}

}
