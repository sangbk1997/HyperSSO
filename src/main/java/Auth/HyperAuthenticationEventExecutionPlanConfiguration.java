package Auth;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

//import org.springframework.security.crypto.password.PasswordEncoder;

//import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration("MyAuthenticationEventExecutionPlanConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class HyperAuthenticationEventExecutionPlanConfiguration implements AuthenticationEventExecutionPlanConfigurer {

    @Autowired
    private CasConfigurationProperties casProperties;

    ServicesManager servicesManager;

    PrincipalFactory principalFactory;

//    @Bean
//    public DataSource dataSource() {
//        return DataSourceBuilder
//                .create()
//                .username("monty")
//                .password("Hyperlogya@123")
//                .url("jdbc:mysql://172.20.40.124:3306/sso?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC")
//                .driverClassName("com.mysql.cj.jdbc.Driver")
//                .build();
//    }

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder
                .create()
//                .username("ihcm_dev")
                .username("ihcm_test")
//                .password("ihcmco10@")
                .password("AHdxEN3LzMaGajV5")
//                .url("jdbc:mysql://172.16.40.213:3306/ihcm_dev?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC")
                .url("jdbc:mysql://172.16.40.213:3306/ihcm_test?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }


    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
        MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder("SHA-256");
//        encoder.setEncodeHashAsBase64(true);
        return encoder;
    }


    @Bean
    public AuthenticationHandler myAuthenticationHandler() {
        final HyperAuthenticationHandler handler = new HyperAuthenticationHandler("HyperCustomeHandler", servicesManager, principalFactory, 1, dataSource());
        /*
            Configure the handler by invoking various setter methods.
            Note that you also have full access to the collection of resolved CAS settings.
            Note that each authentication handler may optionally qualify for an 'order`
            as well as a unique name.
        */
        handler.setAlgorithmName("SHA-256");
        handler.setNumberOfIterations(1);
//        handler.setSaltFieldName("salt");
        handler.setSaltFieldName("SALT");
//        handler.setSql("SELECT * FROM users WHERE email = ?");
        handler.setSqlGetOrg("SELECT * FROM HFW_ORGANIZATION_VIRTUAL_HOST WHERE NAME = ?");
        handler.setSqlGetUser("SELECT * FROM HFW_USER WHERE USERNAME = ? AND ORG_ID = ?");
//        handler.setPasswordEncoder();
//        handler.setPasswordFieldName("password");
        handler.setPasswordFieldName("PASSWORD");
        handler.setDisabledFieldName("STATUS");

        return handler;
//        return null;
    }

    @Override
    public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
        if (feelingGoodOnAMondayMorning()) {
            plan.registerAuthenticationHandler(myAuthenticationHandler());
        }
    }

    private boolean feelingGoodOnAMondayMorning() {
        return true;
    }
}
