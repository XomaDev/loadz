package xyz.kumaraswamy.loadz;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.PermissionResultHandler;
import com.google.appinventor.components.runtime.ReplForm;
import com.google.appinventor.components.runtime.util.YailList;

import java.util.Arrays;

public class FForm extends ReplForm {
    private static final String TAG = "FForm";
    private final Loadz loadz;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public FForm(Context context, Loadz loadz) {
        attachBaseContext(context);
        this.loadz = loadz;
    }

    public boolean canDispatchEvent(Component component, String name) {
        return true;
    }

    @Override
    public boolean dispatchEvent(Component component, String componentName, String eventName,
                                 Object[] args) {
        Log.d(TAG, "dispatchEvent: Event raised, component name " + componentName + " event " + eventName);
        loadz.Event(component, componentName, eventName, YailList.makeList(args));
        return true;
    }

    @Override
    public void dispatchGenericEvent(Component component, String eventName,
                                     boolean notAlreadyHandled, Object[] args) {
        Log.d(TAG, "dispatchEvent: Event raised, component name " + component + " event " + eventName);
        loadz.Event(component, "", eventName, YailList.makeList(args));
    }

    @Override
    public void askPermission(final String permission, final PermissionResultHandler responseRequestor) {
        Log.d(TAG, "askPermission: Permission needed of name " + permission);
        loadz.PermissionRequest(permission);
    }

    @Override
    public void dispatchErrorOccurredEvent(final Component component, final String functionName,
                                           final int errorNumber, final Object... messageArgs) {
        Log.d(TAG, "dispatchErrorOccurredEvent: " + Arrays.toString(messageArgs));
        loadz.Exception(component, functionName, errorNumber, YailList.makeList(messageArgs));
    }
}
