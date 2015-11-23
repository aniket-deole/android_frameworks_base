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
            Slog.d(TAG, "Start service SysInvaders");
        init_native ();
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
                Slog.d(TAG, "Call native service SysInvaders");
            /*
             * We do not really need the nativePointer here;
             * Just to show how arguments are passed to JNI from Java
             */
        }
    };
    private static native long init_native();

}
