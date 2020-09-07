package com.payneteasy.superfly.dao;

import java.util.Date;
import java.util.List;

import com.googlecode.jdbcproc.daofactory.annotation.AStoredProcedure;
import com.payneteasy.superfly.model.RoutineResult;
import com.payneteasy.superfly.model.SSOSession;
import com.payneteasy.superfly.model.ui.session.UISession;

/**
 * DAO for sessions.
 * 
 * @author Roman Puchkovskiy
 */
public interface SessionDao {
    /**
     * Returns a list of all invalid sessions (these are sessions which
     * have not yet expired themselves but whose actions have expired).
     *
     * @return invalid sessions
     */
    @AStoredProcedure(name = "ui_get_invalid_sessions")
    List<UISession> getInvalidSessions();

    /**
     * Returns a list of all expired sessions.
     *
     * @return expired sessions
     */
    @AStoredProcedure(name = "ui_get_expired_sessions")
    List<UISession> getExpiredSessions();

    /**
     * Makes invalid sessions expired.
     *
     * @return routine result
     */
    @AStoredProcedure(name = "ui_expire_invalid_sessions")
    RoutineResult expireInvalidSessions();

    /**
     * Deletes expired sessions. 'Expired' sessions are those for which any
     * of the following conditions is true:
     * 1. session was expired explicitly
     * 2. session start date is before the given date
     * If the specified date is null, only item 1 applies.
     *
     * @param beforeWhat    date before which all sessions are considered as
     *                         expired (ignored if null)
     * @return deleted sessions
     */
    @AStoredProcedure(name = "ui_delete_expired_sessions")
    List<UISession> deleteExpiredSessions(Date beforeWhat);

    /**
     * Obtains a valid SSO session by its identifier if such session exists,
     * otherwise returns null.
     *
     * @param ssoSessionIdentifier  identifier of an SSO session
     * @return session or null
     */
    @AStoredProcedure(name = "get_valid_sso_session")
    SSOSession getValidSSOSession(String ssoSessionIdentifier);

    /**
     * Creates an SSO session for a user with given name.
     *
     * @param username  user name
     * @return SSO session
     */
    @AStoredProcedure(name = "create_sso_session")
    SSOSession createSSOSession(String username, String uniqueToken);

    /**
     * Touches sessions: that is, updates their access time to avoid
     * removal. If a session was issued by an SSO session, the latter
     * is touched too.
     *
     * @param sessionIds    IDs of sessions to touch (comma-separated list)
     */
    @AStoredProcedure(name = "touch_sessions")
    void touchSessions(String sessionIds);

    /**
     * Deletes SSO sessions which have been inactive for the
     * given amount of time.
     *
     * @param maxAgeSeconds max SSO session age in seconds
     */
    @AStoredProcedure(name = "delete_expired_sso_sessions")
    void deleteExpiredSSOSessions(int maxAgeSeconds);

    /**
     * Deletes tokens which have expired.
     *
     * @param maxSubsystemTokenAgeSeconds max subsystem token age in seconds
     */
    @AStoredProcedure(name = "delete_expired_tokens")
    void deleteExpiredTokens(int maxSubsystemTokenAgeSeconds);

    /**
     * Deletes SSO session by its identifier.
     *
     * @param ssoSessionIdentifier  identifier of SSO session
     */
    @AStoredProcedure(name = "delete_sso_session")
    void deleteSSOSession(String ssoSessionIdentifier);
}
