package io.rmielnik.toast;

import android.app.Application;
import android.databinding.DataBindingUtil;

import io.rmielnik.toast.bindings.CustomTextViewBindingAdapter;

public class App extends Application implements android.databinding.DataBindingComponent {

    @Override
    public void onCreate() {
        super.onCreate();
        DataBindingUtil.setDefaultComponent(this);
    }


    @Override
    public CustomTextViewBindingAdapter getCustomTextViewBindingAdapter() {
        return new CustomTextViewBindingAdapter();
    }
}
