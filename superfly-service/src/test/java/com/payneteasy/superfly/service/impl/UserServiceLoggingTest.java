package com.payneteasy.superfly.service.impl;


import com.payneteasy.superfly.dao.UserDao;
import com.payneteasy.superfly.model.RoutineResult;
import com.payneteasy.superfly.model.ui.user.UICloneUserRequest;
import com.payneteasy.superfly.model.ui.user.UIUser;
import com.payneteasy.superfly.model.ui.user.UIUserForCreate;
import com.payneteasy.superfly.password.NullSaltSource;
import com.payneteasy.superfly.password.PlaintextPasswordEncoder;
import com.payneteasy.superfly.password.SHA256RandomGUIDSaltGenerator;
import com.payneteasy.superfly.policy.account.none.SimpleAccountPolicy;
import com.payneteasy.superfly.policy.create.none.NoneCreateUserStrategy;
import com.payneteasy.superfly.service.NotificationService;
import com.payneteasy.superfly.service.UserService;
import com.payneteasy.superfly.spisupport.HOTPService;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.Collections;

import static org.easymock.EasyMock.*;

public class UserServiceLoggingTest extends AbstractServiceLoggingTest {

    private UserService userService;
    private UserDao userDao;

    @Before
    public void setUp() {
        UserServiceImpl service = new UserServiceImpl();
        userService = service;
        userDao = EasyMock.createStrictMock(UserDao.class);
        service.setUserDao(userDao);
        service.setNotificationService(TrivialProxyFactory.createProxy(NotificationService.class));
        service.setLoggerSink(loggerSink);
        service.setPasswordEncoder(new PlaintextPasswordEncoder());
        service.setSaltSource(new NullSaltSource());
        service.setHotpSaltGenerator(new SHA256RandomGUIDSaltGenerator());
        SimpleAccountPolicy accountPolicy = new SimpleAccountPolicy();
        accountPolicy.setUserService(service);
        service.setAccountPolicy(accountPolicy);
        service.setHotpService(TrivialProxyFactory.createProxy(HOTPService.class));
        service.setCreateUserStrategy(new NoneCreateUserStrategy(service));
    }

    @Test
    public void testCreateUser() throws Exception {
        userDao.createUser(anyObject(UIUserForCreate.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<RoutineResult>() {
            public RoutineResult answer() throws Throwable {
                UIUserForCreate user = (UIUserForCreate) EasyMock.getCurrentArguments()[0];
                user.setId(1L);
                return okResult();
            }
        });
        loggerSink.info(anyObject(Logger.class), eq("CREATE_USER"), eq(true), eq("test-user"));
        EasyMock.replay(loggerSink, userDao);

        UIUserForCreate user = new UIUserForCreate();
        user.setUsername("test-user");
        userService.createUser(user, "subsystem");

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testCreateUserFail() throws Exception {
        userDao.createUser(anyObject(UIUserForCreate.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<RoutineResult>() {
            public RoutineResult answer() throws Throwable {
                UIUserForCreate user = (UIUserForCreate) EasyMock.getCurrentArguments()[0];
                user.setId(1L);
                return failureResult();
            }
        });
        loggerSink.info(anyObject(Logger.class), eq("CREATE_USER"), eq(false), eq("test-user"));
        EasyMock.replay(loggerSink, userDao);

        UIUserForCreate user = new UIUserForCreate();
        user.setUsername("test-user");
        userService.createUser(user, "subsystem");

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testUpdateUser() throws Exception {
        userDao.updateUser(anyObject(UIUser.class));
        EasyMock.expectLastCall().andReturn(okResult());
        loggerSink.info(anyObject(Logger.class), eq("UPDATE_USER"), eq(true), eq("test-user"));
        EasyMock.replay(loggerSink, userDao);

        UIUser user = new UIUser();
        user.setUsername("test-user");
        userService.updateUser(user);

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testUpdateUserFail() throws Exception {
        userDao.updateUser(anyObject(UIUser.class));
        EasyMock.expectLastCall().andReturn(failureResult());
        loggerSink.info(anyObject(Logger.class), eq("UPDATE_USER"), eq(false), eq("test-user"));
        EasyMock.replay(loggerSink, userDao);

        UIUser user = new UIUser();
        user.setUsername("test-user");
        userService.updateUser(user);

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testDeleteUser() throws Exception {
        userDao.deleteUser(anyLong());
        EasyMock.expectLastCall().andReturn(okResult());
        loggerSink.info(anyObject(Logger.class), eq("DELETE_USER"), eq(true), eq("1"));
        EasyMock.replay(loggerSink, userDao);

        userService.deleteUser(1L);

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testDeleteUserFail() throws Exception {
        userDao.deleteUser(anyLong());
        EasyMock.expectLastCall().andReturn(failureResult());
        loggerSink.info(anyObject(Logger.class), eq("DELETE_USER"), eq(false), eq("1"));
        EasyMock.replay(loggerSink, userDao);

        userService.deleteUser(1L);

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testLockUser() throws Exception {
        userDao.lockUser(anyLong());
        EasyMock.expectLastCall().andReturn(okResult());
        loggerSink.info(anyObject(Logger.class), eq("LOCK_USER"), eq(true), eq("1"));
        EasyMock.replay(loggerSink, userDao);

        userService.lockUser(1L);

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testLockUserFail() throws Exception {
        userDao.lockUser(anyLong());
        EasyMock.expectLastCall().andReturn(failureResult());
        loggerSink.info(anyObject(Logger.class), eq("LOCK_USER"), eq(false), eq("1"));
        EasyMock.replay(loggerSink, userDao);

        userService.lockUser(1L);

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testUnlockUser() throws Exception {
        userDao.unlockUser(anyLong());
        EasyMock.expectLastCall().andReturn(okResult());
        loggerSink.info(anyObject(Logger.class), eq("UNLOCK_USER"), eq(true), eq("1"));
        EasyMock.replay(loggerSink, userDao);

        userService.unlockUser(1L, false);

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testCloneUser() throws Exception {
        EasyMock.expect(userDao.cloneUser(anyObject(UICloneUserRequest.class))).andAnswer(new IAnswer<RoutineResult>() {
            public RoutineResult answer() throws Throwable {
                UICloneUserRequest user = (UICloneUserRequest) EasyMock.getCurrentArguments()[0];
                user.setId(1L);
                return okResult();
            }
        });
        loggerSink.info(anyObject(Logger.class), eq("CLONE_USER"), eq(true), eq("1->new-user"));
        EasyMock.replay(loggerSink, userDao);

        userService.cloneUser(1L, "new-user", "new-password", "new-email", "new key", null);

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testCloneUserFail() throws Exception {
        EasyMock.expect(userDao.cloneUser(anyObject(UICloneUserRequest.class))).andReturn(failureResult());
        loggerSink.info(anyObject(Logger.class), eq("CLONE_USER"), eq(false), eq("1->new-user"));
        EasyMock.replay(loggerSink, userDao);

        userService.cloneUser(1L, "new-user", "new-password", "new-email", "new key", null);

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testChangeUserRoles() throws Exception {
        userDao.changeUserRoles(anyLong(), anyObject(String.class), anyObject(String.class), anyObject(String.class));
        EasyMock.expectLastCall().andReturn(okResult());
        loggerSink.info(anyObject(Logger.class), eq("CHANGE_USER_ROLES"), eq(true), eq("1"));
        EasyMock.replay(loggerSink, userDao);

        userService.changeUserRoles(1L, Collections.<Long>emptyList(), Collections.<Long>emptyList(), Collections.<Long>emptyList());

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testChangeUserRolesFail() throws Exception {
        userDao.changeUserRoles(anyLong(), anyObject(String.class), anyObject(String.class), anyObject(String.class));
        EasyMock.expectLastCall().andReturn(failureResult());
        loggerSink.info(anyObject(Logger.class), eq("CHANGE_USER_ROLES"), eq(false), eq("1"));
        EasyMock.replay(loggerSink, userDao);

        userService.changeUserRoles(1L, Collections.<Long>emptyList(), Collections.<Long>emptyList(), Collections.<Long>emptyList());

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testChangeUserRoleActions() throws Exception {
        userDao.changeUserRoleActions(anyLong(), anyObject(String.class), anyObject(String.class));
        EasyMock.expectLastCall().andReturn(okResult());
        loggerSink.info(anyObject(Logger.class), eq("CHANGE_USER_ROLE_ACTIONS"), eq(true), eq("1"));
        EasyMock.replay(loggerSink, userDao);

        userService.changeUserRoleActions(1L, Collections.<Long>emptyList(), Collections.<Long>emptyList());

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testChangeUserRoleActionsFail() throws Exception {
        userDao.changeUserRoleActions(anyLong(), anyObject(String.class), anyObject(String.class));
        EasyMock.expectLastCall().andReturn(failureResult());
        loggerSink.info(anyObject(Logger.class), eq("CHANGE_USER_ROLE_ACTIONS"), eq(false), eq("1"));
        EasyMock.replay(loggerSink, userDao);

        userService.changeUserRoleActions(1L, Collections.<Long>emptyList(), Collections.<Long>emptyList());

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testSuspendUser() throws Exception {
        userDao.suspendUser(anyLong());
        EasyMock.expectLastCall().andReturn(okResult());
        loggerSink.info(anyObject(Logger.class), eq("SUSPEND_USER"), eq(true), eq("1"));
        EasyMock.replay(loggerSink, userDao);

        userService.suspendUser(1L);

        EasyMock.verify(loggerSink);
    }

    @Test
    public void testSuspendUserFail() throws Exception {
        userDao.suspendUser(anyLong());
        EasyMock.expectLastCall().andReturn(failureResult());
        loggerSink.info(anyObject(Logger.class), eq("SUSPEND_USER"), eq(false), eq("1"));
        EasyMock.replay(loggerSink, userDao);

        userService.suspendUser(1L);

        EasyMock.verify(loggerSink);
    }

}
