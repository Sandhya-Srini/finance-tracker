package com.finst.financetracker.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class ApiKeyAuthorizationInterceptor implements HandlerInterceptor {
    public static final String X_API_KEY_HEADER = "X-Api-Key";
    private final String apiKey;

    public ApiKeyAuthorizationInterceptor(@Value("${application.apikey}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        if (requestUri.startsWith("/swagger-ui") || requestUri.startsWith("/v3/api-docs")) {
            return true; // Allow Swagger requests without API key
        }
        try {
            final String requestApiKey = getApiKey(request)
                    .orElseThrow(() -> new ApiKeyAuthenticationException("Api key not found"));

            verify(requestApiKey);

            return true;
        } catch (ApiKeyAuthenticationException e) {
            log.warn("Api key authentication failed: {} [host {}, URI {}]",
                    e.getMessage(), request.getRemoteHost(), request.getRequestURI());
            response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
            return false;
        }
    }

    private Optional<String> getApiKey(final HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(X_API_KEY_HEADER));
    }

    private void verify(final String apiKey) throws ApiKeyAuthenticationException {
        if (this.apiKey.equals(apiKey)) {
            return;
        }

        throw new ApiKeyAuthenticationException("Api key invalid");
    }

    private static class ApiKeyAuthenticationException extends Exception {
        ApiKeyAuthenticationException(final String message) {
            super(message);
        }
    }
}
