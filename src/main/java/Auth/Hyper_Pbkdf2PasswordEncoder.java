//package Auth;
//
//import java.security.GeneralSecurityException;
//import java.security.NoSuchAlgorithmException;
//import java.util.Base64;
//
//import javax.crypto.SecretKeyFactory;
//import javax.crypto.spec.PBEKeySpec;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.codec.Hex;
//import org.springframework.security.crypto.codec.Utf8;
//import org.springframework.security.crypto.keygen.BytesKeyGenerator;
//import org.springframework.security.crypto.keygen.KeyGenerators;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
//import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
//
//import static org.springframework.security.crypto.util.EncodingUtils.concatenate;
//import static org.springframework.security.crypto.util.EncodingUtils.subArray;
//
///**
// * A {@code PasswordEncoder} implementation that uses PBKDF2 with a configurable number of
// * iterations and a random 8-byte random salt value.
// * <p>
// * The width of the output hash can also be configured.
// * <p>
// * The algorithm is invoked on the concatenated bytes of the salt, secret and password.
// *
// * @author Rob Worsnop
// * @author Rob Winch
// * @since 4.1
// */
//public class Hyper_Pbkdf2PasswordEncoder extends Pbkdf2PasswordEncoder {
//
//    private static final int DEFAULT_HASH_WIDTH = 256;
//    private static final int DEFAULT_ITERATIONS = 185000;
//
////    private final BytesKeyGenerator saltGenerator = KeyGenerators.secureRandom();
//
//    private final byte[] secret;
//    private final int hashWidth;
//    private final int iterations;
//    private String algorithm = SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256.name();
//    private boolean encodeHashAsBase64;
//    public static String customSalt = "Hyperlogy";
//
//    /**
//     * Constructs a PBKDF2 password encoder with no additional secret value. There will be
//     * {@value DEFAULT_ITERATIONS} iterations and a hash width of {@value DEFAULT_HASH_WIDTH}. The default is based upon aiming for .5
//     * seconds to validate the password when this class was added.. Users should tune
//     * password verification to their own systems.
//     */
//    public Hyper_Pbkdf2PasswordEncoder() {
//        this("");
//    }
//
//    /**
//     * Constructs a standard password encoder with a secret value which is also included
//     * in the password hash. There will be {@value DEFAULT_ITERATIONS} iterations and a hash width of {@value DEFAULT_HASH_WIDTH}.
//     *
//     * @param secret the secret key used in the encoding process (should not be shared)
//     */
//    public Hyper_Pbkdf2PasswordEncoder(CharSequence secret) {
//        this(secret, DEFAULT_ITERATIONS, DEFAULT_HASH_WIDTH);
//    }
//
//    /**
//     * Constructs a standard password encoder with a secret value as well as iterations
//     * and hash.
//     *
//     * @param secret     the secret
//     * @param iterations the number of iterations. Users should aim for taking about .5
//     *                   seconds on their own system.
//     * @param hashWidth  the size of the hash
//     */
//    public Hyper_Pbkdf2PasswordEncoder(CharSequence secret, int iterations, int hashWidth) {
//        this.secret = Utf8.encode(secret);
//        this.iterations = iterations;
//        this.hashWidth = hashWidth;
//    }
//
//    public Hyper_Pbkdf2PasswordEncoder(CharSequence secret, boolean encodeHashAsBase64) {
//        this.secret = Utf8.encode(secret);
//        this.iterations = DEFAULT_ITERATIONS;
//        this.hashWidth = DEFAULT_HASH_WIDTH;
//        this.encodeHashAsBase64 = encodeHashAsBase64;
//    }
//
//    @Override
//    public String encode(CharSequence rawPassword) {
////        byte[] salt = this.saltGenerator.generateKey();
//        byte[] salt = customSalt.getBytes();
//        byte[] encoded = encode(rawPassword, salt);
//        return encode(encoded);
//    }
//
//    private String encode(byte[] bytes) {
//        if (this.encodeHashAsBase64) {
//            return Base64.getEncoder().encodeToString(bytes);
//        }
//        return String.valueOf(Hex.encode(bytes));
//    }
//
//    @Override
//    public boolean matches(CharSequence rawPassword, String encodedPassword) {
//        byte[] digested = decode(encodedPassword);
////        byte[] salt = subArray(digested, 0, this.saltGenerator.getKeyLength());
//        byte[] salt = subArray(digested, 0, customSalt.length());
//        return matches(digested, encode(rawPassword, salt));
//    }
//
//    private static boolean matches(byte[] expected, byte[] actual) {
//        if (expected.length != actual.length) {
//            return false;
//        }
//
//        int result = 0;
//        for (int i = 0; i < expected.length; i++) {
//            result |= expected[i] ^ actual[i];
//        }
//        return result == 0;
//    }
//
//    private byte[] decode(String encodedBytes) {
//        if (this.encodeHashAsBase64) {
//            return Base64.getDecoder().decode(encodedBytes);
//        }
//        return Hex.decode(encodedBytes);
//    }
//
//    private byte[] encode(CharSequence rawPassword, byte[] salt) {
//        try {
//            PBEKeySpec spec = new PBEKeySpec(rawPassword.toString().toCharArray(),
//                    concatenate(salt, this.secret), this.iterations, this.hashWidth);
//            SecretKeyFactory skf = SecretKeyFactory.getInstance(this.algorithm);
//            return concatenate(salt, skf.generateSecret(spec).getEncoded());
//        } catch (GeneralSecurityException e) {
//            throw new IllegalStateException("Could not create hash", e);
//        }
//    }
//
//    public static void main(String[] args) {
//        Hyper_Pbkdf2PasswordEncoder obj01 = new Hyper_Pbkdf2PasswordEncoder("Hyperlogy", true);
//        Hyper_Pbkdf2PasswordEncoder obj02 = new Hyper_Pbkdf2PasswordEncoder("Hyperlogy", true);
//        Hyper_Pbkdf2PasswordEncoder obj03 = new Hyper_Pbkdf2PasswordEncoder("Hyper", 185000, 256);
//        Hyper_Pbkdf2PasswordEncoder obj04 = new Hyper_Pbkdf2PasswordEncoder("Hyper", 185000, 256);
//        String encode01 = obj03.encode("sangnb");
//        String encode02 = obj04.encode("sangnb");
//        System.out.println(encode01);
//        System.out.println(encode02);
//        System.out.println(encode01.equals(encode02));
//        System.out.println("Good bye !");
//    }
//}
