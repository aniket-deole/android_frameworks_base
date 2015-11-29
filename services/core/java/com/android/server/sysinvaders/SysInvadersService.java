package com.android.server.sysinvaders;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Slog;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.ResourcesManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.sysinvaders.ISysInvadersService;
import android.os.CountDownTimer;

import com.android.internal.telephony.RILConstants;
import com.android.server.SystemService;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*
https://www.linaro.org/blog/adding-a-new-system-service-to-android-5-tips-and-how-to/
*/
public class SysInvadersService extends SystemService {

	/*
	 * LTE = 4.
	 * 3G = 3.
	 * 2G = 2
	 */
	public enum PREFERRED_NETWORK {
		NONE,TWOG,THREEG,LTE
	} 
  private final Context mContext;
  private static final String TAG = "SysInvadersService";
  private static final boolean DEBUG = true;
  
  private Boolean timerStarted;
  
  public static final Integer TIMER_DURATION = 10000;
  
  public SysInvadersService(Context context) {
    super(context);
    if (DEBUG){
      Slog.d(TAG, "Build service");
    }
    mContext = context;
    publishBinderService(context.SYSINVADERS_SERVICE, mService);
    
    timerStarted = false;

  }

  /**
   * Called when service is started by the main system service
   */
  @Override
  public void onStart() {
    Slog.d(TAG, "Start service SysInvaders");
    init_native ();
  }

    private static Timer timer = new Timer(); 
  /**
   * Implementation of AIDL service interface
   */
  private final IBinder mService = new ISysInvadersService.Stub() {
    /**
     * Implementation of the methods described in AIDL interface
     */
    @Override
    public void callSysInvadersMethod() {
      Slog.d(TAG, "Call native service SysInvaders");
      if (timerStarted)
    	  timer.cancel();
      timer.schedule (new mainTask(), TIMER_DURATION);
    }
};
  private class mainTask extends TimerTask
      { 
	  
	  	PREFERRED_NETWORK getPreferredNetworkFromString (String networkPreference) {
	  		PREFERRED_NETWORK preferredNetwork = PREFERRED_NETWORK.NONE;
	  		if (networkPreference.equalsIgnoreCase ("LTE")) {
          	  preferredNetwork = PREFERRED_NETWORK.LTE;
            } else if (networkPreference.equalsIgnoreCase ("3G")) {
          	  preferredNetwork = PREFERRED_NETWORK.THREEG;
            } else if (networkPreference.equalsIgnoreCase ("2G")) {
          	  preferredNetwork = PREFERRED_NETWORK.TWOG;
            } else {
          	  preferredNetwork = PREFERRED_NETWORK.TWOG;
            }
	  		return preferredNetwork;
	  	}
	  
	  	public void run() 
        {
        	try {
            	timerStarted = false;
	        	IActivityManager am = ActivityManagerNative.getDefault();
	
	            List<ActivityManager.RunningServiceInfo> services 
	                    = am.getServices(100, 0);
	
	            PREFERRED_NETWORK topPreferredNetwork = PREFERRED_NETWORK.NONE;
	            
	            for (ActivityManager.RunningServiceInfo amrsi : services) {
	              ApplicationInfo ai = ResourcesManager.getPackageManager ().getApplicationInfo(amrsi.process, PackageManager.GET_META_DATA, 0);
	              if (ai != null) {
		              Bundle bundle = ai.metaData;
		              if (bundle != null) {
		                  String networkPreference = bundle.getString ("network_preference");
		                  PREFERRED_NETWORK preferredNetwork = getPreferredNetworkFromString (networkPreference);
		                  if (preferredNetwork.ordinal() > topPreferredNetwork.ordinal())
		                	  topPreferredNetwork = preferredNetwork;
		                  Slog.v ("SysInvaders", "NetworkPreference: " + networkPreference +", Top Preference: " + topPreferredNetwork);
		              }
	              }
	            }
	            
	            List<ActivityManager.RunningAppProcessInfo> processes
	                    = am.getRunningAppProcesses();
	            for (ActivityManager.RunningAppProcessInfo amrapi : processes) {
	              ApplicationInfo ai = ResourcesManager.getPackageManager ().getApplicationInfo(amrapi.processName, PackageManager.GET_META_DATA, 0);
	              if (ai != null) {
		              Bundle bundle = ai.metaData;
		              if (bundle != null) {
		                  String networkPreference = bundle.getString ("network_preference");
		                  PREFERRED_NETWORK preferredNetwork = getPreferredNetworkFromString (networkPreference);
		                  if (preferredNetwork.ordinal() > topPreferredNetwork.ordinal())
		                	  topPreferredNetwork = preferredNetwork;
		                  Slog.v ("SysInvaders", "NetworkPreference: " + networkPreference +", Top Preference: " + topPreferredNetwork);
		              }
	              }
	              break;
	            }
	            
	            if (topPreferredNetwork == PREFERRED_NETWORK.LTE) {
	                TelephonyManager tm = TelephonyManager.getDefault ();
	                tm.setPreferredNetworkType (RILConstants.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA);	
	                  Slog.v ("SysInvaders", "NetworkPreference set to LTE Preferred.");
	            } else if (topPreferredNetwork == PREFERRED_NETWORK.TWOG) {
	                TelephonyManager tm = TelephonyManager.getDefault ();
	                tm.setPreferredNetworkType (RILConstants.NETWORK_MODE_GSM_UMTS);
	                  Slog.v ("SysInvaders", "NetworkPreference set to 2G.");
	            } else if (topPreferredNetwork == PREFERRED_NETWORK.THREEG) {
	                TelephonyManager tm = TelephonyManager.getDefault ();
	                tm.setPreferredNetworkType (RILConstants.NETWORK_MODE_WCDMA_PREF);
	                  Slog.v ("SysInvaders", "NetworkPreference set to 3G preferred.");
	            } else if (topPreferredNetwork == PREFERRED_NETWORK.NONE) {
	                  Slog.v ("SysInvaders", "NetworkPreference unchanged.");
	            }
                Slog.v ("SysInvaders", "-----------------------------------");
	            
        	} catch (RemoteException e) {
        		Slog.d(TAG, "Remote Exception", e);
        	} catch (Exception e) {
        		Slog.d (TAG, "Unknown Exception", e);
        	}
        }
      }
private static native long init_native();

}
