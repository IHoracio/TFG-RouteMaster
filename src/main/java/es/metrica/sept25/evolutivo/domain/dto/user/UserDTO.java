package es.metrica.sept25.evolutivo.domain.dto.user;

public class UserDTO {

	private String email;
	private String password;
	private String name;
	private String surname;

	public UserDTO(String email, String password, String name, String surname) {
		super();
		this.email = email;
		this.password = password;
		this.name = name;
		this.surname = surname;
	}

	public UserDTO() {
		// TODO Auto-generated constructor stub
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	@Override
	public String toString() {
		return "UserDTO [email=" + email + ", password=" + password + ", name=" + name + ", surname=" + surname + "]";
	}

}
