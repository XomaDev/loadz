package xyz.kumaraswamy.loadz;

import android.app.Activity;
import android.util.Log;

import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.errors.YailRuntimeError;
import com.google.appinventor.components.runtime.util.YailList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

public class Loadz extends AndroidNonvisibleComponent {

    private static final String TAG = "Loadz";
    private final Activity activity;

    private final ComponentContainer container;

    public Loadz(ComponentContainer container) {
        super(container.$form());
        this.container = container;
        activity = container.$context();
    }

    @SimpleFunction(description = "Returns the activity")
    public Object Activity() {
        return activity;
    }

    @SimpleFunction(description = "Invokes the object provided")
    public Object Invoke(final Object object, final String method, final YailList parms) {
        try {
            final Object[] objectParms = parms.toArray();
            final Method methodObject = findMethod(object.getClass().getMethods(),
                    method, objectParms.length);

            final Object result = methodObject.invoke(object,
                    castParms(methodObject.getParameterTypes(), objectParms).toArray());
            return result == null ? "" : result;
        } catch (Exception exception) {
            Exception(this, "Invoke", 0, YailList.makeList(Collections.singleton(exception.getMessage())));
        }
        return "";
    }

    private ArrayList<Object> castParms(Class<?>[] requestedTypes, Object[] objectParms) {
        ArrayList<Object> modifiedParms = new ArrayList<>();

        for (int i = 0; i < requestedTypes.length; i++) {
            final String value = String.valueOf(objectParms[i]);

            switch (requestedTypes[i].getName()) {
                case "int":
                    modifiedParms.add(Integer.parseInt(value));
                    break;
                case "float":
                    modifiedParms.add(Float.parseFloat(value));
                    break;
                case "double":
                    modifiedParms.add(Double.parseDouble(value));
                    break;
                case "java.lang.String":
                    modifiedParms.add(value);
                    break;
                case "boolean":
                    modifiedParms.add(Boolean.parseBoolean(value));
                    break;
                default:
                    modifiedParms.add(objectParms[i]);
            }
        }
        return modifiedParms;
    }

    @SimpleFunction(
            description =
                    "Loads class from JAR")
    public Object Load(final String name, final String className) throws Exception {
        final Class<?> myCLass = load(name, className);
        Log.d(TAG, "LoadJAR: Loaded class name " + myCLass.getName());
        return myCLass.newInstance();
    }

    @SimpleFunction(
            description =
                    "Loads extension from JAR")
    public Component LoadExtension(final String name, final String className) throws Exception {
        final Class<?> myClass = load(name, className);
        final Component component;
        try {
            component = (Component) myClass.getConstructor(ComponentContainer.class).newInstance(container);
        } catch (Exception exception) {
            throw new YailRuntimeError("Expected a component!", TAG);
        }
        return component;
    }

    private Class<?> load(final String name, final String className) throws Exception {
        final Object dexLoader = Class.forName("dalvik.system.DexClassLoader").getConstructor(String.class, String.class,
                        String.class, ClassLoader.class)
                .newInstance(name, "/data/data/" + activity.getPackageName() +
                        "/", null, getClass().getClassLoader());

        return (Class<?>) findMethod(dexLoader.getClass().getMethods(), "loadClass", 1)
                .invoke(dexLoader, className);
    }

    private Method findMethod(Method[] methods, String name, int parm) {
        for (final Method method : methods) {
            if (method.getName().equals(name)
                    && method.getParameterTypes().length == parm) {
                return method;
            }
        }
        throw new YailRuntimeError("Unable to find method named '" + name + "'", TAG);
    }

    @SimpleFunction(
            description =
                    "Injects the new behaviour to the component"
    )
    public void Inject(final Component component) throws NoSuchFieldException, IllegalAccessException {
        if (component == null) {
            return;
        }
        final Field form = component.getClass().getSuperclass().getDeclaredField("form");
        form.setAccessible(true);
        form.set(component, new FForm(activity, this));
    }

    @SimpleEvent(
            description =
                    "Injected component or extension got an error")
    public void Exception(final Component component, final String block, int code, YailList messages) {
        EventDispatcher.dispatchEvent(this, "Exception", component, block, code, messages);
    }

    @SimpleEvent(
            description =
            "Event raised of an injected component")
    public void Event(final Component component, final String name, final String eventName, YailList args) {
        EventDispatcher.dispatchEvent(this, "Event", component, name, eventName, args);
    }

    @SimpleEvent(
            description =
                    "One of the injected component requested for permission"
    )
    public void PermissionRequest(final String permission) {
        EventDispatcher.dispatchEvent(this, "PermissionRequest", permission);
    }
}
