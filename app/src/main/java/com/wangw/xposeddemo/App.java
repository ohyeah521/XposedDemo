package com.wangw.xposeddemo;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by wangw on 2017/12/19.
 */

public class App extends Application {

    public static App instance;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public void showToast(final String msg){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.instance,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
