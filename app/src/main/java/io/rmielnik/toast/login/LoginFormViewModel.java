package io.rmielnik.toast.login;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginFormViewModel {

    public interface LoginCallback {
        void onLoginSuccess(String token, String greeting);

        void onLoginError(String message);
    }

    public final ObservableField<String> login = new ObservableField<>();
    public final ObservableField<String> password = new ObservableField<>();
    public final ObservableField<String> greeting = new ObservableField<>();
    public final ObservableBoolean inProgress = new ObservableBoolean();

    private LoginService service = new LoginService();
    private Disposable loginDisposable;
    private LoginCallback loginCallback;

    public void setLoginCallback(LoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }

    public void logIn() {
        if (loginDisposable != null && !loginDisposable.isDisposed()) {
            loginDisposable.dispose();
        }

        final String login = this.login.get();
        final String password = this.password.get();
        final String greeting = this.greeting.get();

        loginDisposable = service.login(login, password)
                .subscribeOn(Schedulers.io())
                .map(LoginResult::success)
                .startWith(LoginResult.inProgress())
                .onErrorReturn(LoginResult::error)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<LoginResult>() {
                    @Override
                    public void onNext(@NonNull LoginResult loginResult) {
                        boolean shouldBlockUi = loginResult.isInProgress() || loginResult.isSuccess();
                        inProgress.set(shouldBlockUi);

                        if (loginResult.isSuccess()) {
                            if (loginCallback != null) {
                                loginCallback.onLoginSuccess(loginResult.getToken(), greeting);
                            }
                        }

                        if (loginResult.getError() != null) {
                            if (loginCallback != null) {
                                loginCallback.onLoginError(loginResult.getError().getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
