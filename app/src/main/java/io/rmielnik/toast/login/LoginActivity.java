package io.rmielnik.toast.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.rmielnik.toast.R;
import io.rmielnik.toast.calculations.CalculationsActivity;
import io.rmielnik.toast.databinding.ActivityMainBinding;

public class LoginActivity extends AppCompatActivity implements LoginController {

    private static final String[] GREETINGS = new String[]{"Cześć", "Hello", "Halo", "Hola", "привет", "Hi", "Salut"};

    private ActivityMainBinding binding;
    private LoginFormViewModel formViewModel = new LoginFormViewModel();

    private LoginService service = new LoginService();
    private Disposable loginDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setForm(formViewModel);
        binding.setController(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GREETINGS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);
    }

    @Override
    public void login() {
        if (loginDisposable != null && !loginDisposable.isDisposed()) {
            loginDisposable.dispose();
        }

        final String login = formViewModel.login.get();
        final String password = formViewModel.password.get();
        final String greeting = formViewModel.greeting.get();

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

                        formViewModel.inProgress.set(shouldBlockUi);

                        if (loginResult.isSuccess()) {
                            Toast.makeText(LoginActivity.this, "Successful login: " + loginResult.getToken(), Toast.LENGTH_SHORT).show();
                            goToCalculationsActivity(greeting);
                        }

                        if (loginResult.getError() != null) {
                            Toast.makeText(LoginActivity.this, "Error occurred, cause: " + loginResult.getError().getMessage(), Toast.LENGTH_SHORT).show();
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

    private void goToCalculationsActivity(String greeting) {
        Intent startIntent = CalculationsActivity.getStartIntent(this, greeting);
        startActivity(startIntent);
    }

    @Override
    protected void onDestroy() {
        if (loginDisposable != null && !loginDisposable.isDisposed()) {
            loginDisposable.dispose();
        }
        super.onDestroy();
    }
}
