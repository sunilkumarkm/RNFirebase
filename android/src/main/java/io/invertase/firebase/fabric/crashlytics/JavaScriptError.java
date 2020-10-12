package io.invertase.firebase.fabric.crashlytics;

/**
 * This class is purely cosmetic - to indicate on the Crashlytics console that it's
 * a JavaScript error rather than the generic `java.lang.Exception`.
 */
class JavaScriptError extends Exception {
  JavaScriptError(String message) {
    super(message);
  }
}