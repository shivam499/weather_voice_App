package com.pathantalabs.weatherreportupdate;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;

import java.lang.reflect.Field;

class ReplaceFont {

    static void ReplaceDefaultFont(Context context, String oldFont, String newFont){
        Typeface chooseFont = Typeface.createFromAsset(context.getAssets(),newFont);
        replaceFont(oldFont,chooseFont);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void replaceFont(String oldFont, Typeface chooseFont) {

        try {
            Field myField = Typeface.class.getDeclaredField(oldFont);
            myField.setAccessible(true);
            myField.set(null,chooseFont);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
