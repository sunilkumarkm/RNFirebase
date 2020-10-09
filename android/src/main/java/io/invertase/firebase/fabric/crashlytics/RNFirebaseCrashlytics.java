package io.invertase.firebase.fabric.crashlytics;

import android.os.Handler;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import android.util.Log;
import java.util.Objects;

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
  public void recordError(ReadableMap jsErrorMap) {
    if (ReactNativeFirebaseCrashlyticsInitProvider.isCrashlyticsCollectionEnabled()) {
      recordJavaScriptError(jsErrorMap);
    }
  }

  @ReactMethod
  public void recordCustomError(ReadableMap jsErrorMap) {
    if (ReactNativeFirebaseCrashlyticsInitProvider.isCrashlyticsCollectionEnabled()) {
      recordJavaScriptError(jsErrorMap);
    }
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

   private void recordJavaScriptError(ReadableMap jsErrorMap) {
    String message = jsErrorMap.getString("message");
    ReadableArray stackFrames = Objects.requireNonNull(jsErrorMap.getArray("frames"));
    boolean isUnhandledPromiseRejection = jsErrorMap.getBoolean("isUnhandledRejection");

    Exception customException;
    if (isUnhandledPromiseRejection) {
      customException = new UnhandledPromiseRejection(message);
    } else {
      customException = new JavaScriptError(message);
    }

    StackTraceElement[] stackTraceElements = new StackTraceElement[stackFrames.size()];

    for (int i = 0; i < stackFrames.size(); i++) {
      ReadableMap stackFrame = Objects.requireNonNull(stackFrames.getMap(i));
      String fn = stackFrame.getString("fn");
      String file = stackFrame.getString("file");
      stackTraceElements[i] = new StackTraceElement("", fn, file, -1);
    }

    customException.setStackTrace(stackTraceElements);

    FirebaseCrashlytics.getInstance().recordException(customException);
  }

}
