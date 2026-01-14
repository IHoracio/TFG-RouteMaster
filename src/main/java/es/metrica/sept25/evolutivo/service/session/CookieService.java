package es.metrica.sept25.evolutivo.service.session;

import java.util.Map;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
	SecretKeySpec getSecretKey();

	String cipher(String plainText);

	String decipher(String cipheredText);

	/**
	 * Validates the format of a ciphered cookie by the business rules. Tries to
	 * decipher it first and then validates the plain-text contents.
	 * 
	 * @param cipheredCookieValue The cookie value pre-deciphering.
	 * @return true if the deciphered value is formatted correctly. False in case it
	 *         isn't or if the deciphering fails.
	 */
	boolean validate(String cipheredCookieValue);

	/**
	 * Converts the value of the cookie into a map of users with their counters
	 * 
	 * @param cookieValue Value of the cookie to deserialize.
	 * @return Map of <Usuario (String), Contador (Integer)> or an empty map if
	 *         invalid.
	 */
	Map<String, Integer> deserialize(String cookieValue);

	/**
	 * Serializes a map of users to a String format for cookies.
	 * 
	 * @param users Map with users and login counters.
	 * @return String in the format user!counter[/user!counter]*"
	 */
	String serialize(Map<String, Integer> usuarios);

	/**
	 * Updates the access counter for a specific user
	 * 
	 * @param userMap       Current user map structure
	 * @param value         Current cookie value
	 * @param usuarioActual User to update
	 * @return The new serialized value to be put in the cookie
	 */
	String update(Map<String, Integer> userMap, String usuarioActual);

	/**
	 * Creates a new cookie with the basic security configuration.
	 */
	void createCookie(HttpServletResponse response, String name, String value, int maxDurationSeconds);

	/**
	 * Deletes a cookie from the client
	 *
	 * @param response An HttpServletResponse object to manipulate the cookies.
	 * @param name   Name of the cookie to delete.
	 */
	void deleteCookie(HttpServletResponse response, String name);

	/**
	 * Obtains the value of a specific cookie.
	 *
	 * @param request A HttpServletRequest object to get the cookie out of.
	 * @param name  Name of the cookie to find
	 * @return Value of the cookie or an empty Optional if it doesn't exist.
	 */
	Optional<String> getCookieValue(HttpServletRequest request, String name);

	/**
	 * Updates the successful login history in the cookies.
	 * @param response The response which contains the users cookie
	 * @param email The user to update the login info for
	 */
	void updateHistoryCookie(HttpServletRequest request, HttpServletResponse response, String email);

	/**
	 * Returns the amount of times the user has gone through log-in
	 * @param cookie The cookie to extract the count from
	 * @param user The user to get the count for
	 * @return The amount of times the user has gone through log-in
	 */
	int getLogins(String cookie, String user);

	/**
	 * Creates the active session cookie.
	 */
	void createSessionCookie(HttpServletResponse response, String correo);

	/**
	 * Handles the logout cookie deletion.
	 */
	void closeSession(HttpServletResponse response, String nombre);
}
