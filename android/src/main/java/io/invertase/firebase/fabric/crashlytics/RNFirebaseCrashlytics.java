package io.invertase.firebase.fabric.crashlytics;

import android.os.Handler;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.util.ArrayList;

public class RNFirebaseCrashlytics extends ReactContextBaseJavaModule {

  private static final String TAG = "RNFirebaseCrashlytics";
  FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

  public RNFirebaseCrashlytics(ReactApplicationContext reactContext) {
    super(reactContext);
    Log.d(TAG, "New instance");
  }

  @Override
  public String getName() {
    return TAG;
  }

  @ReactMethod
  public void crash() {
      // async task so as not to get caught by the React Native redbox handler in debug
      new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
          throw new RuntimeException("Crash Test");
        }
      }, 50);
  }

  @ReactMethod
  public void log(String message) {
    crashlytics.log(message);
  }

  @ReactMethod
  public void recordError(final int code, final String domain) {
    crashlytics.recordException(new Exception(code + ": " + domain));
  }

  @ReactMethod
  public void recordCustomError(String name, String reason, ReadableArray frameArray) {
      ArrayList<StackTraceElement> stackList = new ArrayList<>(0);
      for (int i = 0; i < frameArray.size(); i++) {
        ReadableMap map = frameArray.getMap(i);
        ReadableMap additional = map.hasKey("additional") ? map.getMap("additional") : null;
        String functionName = map.hasKey("functionName") ? map.getString("functionName") : "Unknown Function";
        String className = map.hasKey("className") ? map.getString("className") : "Unknown Class";
        StackTraceElement stack = new StackTraceElement(
          className,
          functionName,
          map.getString("fileName"),
          map.hasKey("lineNumber") ? map.getInt("lineNumber") : -1
        );
        stackList.add(stack);

        if(additional != null){
          StackTraceElement s = new StackTraceElement(
            "Additional Parameters",
            additional.toString(),
            map.getString("fileName"),
            map.hasKey("lineNumber") ? map.getInt("lineNumber") : -1
          );
          stackList.add(s);
        }
      }
      StackTraceElement[] stackTrace =  new StackTraceElement[stackList.size()];
      Exception e = new Exception(name + "\n" + reason);
      stackTrace = stackList.toArray(stackTrace);
      e.setStackTrace(stackTrace);
      crashlytics.recordException(e);
  }

  @ReactMethod
  public void setBoolValue(final String key, final boolean boolValue) {
    crashlytics.setCustomKey(key, boolValue);
  }

  @ReactMethod
  public void setFloatValue(final String key, final float floatValue) {
    crashlytics.setCustomKey(key, floatValue);
  }

  @ReactMethod
  public void setIntValue(final String key, final int intValue) {
    crashlytics.setCustomKey(key, intValue);
  }

  @ReactMethod
  public void setStringValue(final String key, final String stringValue) {
    crashlytics.setCustomKey(key, stringValue);
  }

  @ReactMethod
  public void setUserIdentifier(String userId) {
    crashlytics.setUserId(userId);
  }

  @ReactMethod
  public void setUserName(String userName) {
    crashlytics.setUserId(userName);
  }

  @ReactMethod
  public void setUserEmail(String userEmail) {
    crashlytics.setUserId(userEmail);
  }

  @ReactMethod
  public void enableCrashlyticsCollection() {
    crashlytics.setCrashlyticsCollectionEnabled(true);
  }

}
