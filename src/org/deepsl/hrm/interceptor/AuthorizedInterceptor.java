package org.deepsl.hrm.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deepsl.hrm.domain.User;
import org.deepsl.hrm.util.common.HrmConstants;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

import static org.deepsl.hrm.util.common.HrmConstants.USER_SESSION;

/** 
 * 判断用户权限的Spring MVC的拦截器
 */
public class AuthorizedInterceptor  implements HandlerInterceptor {

	/** 定义不需要拦截的请求 */
	private static final String[] IGNORE_URI = {"/loginForm", "/login","/404.html"};

	 /**
     * 该方法需要preHandle方法的返回值为true时才会执行。
     * 该方法将在整个请求完成之后执行，主要作用是用于清理资源。
     */
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception exception)
			throws Exception {

	}

	 /**
     * 这个方法在preHandle方法返回值为true的时候才会执行。
     * 执行时间是在处理器进行处理之 后，也就是在Controller的方法调用之后执行。
     */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler, ModelAndView mv) throws Exception {

	}

	 /**
     * preHandle方法是进行处理器拦截用的，该方法将在Controller处理之前进行调用，
     * 当preHandle的返回值为false的时候整个请求就结束了。
     * 如果preHandle的返回值为true，则会继续执行postHandle和afterCompletion。
     */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
/*		*//** 获得请求的ServletPath *//*
		String servletPath = request.getServletPath();
		*//**  判断请求是否需要拦截 *//*
        List<String> strings = Arrays.asList(IGNORE_URI);
        if (strings.contains(servletPath)) {
            return true;
        }
        String requestURI = request.getRequestURI();
        if (requestURI.endsWith(".css") || requestURI.endsWith(".js")
                || requestURI.endsWith(".gif") || requestURI.endsWith(".png")
                || requestURI.endsWith(".jpg")) {
            return true;
        }

        *//** 如果需要 则拦截请求 *//*
        if (request.getSession().getAttribute(USER_SESSION) == null) {
            request.getRequestDispatcher("loginForm").forward(request, response);
        }*/
        return true;
	}

}
