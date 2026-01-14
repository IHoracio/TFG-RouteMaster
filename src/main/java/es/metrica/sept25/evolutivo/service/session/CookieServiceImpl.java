package es.metrica.sept25.evolutivo.service.session;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CookieServiceImpl implements CookieService {

	private static Logger log = LoggerFactory.getLogger(CookieServiceImpl.class);;

	@Value("${evolutivo.auth.secret-cookie-key}")
	private String secretString;

	private SecretKeySpec secretKeySpec;

	@Override
	public SecretKeySpec getSecretKey() {
		if (Objects.isNull(secretKeySpec)) {
			secretKeySpec = new SecretKeySpec(secretString.getBytes(), "AES");
		}
		return secretKeySpec;
	}

	@Override
	public String cipher(String plainText) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
			byte[] cifrado = cipher.doFinal(plainText.getBytes());
			return Base64.getEncoder().encodeToString(cifrado);
		} catch (Exception e) {
			log.error("[cookie-service] [" + LocalDateTime.now() + "] " + "Could not cipher the plain-text String: "
					+ "[" + plainText + "]. Reason: " + e);
			return "";
		}
	}

	@Override
	public String decipher(String cipheredText) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
			byte[] descifrado = cipher.doFinal(Base64.getDecoder().decode(cipheredText));
			return new String(descifrado);
		} catch (Exception e) {
			log.error("[cookie-service] [" + LocalDateTime.now() + "] "
					+ "Could not decipher the ciphered text String: " + "[" + cipheredText + "]. Reason: " + e);
			return "";
		}
	}

	@Override
	public boolean validate(String cipheredCookieValue) {
		if (cipheredCookieValue == null || cipheredCookieValue.isEmpty()) {
			log.error("[cookie-service] [" + LocalDateTime.now() + "] " + 
					"Could not validate the given ciphered cookie: " + 
					"[" + cipheredCookieValue + "].");
			return false;
		}

		try {
			String plainText = decipher(cipheredCookieValue);

			// Regex that verifies the following:
			// - No double separators (// o !!)
			// - No text ending in !
			// - "user!number" repeated format
			String regex = "^(?!.*//)(?!.*!!)(?!.*!$)(?!^!)([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}!\\d+)(/[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}!\\d+)*$";
			return plainText.matches(regex);

		} catch (Exception e) {
			// Failing to decipher or any other exception is considered invalid.
			log.error("[cookie-service] [" + LocalDateTime.now() + "] "
					+ "Failed to validate ciphered cookie value. Reason: " + e);
			return false;
		}
	}

	@Override
	public Map<String, Integer> deserialize(String cookieValue) {
		if (!validate(cookieValue)) {
			return new HashMap<>();
		}
		cookieValue = decipher(cookieValue);
		Map<String, Integer> users = new HashMap<>();
		for (String pair : cookieValue.split("/")) {
			String[] data = pair.split("!");
			users.put(data[0], Integer.parseInt(data[1]));
		}
		return users;
	}

	@Override
	public String serialize(Map<String, Integer> usuarios) {
		StringBuilder valor = new StringBuilder();
		boolean primero = true;

		for (Map.Entry<String, Integer> entry : usuarios.entrySet()) {
			if (!primero)
				valor.append("/");
			valor.append(entry.getKey()).append("!").append(entry.getValue());
			primero = false;
		}
		return valor.toString();
	}

	@Override
	public String update(Map<String, Integer> userMap, String usuarioActual) {
		int contador = userMap.getOrDefault(usuarioActual, 0);
		userMap.put(usuarioActual, ++contador);
		return serialize(userMap);
	}

	@Override
	public void createCookie(
			HttpServletResponse response, 
			String name, 
			String value, 
			int maxDurationSeconds
			) {
		String cipheredValue = cipher(value);
		Cookie cookie = new Cookie(name, cipheredValue);
		cookie.setPath("/");
		cookie.setMaxAge(maxDurationSeconds);
		response.addCookie(cookie);
		log.info("[cookie-service] [" + LocalDateTime.now() + "] " + 
				"Created cookie with name: [" + name + "] , and value: " + 
				"[" + value + "].");
	}

	@Override
	public void deleteCookie(HttpServletResponse response, String name) {
		Cookie cookie = new Cookie(name, "");
		cookie.setPath("/");
		cookie.setMaxAge(0); // TTL set to zero for no elimination.
		response.addCookie(cookie);
		log.info("[cookie-service] [" + LocalDateTime.now() + "] " + 
				"Deleted cookie with name: [" + name + "].");
	}

	@Override
	public Optional<String> getCookieValue(HttpServletRequest request, String name) {
		if (request.getCookies() == null) {
			log.warn("[cookie-service] [" + LocalDateTime.now() + "] " + 
					"Couldn't get value from cookie with name: [" + name + "].");
			return Optional.empty();
		}

		Optional<String> transformed = Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals(name))
				.findFirst()
				.map(cookie -> decipher(cookie.getValue()));
		if (transformed.isEmpty()) {
			log.warn("[cookie-service] [" + LocalDateTime.now() + "] " + 
					"Couldn't get value from cookie with name: [" + name + "].");
		} else {
			log.info("[cookie-service] [" + LocalDateTime.now() + "] " 
					+ "Retrieved successfully the value from cookie with name: [" 
					+ name + "].");
		}

		return transformed;
	}

	@Override
	public void updateHistoryCookie(jakarta.servlet.http.HttpServletRequest request, HttpServletResponse response, String email) {
		Optional<String> actualValue = getCookieValue(request, "historialLogins");
		if (actualValue.isPresent()) {
			Map<String, Integer> userMap = deserialize(actualValue.get());
			String newValue = update(userMap, email);
			createCookie(response, "historialLogins", newValue, 604800); // 7 días
			log.info("[cookie-service] [" + LocalDateTime.now() + "] " 
					+ "Updated the cookie successfully for email: [" 
					+ email + "].");
		}
	}

	@Override
	public int getLogins(String cookie, String user) {
		if (cookie == null || user == null)
			return 0;

		Map<String, Integer> users = deserialize(cookie);
		return users.getOrDefault(user, 1);
	}

	@Override
	public void createSessionCookie(HttpServletResponse response, String email) {
		createCookie(response, "sesionActiva", email, 1800); // 30 minutos
		log.info("[cookie-service] [" + LocalDateTime.now() + "] " 
				+ "Created the session cookie successfully for email: [" 
				+ email + "].");
	}

	@Override
	public void closeSession(HttpServletResponse response, String name) {
		deleteCookie(response, name);
		log.info("[cookie-service] [" + LocalDateTime.now() + "] " 
				+ "Closed the session successfully for email: [" 
				+ name + "].");
	}
}
