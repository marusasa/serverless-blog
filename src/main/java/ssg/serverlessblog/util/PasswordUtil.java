package ssg.serverlessblog.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utility class used for password related logic.
 */
public class PasswordUtil {

	private static final SecureRandom RAND = new SecureRandom();
	private static final int ITERATIONS = 1;
	private static final int KEY_LENGTH = 512;
	private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

	public static Optional<String> generateSalt(final int length) {

		if (length < 1) {
			System.err.println("error in generateSalt: length must be > 0");
			return Optional.empty();
		}

		final byte[] salt = new byte[length];
		RAND.nextBytes(salt);

		return Optional.of(Base64.getEncoder().encodeToString(salt));
	}

	public static Optional<String> hashPassword(String password,String salt){
		final char[] chars = password.toCharArray();
		final byte[] bytes = salt.getBytes();

		final PBEKeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);

		Arrays.fill(chars, Character.MIN_VALUE);

		try {
			final SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
			final byte[] securePassword = fac.generateSecret(spec).getEncoded();
			return Optional.of(Base64.getEncoder().encodeToString(securePassword));

		} catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
			System.err.println("Exception encountered in hashPassword()");
			return Optional.empty();

		} finally {
			spec.clearPassword();
		}
	}

}
