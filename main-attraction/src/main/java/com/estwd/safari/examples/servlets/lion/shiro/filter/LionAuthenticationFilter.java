package com.estwd.safari.examples.servlets.lion.shiro.filter;

import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An {@link AuthenticationFilter} used for testing the Safari-park Lion package.
 *
 * @author Guni Y.
 * @see AuthenticationFilter
 */
abstract class LionAuthenticationFilter extends AuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setStatus(HttpResponseStatus.UNAUTHORIZED.getCode());
        httpServletResponse.getWriter().print("The session ID is not valid");
        httpServletResponse.getWriter().flush();
        httpServletResponse.getWriter().close();
        return false;
    }

    String getLastPathSegment(ServletRequest request) {
        String pathInfo = ((HttpServletRequest) request).getPathInfo();
        String[] pathSegments = pathInfo.split("/");
        return pathSegments[pathSegments.length - 1];
    }
}
