package org.baeldung.mocks.mockito;

import org.baeldung.mocks.testCase.LoginController;
import org.baeldung.mocks.testCase.LoginDao;
import org.baeldung.mocks.testCase.LoginService;
import org.baeldung.mocks.testCase.UserForm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class LoginControllerIntegrationTest {

    @Mock
    private LoginDao loginDao;

    @Spy
    @InjectMocks
    private LoginService spiedLoginService;

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    @Before
    public void setUp() {
        loginController = new LoginController();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void assertThatNoMethodHasBeenCalled() {
        loginController.login(null);
        // no method called
        Mockito.verifyZeroInteractions(loginService);
    }

    @Test
    public void assertTwoMethodsHaveBeenCalled() {
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        Mockito.when(loginService.login(userForm)).thenReturn(true);

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        Mockito.verify(loginService).login(userForm);
        Mockito.verify(loginService).setCurrentUser("foo");
    }

    @Test
    public void assertOnlyOneMethodHasBeenCalled() {
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        Mockito.when(loginService.login(userForm)).thenReturn(false);

        String login = loginController.login(userForm);

        Assert.assertEquals("KO", login);
        Mockito.verify(loginService).login(userForm);
        Mockito.verifyNoMoreInteractions(loginService);
    }

    @Test
    public void mockExceptionThrowing() {
        UserForm userForm = new UserForm();
        Mockito.when(loginService.login(userForm)).thenThrow(IllegalArgumentException.class);

        String login = loginController.login(userForm);

        Assert.assertEquals("ERROR", login);
        Mockito.verify(loginService).login(userForm);
        Mockito.verifyZeroInteractions(loginService);
    }

    @Test
    public void mockAnObjectToPassAround() {
        UserForm userForm = Mockito.when(Mockito.mock(UserForm.class).getUsername()).thenReturn("foo").getMock();
        Mockito.when(loginService.login(userForm)).thenReturn(true);

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        Mockito.verify(loginService).login(userForm);
        Mockito.verify(loginService).setCurrentUser("foo");
    }

    @Test
    public void argumentMatching() {
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        // default matcher
        Mockito.when(loginService.login(Mockito.any(UserForm.class))).thenReturn(true);

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        Mockito.verify(loginService).login(userForm);
        // complex matcher
        Mockito.verify(loginService).setCurrentUser(Mockito.argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(Object argument) {
                return argument instanceof String && ((String) argument).startsWith("foo");
            }
        }));
    }

    @Test
    public void partialMocking() {
        // use partial mock
        loginController.loginService = spiedLoginService;
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        // let service's login use implementation so let's mock DAO call
        Mockito.when(loginDao.login(userForm)).thenReturn(1);

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        // verify mocked call
        Mockito.verify(spiedLoginService).setCurrentUser("foo");
    }
}
