package xyz.kumaraswamy.loadz;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

@SuppressWarnings("unused")
public class TestClass {
    private static final String TAG = "TestClass";

    public void print(final String message) {
        Log.d(TAG, "print: " + message);
    }

    public static void toast(Activity activity, final String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }
}
