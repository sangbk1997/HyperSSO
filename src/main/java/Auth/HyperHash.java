package Auth;

import org.apache.shiro.codec.CodecException;
import org.apache.shiro.crypto.UnknownAlgorithmException;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.SimpleHash;

public class HyperHash extends SimpleHash {
    public HyperHash(String algorithmName, Object source, Object salt) throws CodecException, UnknownAlgorithmException {
        super(algorithmName, source, salt);
    }
}


class Tigo {
    public static void main(String[] args) {
        int numberOfIterations = 1;
        String encodedPassword = "123456";
        DefaultHashService hashService = new DefaultHashService();

        hashService.setHashAlgorithmName("SHA-256");
        String dynaSalt;

        hashService.setHashIterations(numberOfIterations);
        dynaSalt = "abc123";
        HashRequest request = (new HashRequest.Builder()).setSalt(dynaSalt).setSource(encodedPassword).build();
        System.out.println(hashService.computeHash(request).toHex());
    }
}
