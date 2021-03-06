package com.scutsehm.openplatform.springSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("userServiceImpl")
    private UserDetailsService userDetailService;

    @Autowired
    private JWTAccessDeniedHandler JWTAccessDeniedHandler;

    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager getauthenticationManager() throws Exception {
        return  authenticationManager();
    }
    private CorsConfigurationSource CorsConfigurationSource() {
        CorsConfigurationSource source =   new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");    //???????????????*????????????????????????????????????????????????ip???????????????????????????localhost???8080?????????????????????????????????
        corsConfiguration.addAllowedHeader("*");//header???????????????header????????????????????????token???????????????*?????????token???
        corsConfiguration.addAllowedMethod("*");    //????????????????????????PSOT???GET???
        ((UrlBasedCorsConfigurationSource) source).registerCorsConfiguration("/**",corsConfiguration); //???????????????????????????url
        return source;
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailService)
                .passwordEncoder(passwordEncoder());
    }


    @Override
    protected void configure(HttpSecurity http)throws Exception{
        http    .exceptionHandling()
                .accessDeniedHandler(JWTAccessDeniedHandler)

                .and()
                .cors().configurationSource(CorsConfigurationSource())//??????????????????
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/user/findAll").hasRole ("ADMIN")
                .antMatchers(HttpMethod.POST, "/user/deleteById").hasRole ("ADMIN")
                .antMatchers(HttpMethod.GET, "/user/getRoleList").hasRole ("ADMIN")
                .antMatchers(HttpMethod.POST, "/user/setUserAdminRole").hasRole ("ADMIN")
                .antMatchers("/user/**").hasAnyRole("ADMIN","USER")
                //????????????????????????????????????
                .anyRequest().authenticated()
                //????????????

                .and()
                //?????????????????????
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                //?????????????????????
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))

                // ?????????session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                // ??????CSRF??????
                .csrf().disable();

    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        /* ???????????????security??????????????? */
        // ??????????????????
        web.ignoring().antMatchers("/**/*.gif", "/**/*.png", "/**/*.jpg", "/**/*.html", "/**/*.js", "/**/*.css", "/**/*.ico", "/webjars/**");

        // ????????????
        web.ignoring().mvcMatchers("/user/register");
    }

}