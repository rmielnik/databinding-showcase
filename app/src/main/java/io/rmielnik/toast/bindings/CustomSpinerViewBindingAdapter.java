package io.rmielnik.toast.bindings;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class CustomSpinerViewBindingAdapter {

    @BindingAdapter("selectedValue")
    public static void setSelectedValue(Spinner spinner, String newValue) {
        SpinnerAdapter adapter = spinner.getAdapter();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            Object elemAtPosition = adapter.getItem(i);
            if (elemAtPosition instanceof String) {
                String elemValue = (String) elemAtPosition;
                if (elemValue.equalsIgnoreCase(newValue)) {
                    boolean isSelected = spinner.getSelectedItemPosition() == i;
                    if (!isSelected) {
                        spinner.setSelection(i);
                        return;
                    }
                }
            }
        }
    }

    @InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueChange")
    public static String getSelectedValue(Spinner spinner) {
        SpinnerAdapter adapter = spinner.getAdapter();
        int position = spinner.getSelectedItemPosition();
        Object object = adapter.getItem(position);
        return object.toString();
    }

    @BindingAdapter(value = "selectedValueChange")
    public static void bindCountryChanged(Spinner spinner, final InverseBindingListener bindingListener) {
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bindingListener.onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                bindingListener.onChange();
            }
        };
        spinner.setOnItemSelectedListener(listener);
    }

}
