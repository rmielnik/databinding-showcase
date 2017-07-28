package io.rmielnik.toast.calculations;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.rmielnik.toast.R;
import io.rmielnik.toast.databinding.ActivityExampleCalculationsBinding;

public class CalculationsActivity extends AppCompatActivity {

    public static final String EXTRA_GREETING = "extra.greeting";

    public static Intent getStartIntent(Context context, String greeting) {
        Intent intent = new Intent(context, CalculationsActivity.class);
        intent.putExtra(EXTRA_GREETING, greeting);
        return intent;
    }

    private ActivityExampleCalculationsBinding binding;
    private CalculationsViewModel model = new CalculationsViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String greeting = getIntent().getStringExtra(EXTRA_GREETING);
        model.greeting.set(greeting);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_example_calculations);
        binding.setModel(model);
    }
}
