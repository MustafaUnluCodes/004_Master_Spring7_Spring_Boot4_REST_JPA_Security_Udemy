package com.eazybytes.jobportal.security;


import ch.qos.logback.core.net.SyslogOutputStream;
import com.eazybytes.jobportal.security.filter.JwtTokenValidatorFilter;
import com.eazybytes.jobportal.security.util.CorsProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity // mentioning this annotation is optional as it is already included in spring-boot-starter-security dependency
@RequiredArgsConstructor
public class JobPartalSecurityConfig {

    @Qualifier("publicPaths")
    private final List<String> publicPaths;

    @Qualifier("securedPaths")
    private final List<String> securedPaths;

    @Qualifier("adminPaths")
    private final List<String> adminPaths;

    @Qualifier("employerPaths")
    private final List<String> employerPaths;

    @Qualifier("jobSeekerPaths")
    private final List<String> jobSeekerPaths;


    private final CorsProperties corsProperties;
    /*


    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) {
        /*
        return  http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated())
                .formLogin(flc -> flc.disable())
                .httpBasic(withDefaults())
                .build();

         */

        /*
        return  http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll())
                .formLogin(flc -> flc.disable())
                .httpBasic(withDefaults())
                .build();
      */


        /*
        return  http.authorizeHttpRequests((requests) -> requests.anyRequest().denyAll())
                .formLogin(flc -> flc.disable())
                .httpBasic(withDefaults())
                .build();

         */


        /*
        return  http.csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests((requests) -> requests.anyRequest().permitAll())
                .formLogin(flc -> flc.disable())
                .httpBasic(withDefaults())
                .build();
        */

        /*
        return  http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests.anyRequest().permitAll())
                .formLogin(flc -> flc.disable())
                .httpBasic(withDefaults())
                .build();

         */

        /*
        return  http.csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests((requests) ->
                        requests.requestMatchers("/api/companies").permitAll()
                        .requestMatchers("/api/contacts").authenticated())
                .formLogin(flc -> flc.disable())
                .httpBasic(withDefaults())
                .build();
           *


    }
    */

    @Bean
    SecurityFilterChain customSecurityFilterChain(HttpSecurity http) {

        /*
        return  http.csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests((requests) ->
                        requests.requestMatchers("/api/companies").permitAll()
                                .requestMatchers("/api/contacts").authenticated())
                .formLogin(flc -> flc.disable())
                .httpBasic(withDefaults())
                .build();
        */

        /*
        return  http.csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests((requests) ->
                        requests.requestMatchers("/api/companies/public").permitAll()
                                .requestMatchers("/api/contacts/public").permitAll())
                .formLogin(flc -> flc.disable())
                .httpBasic(withDefaults())
                .build();

         */

/*
        return  http.csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests((requests) ->
                        requests.requestMatchers(RegexRequestMatcher.regexMatcher(".*public$")).permitAll()
                                .requestMatchers("/api/swagger-ui.html",
                                            "/swagger-ui/**",
                                            "/api/v3/api-docs/**",
                                            "/swagger-resources/**",
                                            "/swagger-ui.html",
                                            "/webjars/**").permitAll())
                .formLogin(flc -> flc.disable())
                .httpBasic(withDefaults())
                .build();


 */


//        return  http.csrf(csrfConfig -> csrfConfig.disable())
//                .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))
//                .authorizeHttpRequests( requests -> {
//                            publicPaths.forEach(path -> requests.requestMatchers(path).permitAll());
//                            securedPaths.forEach(path -> requests.requestMatchers(path).authenticated());
//                            requests.anyRequest().denyAll();
//                        }
//                       )
//                .addFilterBefore( new JwtTokenValidatorFilter(publicPaths), BasicAuthenticationFilter.class)
//                .formLogin(flc -> flc.disable())
//                .httpBasic(withDefaults())
//                .build();

//        return  http
//                .csrf(csrfConfig -> csrfConfig
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
//                .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))
//                .authorizeHttpRequests( requests -> {
//                            publicPaths.forEach(path -> requests.requestMatchers(path).permitAll());
//                            adminPaths.forEach(path -> requests.requestMatchers(path).hasRole("ADMIN"));
//                            securedPaths.forEach(path -> requests.requestMatchers(path).authenticated());
//                            requests.anyRequest().denyAll();
//                        }
//                       )
//                .addFilterBefore( new JwtTokenValidatorFilter(publicPaths), BasicAuthenticationFilter.class)
//                .formLogin(flc -> flc.disable())
//                .httpBasic(withDefaults())
//                .build();


        return  http
                .csrf(csrfConfig -> csrfConfig
                        .ignoringRequestMatchers("/jobportal/actuator/**")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests( requests -> {
                            publicPaths.forEach(path -> requests.requestMatchers(path).permitAll());
                            adminPaths.forEach(path -> requests.requestMatchers(path).hasRole("ADMIN"));
                            employerPaths.forEach(path -> requests.requestMatchers(path).hasRole("EMPLOYER"));
                            jobSeekerPaths.forEach(path -> requests.requestMatchers(path).hasRole("JOB_SEEKER"));
                            securedPaths.forEach(path -> requests.requestMatchers(path).authenticated());
                            requests.anyRequest().denyAll();
                        }
                )
                .addFilterBefore( new JwtTokenValidatorFilter(publicPaths), BasicAuthenticationFilter.class)
                .formLogin(flc -> flc.disable())
                .httpBasic(htb -> htb.disable())
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Access Denied\", \"message\": \"You don't have permission to access this resource\"}");
                        })
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                            response.setContentType("application/json");
//                            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Authentication required\"}");
//                        })
                )
                .build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();


//        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
//        config.setAllowedMethods(Collections.singletonList("*"));
//        config.setAllowedHeaders(Collections.singletonList("*"));
//        config.setAllowCredentials(true);
//        config.setMaxAge(3600L);

        config.setAllowedOrigins(corsProperties.getAllowedOrigins());
        config.setAllowedMethods(corsProperties.getAllowedMethods());
        config.setAllowedHeaders(corsProperties.getAllowedHeaders());
        config.setAllowCredentials(corsProperties.getAllowCredentials());
        config.setMaxAge(corsProperties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*Lesson 121:
    previously username,password and roles are  defined in application.properties file, they are also stored as  InMemoryUserDetailsManager but
    now we are defining them in the code itself using UserDetailsService bean.  This is another alternative for demo purposes.
    "Yani application.properties 1. secenek ama busekilde de yapmak mumkun."
     */

    /* Lesson 132 : this method is completely remarked */

    /*
    @Bean
    public UserDetailsService userDetailsService() {



        //String password1 = passwordEncoder().encode("Madan@123");
        //String password2 = passwordEncoder().encode("Admin@123");

        //System.out.println("Password1: " + password1);
        //System.out.println("Password2: " + password2);



        String password1 = "$2a$10$3CueaUIeaoITrC4tjqFTw.pm6sOMzIyDBBoiHpiApQW.bZqayI09e";
        String password2 = "$2a$10$qZHPebPLeHRUZmVhfXHSf.gF3LMPnfXyDtj7LCQaT7q3WwH/ddoHW";




        var user1 = User.builder()
                .username("madan")
                .password(password1)
                .roles("USER")
                .build();

        var user2 = User.builder()
                .username("admin")
                .password(password2)
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user1, user2);

    }

     */

/*
    @Bean
    public AuthenticationManager authenticationManager() {

        // Lesson 132 : these three lines are remarked  , instead  just this line "return new ProviderManager();" is put
        //return new ProviderManager();



        var authenticationProvider = new DaoAuthenticationProvider(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);




    }
    */


   @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {

        return new ProviderManager(authenticationProvider);

    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return  new HaveIBeenPwnedRestApiPasswordChecker();
    }


//    @Bean
//    public static GrantedAuthorityDefaults grantedAuthorityDefaults() {
//        return new GrantedAuthorityDefaults("MYPREFIX_");
//    }





}
