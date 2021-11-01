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
        corsConfiguration.addAllowedOrigin("*");    //同源配置，*表示任何请求都视为同源，若需指定ip和端口可以改为如“localhost：8080”，多个以“，”分隔；
        corsConfiguration.addAllowedHeader("*");//header，允许哪些header，本案中使用的是token，此处可将*替换为token；
        corsConfiguration.addAllowedMethod("*");    //允许的请求方法，PSOT、GET等
        ((UrlBasedCorsConfigurationSource) source).registerCorsConfiguration("/**",corsConfiguration); //配置允许跨域访问的url
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
                .cors().configurationSource(CorsConfigurationSource())//允许跨域访问
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/user/findAll").hasRole ("ADMIN")
                .antMatchers(HttpMethod.POST, "/user/deleteById").hasRole ("ADMIN")
                .antMatchers(HttpMethod.GET, "/user/getRoleList").hasRole ("ADMIN")
                .antMatchers(HttpMethod.POST, "/user/setUserAdminRole").hasRole ("ADMIN")
                .antMatchers("/user/**").hasAnyRole("ADMIN","USER")
                //调用其他接口需要认证操作
                .anyRequest().authenticated()
                //权限控制

                .and()
                //添加认证过滤器
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                //添加授权过滤器
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))

                // 不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                // 关闭CSRF跨域
                .csrf().disable();

    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        /* 在这里配置security放行的请求 */
        // 统一静态资源
        web.ignoring().antMatchers("/**/*.gif", "/**/*.png", "/**/*.jpg", "/**/*.html", "/**/*.js", "/**/*.css", "/**/*.ico", "/webjars/**");

        // 注册请求
        web.ignoring().mvcMatchers("/user/register");
    }

}