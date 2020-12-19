package mumineen.connect.app.di;

import mumineen.connect.app.viewmodel.ForgotPasswordViewModel;
import mumineen.connect.app.viewmodel.LoginViewModel;
import dagger.Component;

@Component(modules = AppApiModule.class)
public interface LoginApiComponent {

    void inject(LoginViewModel loginViewModel);
    void inject(ForgotPasswordViewModel forgotPasswordViewModel);
}
