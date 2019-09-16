
package Auth;

import encoding.HyperPasswordEncoder;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Map;

//import org.springframework.web.util.WebUtils;

public class HyperAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {
    protected String algorithmName;
    protected String sqlGetUser;
    protected String sqlGetOrg;
    protected String passwordFieldName = "password";
    protected String saltFieldName = "salt";
    protected String expiredFieldName;
    protected String disabledFieldName = "status";
    protected String numberOfIterationsFieldName;
    protected int numberOfIterations;
    protected String staticSalt;

    public HyperAuthenticationHandler(final String name, final ServicesManager servicesManager, final PrincipalFactory principalFactory, final Integer order, final DataSource dataSource, final String algorithmName, final String sqlGetUser, final String sqlGetOrg, final String passwordFieldName, final String saltFieldName, final String expiredFieldName, final String disabledFieldName, final String numberOfIterationsFieldName, final int numberOfIterations, final String staticSalt) {
        super(name, servicesManager, principalFactory, order, dataSource);
        this.algorithmName = algorithmName;
        this.sqlGetUser = sqlGetUser;
        this.sqlGetOrg = sqlGetOrg;
        this.passwordFieldName = passwordFieldName;
        this.saltFieldName = saltFieldName;
        this.expiredFieldName = expiredFieldName;
        this.disabledFieldName = disabledFieldName;
        this.numberOfIterationsFieldName = numberOfIterationsFieldName;
        this.numberOfIterations = numberOfIterations;
        this.staticSalt = staticSalt;
    }

    public HyperAuthenticationHandler(final String name, final ServicesManager servicesManager, final PrincipalFactory principalFactory, final Integer order, final DataSource dataSource) {
        super(name, servicesManager, principalFactory, order, dataSource);
    }


    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential transformedCredential, final String originalPassword) throws GeneralSecurityException, PreventedException {
        if (!StringUtils.isBlank(this.sqlGetUser) && !StringUtils.isBlank(this.sqlGetUser) && !StringUtils.isBlank(this.algorithmName) && this.getJdbcTemplate() != null) {
            String username = transformedCredential.getUsername();
            try {
                final RequestContext context = RequestContextHolder.getRequestContext();
                WebApplicationService service = WebUtils.getService(context);
                String serviceUrl = service.getId();
                System.out.println("Service Url");
                System.out.println(serviceUrl);
                String hostName = "";
                String orgId = "";
                try {
                    URL aURL = new URL(serviceUrl);
                    hostName = aURL.getHost();
                    System.out.println("Hostname");
                    System.out.println(hostName);
                    System.out.println(this.sqlGetOrg);
                    Map<String, Object> hostObj = this.getJdbcTemplate().queryForMap(this.sqlGetOrg, new Object[]{hostName});
                    System.out.println(hostObj);
                    String ORG_ID = "ORG_ID";
                    orgId = hostObj.get(ORG_ID).toString();
                    System.out.println("OrgId");
                    System.out.println(orgId);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Map<String, Object> values = this.getJdbcTemplate().queryForMap(this.sqlGetUser, new Object[]{username, orgId});
                String digestedPassword = this.digestEncodedPassword(transformedCredential.getPassword(), values);
                System.out.println("User Obj");
                System.out.println(values);
                System.out.println("Check validPassword");
                System.out.println("Encoded inputPassword");
                System.out.println(digestedPassword);
                System.out.println("Encoded dbPassword");
                System.out.println(values.get(this.passwordFieldName));
                if (!values.get(this.passwordFieldName).equals(digestedPassword)) {
                    throw new FailedLoginException("Password does not match value on record.");
                } else {
                    System.out.println("DigestedPassword");
                    System.out.println(digestedPassword);
                    String dbDisabled;
                    if (StringUtils.isNotBlank(this.expiredFieldName) && values.containsKey(this.expiredFieldName)) {
                        dbDisabled = values.get(this.expiredFieldName).toString();
                        if (BooleanUtils.toBoolean(dbDisabled) || "1".equals(dbDisabled)) {
                            throw new AccountPasswordMustChangeException("Password has expired");
                        }
                    }

                    if (StringUtils.isNotBlank(this.disabledFieldName) && values.containsKey(this.disabledFieldName)) {
                        dbDisabled = values.get(this.disabledFieldName).toString();
                        if (BooleanUtils.toBoolean(dbDisabled) || "0".equals(dbDisabled)) {
                            throw new AccountDisabledException("Account has been disabled");
                        }
                    }

                    System.out.println("Return values");
                    System.out.println(this.createHandlerResult(transformedCredential, this.principalFactory.createPrincipal(username), new ArrayList(0)));
                    return this.createHandlerResult(transformedCredential, this.principalFactory.createPrincipal(username), new ArrayList(0));
                }
            } catch (IncorrectResultSizeDataAccessException var7) {
                if (var7.getActualSize() == 0) {
                    throw new AccountNotFoundException(username);
                } else {
                    throw new FailedLoginException(username);
                }
            } catch (DataAccessException var8) {
                throw new PreventedException(username, var8);
            }
        } else {
            if (StringUtils.isBlank(this.sqlGetUser)) {
                System.out.println("Blank sql");
                System.out.println(this.sqlGetUser);
            }
            if (StringUtils.isBlank(this.algorithmName)) {
                System.out.println("Blank algorithmName");
            }
            if (this.getJdbcTemplate() == null) {
                System.out.println("Null JDBC Template");
            }
            throw new GeneralSecurityException("Authentication handler is not configured correctly");
        }
    }

    protected String digestEncodedPassword(final String encodedPassword, final Map<String, Object> values) {
//               DefaultHashService hashService = new DefaultHashService();
//        if (StringUtils.isNotBlank(this.staticSalt)) {
//            hashService.setPrivateSalt(Util.bytes(this.staticSalt));
//        }
//
//        hashService.setHashAlgorithmName(this.algorithmName);
//        String dynaSalt;
//        if (values.containsKey(this.numberOfIterationsFieldName)) {
//            dynaSalt = values.get(this.numberOfIterationsFieldName).toString();
//            hashService.setHashIterations(Integer.parseInt(dynaSalt));
//        } else {
//            hashService.setHashIterations(this.numberOfIterations);
//        }

        if (!values.containsKey(this.saltFieldName)) {
            throw new IllegalArgumentException("Specified field name for salt does not exist in the results");
        } else {
//            dynaSalt = values.get(this.saltFieldName).toString();
//            HashRequest request = (new Builder()).setSalt(dynaSalt).setSource(encodedPassword).build();
//            System.out.println("Hyper encode");
//            System.out.println(encodedPassword);
//            System.out.println(values);
//            System.out.println(values.get(this.saltFieldName).toString());
//            System.out.println(values.get(this.passwordFieldName).toString());
//            return hashService.computeHash(request).toHex();
            HyperPasswordEncoder hyperPasswordEncoder = new HyperPasswordEncoder("SHA-256", true);
            String result = hyperPasswordEncoder.encodePassword(encodedPassword, values.get(this.saltFieldName));
            System.out.println("Hyper encoder");
            System.out.println(encodedPassword);
            System.out.println("Field Salt");
            System.out.println(this.saltFieldName);
            System.out.println(values.get(this.saltFieldName));
            System.out.println(result);
            return result;
        }
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getSqlGetUser() {
        return sqlGetUser;
    }

    public void setSqlGetUser(String sql) {
        this.sqlGetUser = sql;
    }

    public String getSqlGetOrg() {
        return sqlGetOrg;
    }

    public void setSqlGetOrg(String sql) {
        this.sqlGetOrg = sql;
    }

    public String getPasswordFieldName() {
        return passwordFieldName;
    }

    public void setPasswordFieldName(String passwordFieldName) {
        this.passwordFieldName = passwordFieldName;
    }

    public String getSaltFieldName() {
        return saltFieldName;
    }

    public void setSaltFieldName(String saltFieldName) {
        this.saltFieldName = saltFieldName;
    }

    public String getExpiredFieldName() {
        return expiredFieldName;
    }

    public void setExpiredFieldName(String expiredFieldName) {
        this.expiredFieldName = expiredFieldName;
    }

    public String getDisabledFieldName() {
        return disabledFieldName;
    }

    public void setDisabledFieldName(String disabledFieldName) {
        this.disabledFieldName = disabledFieldName;
    }

    public String getNumberOfIterationsFieldName() {
        return numberOfIterationsFieldName;
    }

    public void setNumberOfIterationsFieldName(String numberOfIterationsFieldName) {
        this.numberOfIterationsFieldName = numberOfIterationsFieldName;
    }

    public int getNumberOfIterations() {
        return numberOfIterations;
    }

    public void setNumberOfIterations(int numberOfIterations) {
        this.numberOfIterations = numberOfIterations;
    }

    public String getStaticSalt() {
        return staticSalt;
    }

    public void setStaticSalt(String staticSalt) {
        this.staticSalt = staticSalt;
    }
}


//import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
//import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
//import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
//import org.apereo.cas.authentication.principal.PrincipalFactory;
//import org.apereo.cas.services.ServicesManager;
//
//import javax.security.auth.login.FailedLoginException;
//
//public class HyperAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
//
////    @Autowired
////    private Logger logger;
//
//    public HyperAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
//        super(name, servicesManager, principalFactory, order);
//    }
//
//    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential,
//                                                                                        final String originalPassword) throws FailedLoginException {
//        String username = "tigo";
//        System.out.println("Hyper Custome");
//
//        System.out.println(String.valueOf(credential));
//        System.out.println(originalPassword);
//        if (true) {
//            return createHandlerResult(credential,
//                    this.principalFactory.createPrincipal(username), null);
//        }
//        throw new FailedLoginException("Sorry, you are a failure!");
//    }
//}
