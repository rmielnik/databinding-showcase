package io.rmielnik.toast.bindings;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class CustomTextViewBindingAdapter {

    private Map<String, Typeface> fontCache;

    public CustomTextViewBindingAdapter() {
        fontCache = new HashMap<>();
    }

    @BindingAdapter({"android:fontFamily", "android:textStyle"})
    public void setFont(TextView tv, String fontName, String fontStyle) {
        Typeface typeface;
        String font = fontName + "-" + fontStyle;

        if (fontCache.containsKey(font)) {
            typeface = fontCache.get(font);
        } else {
            Context context = tv.getContext();
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + font + ".ttf");
            fontCache.put(font, typeface);
        }

        tv.setTypeface(typeface);
    }

}
