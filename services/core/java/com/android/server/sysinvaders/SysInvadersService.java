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

  private final Context mContext;
  private static final String TAG = "SysInvadersService";
  private static final boolean DEBUG = true;
  
  private Boolean timerStarted;
  
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
      timer.schedule (new mainTask(), 5000);
    }
};
  private class mainTask extends TimerTask
      { 
        public void run() 
        {
        	try {
        	IActivityManager am = ActivityManagerNative.getDefault();

            List<ActivityManager.RunningServiceInfo> services 
                    = am.getServices(100, 0);
            
            for (ActivityManager.RunningServiceInfo amrsi : services) {
              Slog.v ("SysInvaders", "Service: " + amrsi.process);
              ApplicationInfo ai = ResourcesManager.getPackageManager ().getApplicationInfo(amrsi.process, PackageManager.GET_META_DATA, 0);
              Bundle bundle = ai.metaData;
              if (bundle != null) {
                  String networkPreference = bundle.getString ("network_preference");
                  Slog.v ("SysInvaders", "NetworkPreference: " + networkPreference);
              } else {
            	  Slog.v ("SysInvaders", "NetworkPreference: None");
              }
              
            }
            
            List<ActivityManager.RunningAppProcessInfo> processes
                    = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo amrapi : processes) {
              Slog.v ("SysInvaders", "Apps: " + amrapi.processName);
              ApplicationInfo ai = ResourcesManager.getPackageManager ().getApplicationInfo(amrapi.processName, PackageManager.GET_META_DATA, 0);
              Bundle bundle = ai.metaData;
              if (bundle != null) {
                  String networkPreference = bundle.getString ("network_preference");
                  Slog.v ("SysInvaders", "NetworkPreference: " + networkPreference);
              } else  {
            	  Slog.v ("SysInvaders", "NetworkPreference: None");
              }
            }
            
            TelephonyManager tm = TelephonyManager.getDefault ();
            tm.setPreferredNetworkType (RILConstants.NETWORK_MODE_GSM_ONLY);
        	} catch (RemoteException e) {
        		Slog.d(TAG, "Remote Exception", e);
        	} catch (Exception e) {
        		Slog.d (TAG, "Unknown Exception", e);
        	}
        }
      }
private static native long init_native();

}
