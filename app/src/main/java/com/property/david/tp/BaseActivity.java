package com.property.david.tp;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.property.david.tp.Utils.FontManager;

import java.lang.reflect.Method;


/**
 * Created by david on 11/8/17.
 */

public class BaseActivity extends AppCompatActivity {
    public static long ONE_DAY_SECONDS = 60 * 60 * 24;
    public Typeface iconFont;

    public int screenHeight, screenWidth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    public void tryToRequestMarshmallowAccessFineLocationPermission() {

        if (Build.VERSION.SDK_INT < 23) return;

        final Method checkSelfPermissionMethod = getCheckSelfPermissionMethod();

        if (checkSelfPermissionMethod == null) return;

        try {

            final Integer permissionCheckResult = (Integer) checkSelfPermissionMethod.invoke(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) return;

            final Method requestPermissionsMethod = getRequestPermissionsMethod();
            if (requestPermissionsMethod == null) return;

            requestPermissionsMethod.invoke(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void tryToRequestMarshmallowAccessCoarseLocationPermission() {

        if (Build.VERSION.SDK_INT < 23) return;

        final Method checkSelfPermissionMethod = getCheckSelfPermissionMethod();

        if (checkSelfPermissionMethod == null) return;

        try {

            final Integer permissionCheckResult = (Integer) checkSelfPermissionMethod.invoke(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) return;

            final Method requestPermissionsMethod = getRequestPermissionsMethod();
            if (requestPermissionsMethod == null) return;

            requestPermissionsMethod.invoke(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private Method getCheckSelfPermissionMethod() {
        try {
            return Activity.class.getMethod("checkSelfPermission", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    private Method getRequestPermissionsMethod() {
        try {
            final Class[] parameterTypes = {String[].class, int.class};

            return Activity.class.getMethod("requestPermissions", parameterTypes);

        } catch (Exception e) {
            return null;
        }
    }
}
