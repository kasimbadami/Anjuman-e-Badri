package mumineen.connect.app.model;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AppApi
{
    @POST("common_login.php")
    Single<LoginResponse> login(@Body LoginRequest request);

    @POST("common_forgot_pass.php")
    Single<ForgotPasswordResponse> forgotPassword(@Body LoginRequest request);
}
