package com.example.user.app_jni;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.user.fpLibrary.FpControl;

/**
 * Created by user on 2017/3/6.
 */
public class FpService extends Service {

    private int count = 0;
    private boolean quit = false;
    private Person person1;
    String tag="FpService";
    private RemoteCallbackList<IMessageCallback> mCallbacks = new RemoteCallbackList<IMessageCallback>();
    String name="--";
    int[] msg1={0};
    private IFpApi.Stub binder = new IFpApi.Stub() {
        @Override
        public void getCount(int[] send_data) throws RemoteException {
            msg1 = send_data;
            Log.i(tag,"client msg got="+msg1[0]);
        }

        @Override
        public void getPerson(Person person) throws RemoteException {
            person1=person;
        }

        @Override
        public void registerCallback(IMessageCallback cb) throws RemoteException {
            mCallbacks.register(cb);
            Log.i(tag,"service register callback");
        }

        @Override
        public void unregisterCallback(IMessageCallback cb) throws RemoteException {
            mCallbacks.unregister(cb);
        }
    };



    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(tag,"onBind");
        return binder;
       // return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //FpControl.fpOpen();

        new Thread(){
            int[] array1= {55,66,77};

            public void run() {
                while (!quit) {
                    try {
                        Thread.sleep(1000);

                    } catch (Exception e) {
                        Log.i(tag,"thread sleep exception");
                    }
                    count++;
                    if(count%3==0){
                        // notify client
                        if(mCallbacks== null)
                            Log.i(tag, "mCallbacks== null");
                         mCallbacks.beginBroadcast();
                        try {
                                array1[0]=count;
                            if(msg1[0]!=0)
                                array1[0]=3344;
                            /*send msg to client*/

                            Log.i(tag, "send msg to client");
                            IMessageCallback im=   mCallbacks.getBroadcastItem(0);
                                if(im!=null)
                                {
                                    Log.i(tag,"im not null");
                                    im.getMsg(array1);

                                }

                            } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        mCallbacks.finishBroadcast();
                        // notify client end
                    }
                }
            }
        }.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(tag,"service started");
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
        mCallbacks.kill();
        Log.i(tag,"service destroyed");
    }
}
