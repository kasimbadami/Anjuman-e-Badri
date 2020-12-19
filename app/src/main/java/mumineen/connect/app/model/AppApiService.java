package mumineen.connect.app.model;

import javax.inject.Inject;

import mumineen.connect.app.di.DaggerApiComponent;
import io.reactivex.Single;


public class AppApiService {

    @Inject
    AppApi api;

    public AppApiService()
    {
        DaggerApiComponent.create().inject(this);
    }

    // Function to call login API
    public Single<LoginResponse> login(LoginRequest loginRequest)
    {
        return api.login(loginRequest);
    }

    // Function to call Forgot-Password API
    public Single<ForgotPasswordResponse> forgotPassword(LoginRequest loginRequest)
    {
        return api.forgotPassword(loginRequest);
    }

}
