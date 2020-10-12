package io.invertase.firebase.fabric.crashlytics;

/**
 * This class is purely cosmetic - to indicate on the Crashlytics console that it's
 * an UnhandledPromiseRejection JS error rather than the generic `java.lang.Exception`.
 */
class UnhandledPromiseRejection extends Exception {
  UnhandledPromiseRejection(String message) {
    super(message);
  }
}