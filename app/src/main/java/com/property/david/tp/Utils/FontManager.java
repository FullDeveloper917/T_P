package com.property.david.tp.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by MH on 7/3/2017.
 */

public class FontManager {
    public static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "fontawesome-webfont.ttf",
            GOTHAM = ROOT + "gotham-book.ttf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }

    public static void setFontType(View v, Typeface typeface) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                setFontType(child, typeface);
            }
        }
        if (v instanceof TextView) {
            TextView textView = (TextView) v;
            textView.setTypeface(typeface, textView.getTypeface().getStyle());

        }
        if (v instanceof Button) {
            Button button = (Button) v;
            button.setTypeface(typeface, button.getTypeface().getStyle());
        }
    }
}
