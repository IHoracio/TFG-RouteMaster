package es.metrica.sept25.evolutivo.domain.dto.auth;

public class UserLoginDto {

    private String user;
    private String password;

    public UserLoginDto() {
    }

    public UserLoginDto(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserLoginDto [user=" + user + ", password=PROTECTED]";
    }
}
