package com.payneteasy.superfly.demo.mock;


import com.payneteasy.superfly.api.AuthenticationRequestInfo;
import com.payneteasy.superfly.api.SSOService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock SSOService implementation.
 * 
 * @author Roman Puchkovskiy
 */
public class SSOServiceMock implements SSOService {

    /**
     * @see SSOService#authenticate(String, String, AuthenticationRequestInfo)
     */
    public SSOUser authenticate(String username, String password,
            AuthenticationRequestInfo authRequestInfo) {
        Map<SSORole, SSOAction[]> actionsMap = new HashMap<SSORole, SSOAction[]>();

        List<SSOAction> adminActions = new ArrayList<SSOAction>();
        adminActions.add(new SSOAction("adminpage1", false));
        adminActions.add(new SSOAction("adminpage2", false));
        actionsMap.put(new SSORole("admin"), adminActions.toArray(new SSOAction[adminActions.size()]));

        List<SSOAction> userActions = new ArrayList<SSOAction>();
        userActions.add(new SSOAction("userpage1", false));
        userActions.add(new SSOAction("userpage2", false));
        actionsMap.put(new SSORole("user"), userActions.toArray(new SSOAction[userActions.size()]));

        Map<String, String> prefs = Collections.emptyMap();

        return new SSOUser(username, Collections.unmodifiableMap(actionsMap), prefs);
    }

    public SSOUser pseudoAuthenticate(String username, String subsystemIdentifier) {
        return null;
    }

    public void sendSystemData(String systemIdentifier,
            ActionDescription[] actionDescriptions) {
    }

    public List<SSOUserWithActions> getUsersWithActions(
            String subsystemIdentifier) {
        List<SSOUserWithActions> users = new ArrayList<SSOUserWithActions>();
        List<SSOAction> actions;

        actions = new ArrayList<SSOAction>();
        actions.add(new SSOAction("adminpage1", false));
        actions.add(new SSOAction("adminpage2", false));
        users.add(new SSOUserWithActions("admin", "example@nohost.df",
                actions.toArray(new SSOAction[actions.size()])));

        actions = new ArrayList<SSOAction>();
        actions.add(new SSOAction("userpage1", false));
        actions.add(new SSOAction("userpage2", false));
        users.add(new SSOUserWithActions("user", "example@nohost.df",
                actions.toArray(new SSOAction[actions.size()])));

        return users;
    }

    public boolean authenticateUsingHOTP(String username, String hotp) {
        return true;
    }

    public void registerUser(String username, String password, String email, String subsystemHint,
            RoleGrantSpecification[] roleGrants, String name, String surname, String secretQuestion,
            String secretAnswer, String publicKey,
            String organization) throws UserExistsException, PolicyValidationException, BadPublicKeyException, MessageSendException {

    }

    public void registerUser(
            UserRegisterRequest registerRequest) throws UserExistsException, PolicyValidationException, BadPublicKeyException, MessageSendException {

    }

    public void registerUser(String s, String s1, String s2, String s3, RoleGrantSpecification[] roleGrantSpecifications, String s4, String s5, String s6, String s7, String s8) throws UserExistsException, PolicyValidationException, BadPublicKeyException, MessageSendException {
    }

    public void changeTempPassword(String userName, String password) {
        // TODO Auto-generated method stub

    }

    public UserDescription getUserDescription(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resetAndSendOTPTable(String s) throws UserNotFoundException, MessageSendException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resetAndSendOTPTable(String subsystemIdentifier,
            String username) throws UserNotFoundException, MessageSendException {

    }

    public void updateUserDescription(UserDescription userDescription) throws UserNotFoundException, BadPublicKeyException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resetPassword(String s, String s1) throws UserNotFoundException, PolicyValidationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resetPassword(PasswordReset reset) throws UserNotFoundException, PolicyValidationException {

    }

    public List<UserStatus> getUserStatuses(List<String> userNames) {
        return null;
    }

    public SSOUser exchangeSubsystemToken(String subsystemToken) {
        return null;
    }

    public void touchSessions(List<Long> sessionIds) {

    }

    public void completeUser(String username) {

    }

    public void changeUserRole(String username, String newRole) {

    }

    public void changeUserRole(String username, String newRole, String subsystemHint) {

    }

    public String getFlagTempPassword(String userName) {
        return null;
    }

}
