package pl.droidsonroids.rockpaperscissors;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserPreferences {

    private static final String PREF_USER_NAME = ".user_name";

    private static UserPreferences sInstance;
    private final SharedPreferences mSharedPreferences;

    public static UserPreferences getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new UserPreferences(context);
        }
        return sInstance;
    }

    private UserPreferences(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setUserName(String name) {
        mSharedPreferences.edit().putString(PREF_USER_NAME, name).apply();
    }

    public String getUserName() {
        return mSharedPreferences.getString(PREF_USER_NAME, null);
    }
}
