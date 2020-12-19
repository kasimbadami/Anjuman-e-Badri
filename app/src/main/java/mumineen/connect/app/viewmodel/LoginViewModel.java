package mumineen.connect.app.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import javax.inject.Inject;
import mumineen.connect.app.di.DaggerLoginApiComponent;
import mumineen.connect.app.model.AppApiService;
import mumineen.connect.app.model.LoginRequest;
import mumineen.connect.app.model.LoginResponse;
import mumineen.connect.app.utils.AppConstants;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends ViewModel
{
    @Inject
    AppApiService appApiService;

    private CompositeDisposable disposable= new CompositeDisposable();

    public MutableLiveData<String> validationError = new MutableLiveData<>();
    public MutableLiveData<LoginResponse> loginResponse = new MutableLiveData<>();
    public MutableLiveData<Boolean> isError = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

   public LoginViewModel()
   {
       DaggerLoginApiComponent.create().inject(this);
   }


    private boolean validateInputs(LoginRequest loginRequest)
    {
        if(loginRequest.getUsername()==null || loginRequest.getUsername().isEmpty())
        {
            validationError.setValue(AppConstants.EMPTY_ITS_ID);
            return false;
        }

        if(loginRequest.getPassword()==null || loginRequest.getPassword().isEmpty())
        {
            validationError.setValue(AppConstants.EMPTY_PASSWORD);
            return false;
        }

        return true;
    }

    public void proceedToLogin(LoginRequest loginRequest)
    {
        if (validateInputs(loginRequest))
        {
            login(loginRequest);
        }
    }

    private void login(LoginRequest loginRequest) {
        isLoading.setValue(true);
        isError.setValue(false);

        disposable.add(appApiService.login(loginRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<LoginResponse>() {

                    @Override
                    public void onSuccess(LoginResponse response) {

                        if (loginResponse!=null)
                        {
                            loginResponse.setValue(response);
                            isLoading.setValue(false);
                            isError.setValue(false);
                        }
                        else
                        {
                            isLoading.setValue(false);
                            isError.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading.setValue(false);
                        isError.setValue(true);
                    }
                }));
    }

    @Override
    public void onCleared()
    {
        super.onCleared();
        disposable.clear();
    }
}
