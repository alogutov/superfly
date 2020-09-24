package com.payneteasy.superfly.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import com.payneteasy.superfly.service.LocalSecurityService;

/**
 * Authentication provider which is used to authenticate a local user against
 * standard Superfly DB.
 * 
 * @author Roman Puchkovskiy
 */
public class SuperflyLocalAuthenticationProvider extends
        AbstractUserDetailsAuthenticationProvider {

    private LocalSecurityService localSecurityService;
    private String rolePrefix = "ROLE_";

    @Autowired
    public void setLocalSecurityService(LocalSecurityService localSecurityService) {
        this.localSecurityService = localSecurityService;
    }

    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    @Override
    protected UserDetails retrieveUser(String username,
            UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        String password = (String) authentication.getCredentials();
        String actions[] = localSecurityService.authenticate(username, password);
        if (actions == null) {
            throw new BadCredentialsException("Did not find a user with matching password");
        }
        return new UserDetailsImpl(username, password, actions, rolePrefix);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
    }

}
