package com.dreamzone.remoteservice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;

/**
 *
 * RemoteService的示例，可提供给其他app使用的跨进程调用的Service。
 * 原理可以参看设计模式之-代理模式。简单远程Service实现的基本步骤：
 * 一、 remote service提供者
 * 1. 定义aidl文件IMusicControlService.aidl，列出所有该Service提供的接口。参数返回值都是primate类型，否则要自定义Parcelable
 * 2. build工程，在工程的build\generated\source\aidl\debug目录下自动生成aidl文件的同名接口类IMusicControlService
 * 3. 定义Service服务MusicControlService，继承自Service，可以在生命周期函数完成必要操作，如初始化或者关闭某些资源
 * 4. 定义自己的类controlBinder继承IMusicControlService.Stub这个服务桩，并实现中的接口服务函数
 * 5. 重写onBind()并返回这个自己的类controlBinder。
 * 6. 在AndroidManifest.xml文件中注册这个Service并设置好其属性可以跨进程调用。可以为intent-filter指定action
 * android:exported="true"  android:process=":remote"。
 * <p/>
 * 二、app client调用者：参见MTime的调用示例
 * 以activity中调用该远程服务为例：
 * 1. 将aidl文件及其包原封不动地拷贝至目标app的代码目录下，与src/main/java同级。
 * 2. 在onCreate()中调用bindService绑定remote service：用接口IMusicControlService定义remote service变量controlService
 * 注意跟本地service的直接返回自定义的Binder不同的是ServiceConnection的返回是以Stub接口的形式：
 * eg：controlService = IMusicControlService.Stub.asInterface(service);
 * 3. 业务中直接用controlService取用远程服务的方法。
 * 4. onDestroy中unbindService
 *
 * 三、运行
 * 1. 先安装remote service。无界面
 * 2. 安装并运行调用的app
 * **************************************************************************************************************
 * Ref:
 *  1. http://blog.csdn.net/guolin_blog/article/details/9797169
 *  2. http://blog.csdn.net/sunboy_2050/article/details/7366396
 *
 * Demo link：
 *
 */
public class MusicControlService extends Service {

    private MediaPlayer mediaPlayer;
    private static final String TAG = "MATRIX";

    public MusicControlService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "mediaPlayer()  onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Log.e(TAG, "mediaPlayer()  onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return controlBinder;
    }


    /**
     * 音乐播放控制服务
     */
    private final IMusicControlService.Stub controlBinder = new IMusicControlService.Stub() {
        @Override
        public void play() throws RemoteException {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(MusicControlService.this, R.raw.knbks);
                Log.e(TAG, "mediaPlayer()  play");
                mediaPlayer.setLooping(false);
            }
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }

        }

        @Override
        public void stop() throws RemoteException {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "mediaPlayer() stop");
        }

        @Override
        public void pause() throws RemoteException {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }

        @Override
        public int remoteAdd(int a, int b) throws RemoteException {
            return (a + b);
        }


    };

}
