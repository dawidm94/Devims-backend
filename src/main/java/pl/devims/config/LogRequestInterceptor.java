package pl.devims.config;

import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import pl.devims.annotation.LogRequest;
import pl.devims.dao.EsorUserDao;
import pl.devims.dao.RequestHistoryDao;
import pl.devims.entity.RequestHistory;
import pl.devims.util.AuthTokenUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class LogRequestInterceptor implements AsyncHandlerInterceptor {

    private EsorUserDao esorUserDao;
    private RequestHistoryDao requestHistoryDao;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (handler instanceof HandlerMethod) {
            LogRequest isLogRequest = ((HandlerMethod) handler).getMethodAnnotation(LogRequest.class);
            if (isLogRequest == null) {
                isLogRequest = ((HandlerMethod) handler).getMethod().getDeclaringClass()
                        .getAnnotation(LogRequest.class);
            }

            if (isLogRequest != null) {
                saveRequestHistory(request);
            }
        }
        return true;
    }

    private void saveRequestHistory(HttpServletRequest request) {
        RequestHistory requestHistory = new RequestHistory();
        requestHistory.setPath(request.getRequestURI());
        requestHistory.setMethod(request.getMethod());
        requestHistory.setDate(LocalDateTime.now());

        String esorToken = request.getHeader("esor-token");

        if (esorToken != null) {
            esorUserDao.findByLoginIgnoreCase(AuthTokenUtils.getLogin(esorToken))
                    .ifPresent(requestHistory::setUser);
        }
        requestHistoryDao.save(requestHistory);
    }
}
