package io.invertase.firebase.admob;


import android.app.Activity;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import javax.annotation.Nullable;

import io.invertase.firebase.Utils;

class RNFirebaseAdmobInterstitial {

  private InterstitialAd interstitialAd;
  private RNFirebaseAdMob adMob;
  private String adUnit;

  RNFirebaseAdmobInterstitial(final String adUnitString, final RNFirebaseAdMob adMobInstance) {
    adUnit = adUnitString;
    adMob = adMobInstance;

    Activity activity = adMob.getActivity();
    // Some ads won't work without passing activity, or the app will crash
    if (activity == null) {
      interstitialAd = new InterstitialAd(adMob.getContext());
    } else {
      interstitialAd = new InterstitialAd(activity);
    }
    interstitialAd.setAdUnitId(adUnit);

    AdListener adListener = new AdListener() {
      @Override
      public void onAdLoaded() {
        sendEvent("onAdLoaded", null);
      }

      @Override
      public void onAdOpened() {
        sendEvent("onAdOpened", null);
      }

      @Override
      public void onAdLeftApplication() {
        sendEvent("onAdLeftApplication", null);
      }

      @Override
      public void onAdClosed() {
        sendEvent("onAdClosed", null);
      }

      @Override
      public void onAdFailedToLoad(int errorCode) {
        WritableMap payload = RNFirebaseAdMobUtils.errorCodeToMap(errorCode);
        sendEvent("onAdFailedToLoad", payload);
      }
    };

    interstitialAd.setAdListener(adListener);
  }

  /**
   * Load an Ad with a AdRequest instance
   *
   * @param adRequest
   */
  void loadAd(final AdRequest adRequest) {
    Activity activity = adMob.getActivity();
    if (activity != null) {
      activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          interstitialAd.loadAd(adRequest);
        }
      });
    }
  }

  /**
   * Show the loaded interstitial, if it's loaded
   */
  void show() {
    Activity activity = adMob.getActivity();
    if (activity != null) {
      activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (interstitialAd.isLoaded()) {
            interstitialAd.show();
          }
        }
      });
    }
  }

  /**
   * Send a native event over the bridge with a type and optional payload
   *
   * @param type
   * @param payload
   */
  private void sendEvent(String type, final @Nullable WritableMap payload) {
    WritableMap map = Arguments.createMap();
    map.putString("type", type);
    map.putString("adUnit", adUnit);

    if (payload != null) {
      map.putMap("payload", payload);
    }

    Utils.sendEvent(adMob.getContext(), "interstitial_event", map);
  }
}
