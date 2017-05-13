package io.rmielnik.toast.login;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

public class LoginFormViewModel {

    public final ObservableField<String> login = new ObservableField<>();
    public final ObservableField<String> password = new ObservableField<>();
    public final ObservableField<String> greeting = new ObservableField<>();
    public final ObservableBoolean inProgress = new ObservableBoolean();

}
