package modelo;

/**
 *
 * @author luciano
 */
import java.security.MessageDigest;

public class PasswordUtils {

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    public static boolean verifyPassword(String passwordIngresada, String hashGuardado) {
        String hashIngresado = hashPassword(passwordIngresada);
        return hashIngresado.equals(hashGuardado);
    }
}
