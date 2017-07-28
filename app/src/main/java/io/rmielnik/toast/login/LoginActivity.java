package io.rmielnik.toast.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import io.rmielnik.toast.R;
import io.rmielnik.toast.calculations.CalculationsActivity;
import io.rmielnik.toast.databinding.ActivityMainBinding;

public class LoginActivity extends AppCompatActivity implements LoginFormViewModel.LoginCallback {

    private static final String[] GREETINGS = new String[]{"Cześć", "Hello", "Halo", "Hola", "привет", "Hi", "Salut"};

    private ActivityMainBinding binding;
    private LoginFormViewModel formViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        formViewModel = new LoginFormViewModel();
        formViewModel.setLoginCallback(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewModel(formViewModel);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GREETINGS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);
    }


    private void goToCalculationsActivity(String greeting) {
        Intent startIntent = CalculationsActivity.getStartIntent(this, greeting);
        startActivity(startIntent);
    }

    @Override
    protected void onDestroy() {
        formViewModel.setLoginCallback(null);
        super.onDestroy();
    }

    @Override
    public void onLoginSuccess(String token, String greeting) {
        Toast.makeText(LoginActivity.this, "Successful login: " + token, Toast.LENGTH_SHORT).show();
        goToCalculationsActivity(greeting);
    }

    @Override
    public void onLoginError(String message) {
        Toast.makeText(LoginActivity.this, "Error occurred, cause: " + message, Toast.LENGTH_SHORT).show();
    }
}
