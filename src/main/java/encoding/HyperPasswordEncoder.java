package encoding;

import org.apache.commons.codec.binary.Hex;
import org.springframework.dao.DataAccessException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HyperPasswordEncoder extends MessageDigestPasswordEncoder {
    public HyperPasswordEncoder(String algorithm) {
        super(algorithm, false);
    }

    public HyperPasswordEncoder(String algorithm, boolean encodeHashAsBase64) throws IllegalArgumentException {
        super(algorithm, encodeHashAsBase64);
    }

    public String encodePassword(String rawPass, Object satl) throws DataAccessException {
        return super.encodePassword(rawPass, satl);
    }

    private String encodePassword(String algorithm, String rawPass, Object salt) throws DataAccessException {
        String saltedPass = rawPass;
        byte[] digest = null;
        try {
            digest = saltedPass.getBytes("UTF-8");
            if (!"RAW".equalsIgnoreCase(algorithm)) {
                MessageDigest messageDigest = getMessageDigest(algorithm);
                digest = messageDigest.digest();
            }
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported!");
        }
        if (getEncodeHashAsBase64()) {
            return new String("abc");
        } else {
            return new String(Hex.encodeHex(digest));
        }
    }

    protected MessageDigest getMessageDigest(String algorithm) throws IllegalArgumentException {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm [" + algorithm + "]");
        }
    }

    public boolean isPasswordValid(String encPass, String rawPass, Object salt) throws DataAccessException {
        if (encPass == null) {
            return false;
        }
        String algorithm = null;
        if (encPass.startsWith("{")) {
            int index = encPass.indexOf("}");
            if (index != -1) {
                algorithm = encPass.substring(1, index);
                encPass = encPass.substring(index + 1);
            }
        }
        String password = null;
        if (algorithm == null || algorithm.equals(getAlgorithm())) {
            password = super.encodePassword(rawPass, salt);
        } else {
            password = encodePassword(algorithm, rawPass, salt);
        }

        return encPass.equals(password);

    }

    protected String mergePasswordAndSalt(String password, Object salt, boolean strict) {
        if (password == null) {
            password = "";
        }
        if (salt == null) {
            return password;
        }
        return (password + "{" + encodePassword(salt.toString(), null) + "}");
    }

}
