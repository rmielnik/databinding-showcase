package io.rmielnik.toast.calculations;

import android.databinding.ObservableField;

public class CalculationsViewModel {

    public final ObservableField<String> aResult = new ObservableField<>("Idle");
    public final ObservableField<String> bResult = new ObservableField<>("Idle");
    public final ObservableField<String> greeting = new ObservableField<>();

}
