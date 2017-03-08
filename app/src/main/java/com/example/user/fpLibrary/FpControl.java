package com.example.user.fpLibrary;

/**
 * Created by user on 2017/3/2.
 */
public class FpControl {
    public static native int fpCtrl(int which, int status);
    public static native int fpOpen();
    public static native void fpClose();


    static {
        try{
            System.loadLibrary("fpcontrol");
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
