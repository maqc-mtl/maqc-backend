package com.maqc.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestInterceptor implements HandlerInterceptor {
	private static final String API_KEY_FIELD = "apiKey";
	private static final String REQUIRED_API_KEY_AUTHORIZATION_MESSAGE = "Authorization and API_KEY are required";
	@Value("${app.config.apiKey}")
	private String apiKey;

	private static final String START_TIME_ATTR = "startTime";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		String requestUri = request.getRequestURI();
		String requestMethods = request.getMethod();
		long startTime = System.currentTimeMillis();
		request.setAttribute(START_TIME_ATTR, startTime);

		if (isPublicEndpoint(requestUri, requestMethods)) {
			return true;
		}

		if (!apiKey.equals(request.getHeader(API_KEY_FIELD))) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, REQUIRED_API_KEY_AUTHORIZATION_MESSAGE);
			return false;
		}

		return true;
	}

	private boolean isPublicEndpoint(String requestUri, String requestMethods) {
		return (requestUri.contains("/swagger-ui/") || requestUri.contains("swagger")
				|| requestUri.contains("/v3/api-docs")
				|| requestUri.contains("/api/v1/auth/")
				|| requestUri.contains("/api/v1/payments/webhook")
				|| requestUri.contains("/api/v1/properties/public/")
				|| requestMethods.equals("OPTIONS"));
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		long startTime = (Long) request.getAttribute(START_TIME_ATTR);
		long endTime = System.currentTimeMillis();
		long executeTime = endTime - startTime;
		response.addHeader("X-Execution-Time", executeTime + "ms");
	}

}
