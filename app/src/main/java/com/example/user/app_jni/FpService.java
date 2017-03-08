package com.example.user.app_jni;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.user.fpLibrary.FpControl;

/**
 * Created by user on 2017/3/6.
 */
public class FpService extends Service {

    private int count = 0;
    private boolean quit = false;
    private Person person;
    private IFpApi.Stub binder = new IFpApi.Stub() {
        @Override
        public int getCount() throws RemoteException {
            return count;
        }
///*
        @Override
        public Person getPerson() throws RemoteException {
            person.setName("xh");
            return person;
        }
       // */
    };


    @Override
    public IBinder onBind(Intent arg0) {
        System.out.println("server_ onBind");
        return binder;
       // return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("server_ service_lxh_created");
        FpControl.fpOpen();
        new Thread(){
            public void run() {
                while (!quit) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("server_ sleep,count=" + count);

                    } catch (Exception e) {
                        System.out.print("server_ thread sleep exception");
                    }
                    count++;
                }
            }
        }.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("server_ service started");
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        quit = true;
        binder = null;
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        quit = true;
        binder = null;
        System.out.println("server_ service_lxh_destroyed");
    }
}
