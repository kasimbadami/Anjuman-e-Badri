package mumineen.connect.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import mumineen.connect.app.MainActivity;

public class Util
{
    public static void clearPreferences(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("anjuman.e.badri", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("URL", MainActivity.mURL).apply();
    }
}
