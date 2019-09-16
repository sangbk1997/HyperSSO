//package Auth;
//
//import com.sun.nio.sctp.HandlerResult;
//import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
//import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
//
//import javax.security.auth.login.FailedLoginException;
//
//public class HyperAuthenHandler extends AbstractUsernamePasswordAuthenticationHandler {
//    protected HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential,
//                                                                 final String originalPassword) {
//        if (true) {
//            return createHandlerResult(credential,
//                    this.principalFactory.createPrincipal(username), null);
//        }
//        throw new FailedLoginException("Sorry, you are a failure!");
//    }
//}
