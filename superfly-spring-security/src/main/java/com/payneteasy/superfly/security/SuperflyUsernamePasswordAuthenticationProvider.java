package com.payneteasy.superfly.security;

import com.payneteasy.superfly.api.OTPType;
import com.payneteasy.superfly.security.authentication.UsernamePasswordAndOtpCheckedToken;
import com.payneteasy.superfly.security.exception.BadOTPValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.payneteasy.superfly.api.SSOService;
import com.payneteasy.superfly.api.SSOUser;
import com.payneteasy.superfly.security.authentication.UsernamePasswordAuthRequestInfoAuthenticationToken;
import com.payneteasy.superfly.security.authentication.UsernamePasswordCheckedToken;

/**
 * {@link AuthenticationProvider} which uses Superfly password authentication
 * to authenticate user.
 * 
 * @author Roman Puchkovskiy
 */
public class SuperflyUsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private SSOService ssoService;

    @Autowired
    public void setSsoService(SSOService ssoService) {
        this.ssoService = ssoService;
    }

    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        if (authentication instanceof UsernamePasswordAuthRequestInfoAuthenticationToken) {
            UsernamePasswordAuthRequestInfoAuthenticationToken authRequest = (UsernamePasswordAuthRequestInfoAuthenticationToken) authentication;
            if (authRequest.getCredentials() == null) {
                throw new BadCredentialsException("Null credentials");
            }
            SSOUser ssoUser = ssoService.authenticate(
                    authRequest.getName(),
                    authRequest.getCredentials().toString(),
                    authRequest.getAuthRequestInfo());
            if (ssoUser == null) {
                throw new BadCredentialsException("Bad credentials");
            }
            if (ssoUser.getActionsMap().isEmpty()) {
                throw new BadCredentialsException("No roles");
            }
            if (!ssoService.checkOtp(ssoUser, authRequest.secondFactory())) {
                throw new BadOTPValueException("Otp check failed!");
            }

            return createAuthentication(ssoUser);
        }
        return null;
    }

    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthRequestInfoAuthenticationToken.class.isAssignableFrom(authentication);
    }

    protected Authentication createAuthentication(SSOUser ssoUser) {
        if (ssoUser.getOtpType() != OTPType.NONE) {
            return new UsernamePasswordAndOtpCheckedToken(ssoUser);
        }
        return new UsernamePasswordCheckedToken(ssoUser);
    }

}
