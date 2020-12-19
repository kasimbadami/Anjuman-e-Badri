package mumineen.connect.app.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import mumineen.connect.app.di.DaggerLoginApiComponent;
import mumineen.connect.app.model.AppApiService;
import mumineen.connect.app.model.ForgotPasswordResponse;
import mumineen.connect.app.model.LoginRequest;
import mumineen.connect.app.utils.AppConstants;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ForgotPasswordViewModel extends ViewModel
{
    @Inject
    AppApiService appApiService;

    private CompositeDisposable disposable= new CompositeDisposable();

    public MutableLiveData<String> validationError = new MutableLiveData<>();
    public MutableLiveData<ForgotPasswordResponse> forgotPasswordResponse = new MutableLiveData<>();
    public MutableLiveData<Boolean> isError = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public ForgotPasswordViewModel()
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

        return true;
    }

    public void proceedToForgotPassword(LoginRequest loginRequest)
    {
        if (validateInputs(loginRequest))
        {
            forgotPassword(loginRequest);
        }
    }

    private void forgotPassword(LoginRequest loginRequest) {
        isLoading.setValue(true);
        isError.setValue(false);

        disposable.add(appApiService.forgotPassword(loginRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ForgotPasswordResponse>() {

                    @Override
                    public void onSuccess(ForgotPasswordResponse response) {

                        if (response!=null)
                        {
                            forgotPasswordResponse.setValue(response);
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
