package io.rmielnik.toast.login;

import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class LoginService {

    public Observable<String> login(final String login, final String password) {
        return Observable.fromCallable(() -> {
            if (TextUtils.isEmpty(login) || TextUtils.isEmpty(password)) {
                throw new IllegalArgumentException("no data provided");
            }
            return "Auth token for: " + login;
        })
                .delay(4, TimeUnit.SECONDS);
    }

}
