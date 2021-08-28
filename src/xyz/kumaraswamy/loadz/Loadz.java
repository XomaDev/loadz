package xyz.kumaraswamy.loadz;

import android.app.Activity;
import android.util.Log;

import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.util.YailList;
import dalvik.system.DexClassLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Loadz extends AndroidNonvisibleComponent {

    private static final String TAG = "Loadz";
    private final Activity activity;

    public Loadz(ComponentContainer container) {
        super(container.$form());
        activity = container.$context();
    }

    @SimpleFunction(description = "Returns the activity")
    public Object Activity() {
        return activity;
    }

    @SimpleFunction(description = "Invokes the object provided")
    public Object Invoke(final Object object, final String method, final YailList parms) throws InvocationTargetException, IllegalAccessException {
        final Method[] mMethods = object.getClass().getMethods();
        Object[] mParameters = parms.toArray();
        Method mMethod = getMethod(mMethods, method, mParameters.length);

        Class<?>[] mRequestedMethodParameters = mMethod.getParameterTypes();
        ArrayList<Object> mParametersArrayList = new ArrayList<Object>();
        for (int i = 0; i < mRequestedMethodParameters.length; i++) {
            if ("int".equals(mRequestedMethodParameters[i].getName())) {
                mParametersArrayList.add(Integer.parseInt(mParameters[i].toString()));
            } else if ("float".equals(mRequestedMethodParameters[i].getName())) {
                mParametersArrayList.add(Float.parseFloat(mParameters[i].toString()));
            } else if ("double".equals(mRequestedMethodParameters[i].getName())) {
                mParametersArrayList.add(Double.parseDouble(mParameters[i].toString()));
            } else if ("java.lang.String".equals(mRequestedMethodParameters[i].getName())) {
                mParametersArrayList.add(mParameters[i].toString());
            } else if ("boolean".equals(mRequestedMethodParameters[i].getName())) {
                mParametersArrayList.add(Boolean.parseBoolean(mParameters[i].toString()));
            } else {
                mParametersArrayList.add(mParameters[i]);
            }
        }
        final Object result = mMethod.invoke(object, mParametersArrayList.toArray());
        return result == null ? "" : result;
    }

    @SimpleFunction(description = "Loads class from JAR")
    public Object Load(final String name, final String className) throws Exception {
        final Class<?> myClass = new DexClassLoader(name, "/data/data/" + activity.getPackageName() +
                "/", null, getClass().getClassLoader()).loadClass(className);
        Log.d(TAG, "LoadJAR: Loaded class name " + myClass.getName());
        return myClass.newInstance();
    }

    public Method getMethod(Method[] methods, String name, int parameterCount) {
        name = name.replaceAll("[^a-zA-Z0-9]", "");
        for (Method method : methods) {
            int methodParameterCount = method.getParameterTypes().length;
            if (method.getName().equals(name) && methodParameterCount == parameterCount) {
                return method;
            }
        }
        return null;
    }
}
