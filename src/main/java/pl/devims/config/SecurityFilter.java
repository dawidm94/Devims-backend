package pl.devims.config;

import org.apache.catalina.connector.RequestFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.devims.service.TokenService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SecurityFilter implements Filter {

    @Autowired
    TokenService tokenService;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (
                ((RequestFacade) request).getServletPath().equals("/testLogin")
                || ((RequestFacade) request).getServletPath().equals("/generateFacebookLoginUrl")
                || ((RequestFacade) request).getServletPath().equals("/forwardLogin")
                || ((RequestFacade) request).getServletPath().equals("/ouath/facebook")
                || ((RequestFacade) request).getServletPath().equals("/isTokenValid")
                || isLoggedIn(request)) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Not enough credentials.");
        }
    }

    private boolean isLoggedIn(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Object tokenValue = request.getSession().getAttribute("token");
        return tokenValue != null && tokenService.checkIfTokenIsValid(tokenValue.toString());
    }

    @Override
    public void destroy() {
    }
}
