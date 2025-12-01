// java
package es.unex.mdai.FitReserve.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AutenticacionInterceptor autenticacionInterceptor;

    @Autowired
    public WebConfig(AutenticacionInterceptor autenticacionInterceptor) {
        this.autenticacionInterceptor = autenticacionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacionInterceptor)
                .addPathPatterns("/admin/**", "/cliente/**", "/entrenador/**")
                .excludePathPatterns("/login", "/login/**", "/css/**", "/js/**", "/images/**", "/webjars/**", "/error");
    }
}