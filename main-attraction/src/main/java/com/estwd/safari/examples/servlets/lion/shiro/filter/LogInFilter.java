package com.estwd.safari.examples.servlets.lion.shiro.filter;

import com.estwd.safari.examples.servlets.lion.shiro.FixedPasswordRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * An {@link AuthenticationFilter} used for testing the Safari-park Lion package.
 *
 * @author Guni Y.
 * @see AuthenticationFilter
 */
public class LogInFilter extends LionAuthenticationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        String username = getLastPathSegment(request);
        UsernamePasswordToken token =
                new UsernamePasswordToken(username, FixedPasswordRealm.getFixedPassword());
        try {
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException ex) {
            return false;
        }
        return true;
    }
}
