package com.android.server.sysinvaders;

import android.util.Log;
import android.util.Slog;

import android.os.IBinder;
import android.content.Context;

import android.sysinvaders.ISysInvadersService;
import com.android.server.SystemService;


/*
https://www.linaro.org/blog/adding-a-new-system-service-to-android-5-tips-and-how-to/
*/
public class SysInvadersService extends SystemService {

    private final Context mContext;
    private static final String TAG = "SysInvadersService";
    private static final boolean DEBUG = true;

    public SysInvadersService(Context context) {
        super(context);
        if (DEBUG){
            Slog.d(TAG, "Build service");
        }
        mContext = context;
        publishBinderService(context.SYSINVADERS_SERVICE, mService);

    }
    
    /**
     * Called when service is started by the main system service
     */
    @Override
    public void onStart() {
        if (DEBUG){
            Slog.d(TAG, "Start service");
        }
    }
    
    /**
     * Implementation of AIDL service interface
     */
    private final IBinder mService = new ISysInvadersService.Stub() {
        /**
         * Implementation of the methods described in AIDL interface
         */
        @Override
        public void callSysInvadersMethod() {
            if (DEBUG){
                Slog.d(TAG, "Call native service");
            }
            /*
             * We do not really need the nativePointer here;
             * Just to show how arguments are passed to JNI from Java
             */
        }
    };

}
