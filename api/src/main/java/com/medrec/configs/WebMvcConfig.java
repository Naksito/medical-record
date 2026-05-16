package com.medrec.configs;

import com.medrec.interceptors.AuthInterceptor;
import com.medrec.interceptors.LoggedInInterceptop;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    private final LoggedInInterceptop loggedInInterceptop;

    public WebMvcConfig(AuthInterceptor authInterceptor, LoggedInInterceptop loggedInInterceptop) {
        this.authInterceptor = authInterceptor;
        this.loggedInInterceptop = loggedInInterceptop;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor);
        registry.addInterceptor(loggedInInterceptop)
            .addPathPatterns("/api/pages/login", "/api/pages/register");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:4200", "http://tpopov:32000", "http://100.80.222.26:32000")
            .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
