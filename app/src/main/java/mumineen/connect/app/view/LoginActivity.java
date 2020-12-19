package mumineen.connect.app.view;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import mumineen.connect.app.MainActivity;
import mumineen.connect.app.R;
import mumineen.connect.app.model.LoginRequest;
import mumineen.connect.app.utils.MyNetworkManager;
import mumineen.connect.app.viewmodel.ForgotPasswordViewModel;
import mumineen.connect.app.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    View loginProgress;
    EditText etPassword;
    TextView tvLblPassword;
    Switch passwordSwitch;
    LoginViewModel loginViewModel;
    ForgotPasswordViewModel forgotPasswordViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        forgotPasswordViewModel = ViewModelProviders.of(this).get(ForgotPasswordViewModel.class);

        Typeface tfCalibri = Typeface.createFromAsset(getAssets(), "font/calibri.ttf");

        TextView tvLblItsId = findViewById(R.id.tvLblItsId);
        tvLblItsId.setTypeface(tfCalibri);
        TextView tvLblDoYouHavePassword = findViewById(R.id.tvLblDoYouHavePassword);
        tvLblDoYouHavePassword.setTypeface(tfCalibri);
        loginProgress = findViewById(R.id.loginProgress);
        final EditText etItsId = findViewById(R.id.etItsId);
        etItsId.setTypeface(tfCalibri);
        tvLblPassword = findViewById(R.id.tvLblPassword);
        tvLblPassword.setTypeface(tfCalibri);
        etPassword = findViewById(R.id.etPassword);
        etPassword.setTypeface(tfCalibri);
        passwordSwitch = findViewById(R.id.passwordSwitch);
        passwordSwitch.setTypeface(tfCalibri);
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setTypeface(tfCalibri);

        // Check if UserName already exist in preferences
        SharedPreferences sp = getSharedPreferences("anjuman.e.badri", Context.MODE_PRIVATE);
        String userName = sp.getString("USER_NAME", "");

        if(userName!=null && !userName.isEmpty())
        {
            etItsId.setText(userName);
        }

        passwordSwitch.setChecked(true);
        passwordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                passwordSwitch.setChecked(isChecked);
                if (isChecked) {
                    // The toggle is enabled
                    passwordSwitch.setText(getResources().getString(R.string.yes));
                    togglePassword(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    passwordSwitch.setText(getResources().getString(R.string.no));
                    togglePassword(View.INVISIBLE);
                }
            }
        });

        btnLogin.setOnClickListener(view -> {
            if(MyNetworkManager.isNetworkConnected(LoginActivity.this))
            {
                try
                {
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.setUsername(etItsId.getText().toString());

                    if(passwordSwitch.isChecked())
                    {
                        SharedPreferences sharedPreferences = getSharedPreferences("anjuman.e.badri", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString("USER_NAME", etItsId.getText().toString()).apply();

                        // User have Password, so proceed to Login
                        loginRequest.setPassword(etPassword.getText().toString());
                        loginViewModel.proceedToLogin(loginRequest);
                    }
                    else
                    {
                        // User don't have Password, so proceed to Forgot-Password
                        forgotPasswordViewModel.proceedToForgotPassword(loginRequest);
                    }
                }
                catch (Exception ex)
                {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        });

        observeLoginViewModel();
        observeForgotPasswordViewModel();
    }

    private void togglePassword(int visibility)
    {
        tvLblPassword.setVisibility(visibility);
        etPassword.setVisibility(visibility);
    }

    // Observe LiveData in LoginViewModel
    private void observeLoginViewModel()
    {
        loginViewModel.loginResponse.observe(this, loginResponse -> {
            loginProgress.setVisibility(View.GONE);
            if (loginResponse!=null)
            {
                if(loginResponse.getWeb_url()!=null && !(loginResponse.getWeb_url().isEmpty()))
                {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                    MainActivity.mURL = loginResponse.getWeb_url()+"/"+loginResponse.getHandshake();
                    MainActivity.mRegisterURL = loginResponse.getApp_register_url();

                    SharedPreferences sharedPreferences = getSharedPreferences("anjuman.e.badri", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("URL", MainActivity.mURL).apply();
                   //sharedPreferences.edit().putString("", MainActivity.mRegisterURL).apply();

                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.response_url_missing), Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginViewModel.validationError.observe(this, validationError -> {
            loginProgress.setVisibility(View.GONE);
            if (validationError!=null && !(validationError.isEmpty()))
            {
                Toast.makeText(LoginActivity.this, validationError, Toast.LENGTH_SHORT).show();
            }
        });

        loginViewModel.isLoading.observe(this, isLoading -> {
            if (isLoading)
            {
                loginProgress.setVisibility(View.VISIBLE);
            }
        });

        loginViewModel.isError.observe(this, isError -> {
            if (isError)
            {
                loginProgress.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Observe LiveData for ForgotPassword ViewModel
    private void observeForgotPasswordViewModel()
    {
        forgotPasswordViewModel.forgotPasswordResponse.observe(this, forgotPasswordResponse -> {
            loginProgress.setVisibility(View.GONE);
            if (forgotPasswordResponse!=null && (forgotPasswordResponse.getErrormsg()!=null && !forgotPasswordResponse.getErrormsg().isEmpty()))
            {
                Toast.makeText(LoginActivity.this, forgotPasswordResponse.getErrormsg(), Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }

            passwordSwitch.setChecked(true);
            togglePassword(View.VISIBLE);
        });

        forgotPasswordViewModel.validationError.observe(this, validationError -> {
            loginProgress.setVisibility(View.GONE);
            if (validationError!=null && !(validationError.isEmpty()))
            {
                Toast.makeText(LoginActivity.this, validationError, Toast.LENGTH_SHORT).show();
            }
        });

        forgotPasswordViewModel.isLoading.observe(this, isLoading -> {
            if (isLoading)
            {
                loginProgress.setVisibility(View.VISIBLE);
            }
        });

        forgotPasswordViewModel.isError.observe(this, isError -> {
            if (isError)
            {
                loginProgress.setVisibility(View.GONE);
            }
        });
    }
}