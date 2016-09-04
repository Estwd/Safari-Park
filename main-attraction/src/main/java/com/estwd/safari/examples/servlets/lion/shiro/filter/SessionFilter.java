package com.estwd.safari.examples.servlets.lion.shiro.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * An {@link AuthenticationFilter} used for testing the Safari-park Lion package.
 *
 * @author Guni Y.
 * @see AuthenticationFilter
 */
public class SessionFilter extends LionAuthenticationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        String sessionId = getLastPathSegment(request);

        //Fetch the session from the cache and create a subject with it
        Subject requestSubject = new Subject.Builder().sessionId(sessionId).buildSubject();

        if (!requestSubject.isAuthenticated())
            return false;

        ThreadContext.bind(requestSubject);

        return true;
    }
}
