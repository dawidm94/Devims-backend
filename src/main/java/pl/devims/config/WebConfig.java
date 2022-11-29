package pl.devims.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final LogRequestInterceptor logRequestInterceptor;

    public WebConfig(LogRequestInterceptor logRequestInterceptor) {
        this.logRequestInterceptor = logRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Custom interceptor, add intercept path and exclude intercept path
        registry.addInterceptor(logRequestInterceptor).addPathPatterns("/esor/**");
    }
}
