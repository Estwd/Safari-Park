package com.estwd.safari.examples.servlets.lion.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;

/**
 * An {@link AuthenticatingRealm} used for testing the Safari-park Lion package.
 *
 * @author Guni Y.
 * @see AuthenticatingRealm
 */
public class FixedPasswordRealm extends AuthenticatingRealm {

    private static final ByteSource MOCK_SALT = ByteSource.Util.bytes("salt");
    private static final String FIXED_PASSWORD = "PASSWORD";

    public static String getFixedPassword() {
        return FIXED_PASSWORD;
    }

    @Override
    public SaltedAuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException
    {
        String hashedPassword = new Sha256Hash(FIXED_PASSWORD, MOCK_SALT, 1024).toHex();

        return new SimpleAuthenticationInfo(token.getPrincipal(), hashedPassword, MOCK_SALT, getClass().getName());
    }
}