package es.metrica.sept25.evolutivo.service.session;

import jakarta.servlet.http.HttpSession;

public interface SessionManagementService {
	void register(HttpSession session);
	void login(HttpSession session);
	void logout(HttpSession session);
}
