package com.dreamzone.remoteservice;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * @author BMR
 * @ClassName: MyApplication
 * @Description: 此类为了验证当此工程作为RemoteService被其他app调用时，进程名是“包名:remote”的形式
 * @date 2016/1/25 15:37
 */
public class MyApplication extends Application {
    private String pkgName = "com.dreamzone.remoteservice";
    private static final String TAG = "MATRIX";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(TAG, "onCreate() ");
        Log.e(TAG, "isMainProcess()= " + isMainProcess(pkgName));

    }


    protected boolean isMainProcess(String appPkgName) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                // 此处将会打印：isMainProcess()= com.dreamzone.remoteservice:remote
                Log.e(TAG, "processName() " + appProcess.processName);
                Log.e(TAG, "appPkgName() " + appPkgName);
                if (appProcess.processName.equals(appPkgName)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
