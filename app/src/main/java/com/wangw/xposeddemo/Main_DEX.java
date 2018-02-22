package com.wangw.xposeddemo;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by wangw on 2018/2/13.
 */

public class Main_DEX implements IXposedHookLoadPackage {


    private static final String TAG = "Main_DEX";
    private Class<?> mDex;
    private Method mDex_getBytes;
    private Method mGetDex;
    private XSharedPreferences mSp;
    private String mPkName;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (mSp == null)
            mSp = new XSharedPreferences("com.wangw.xposeddemo", MainActivity.SP_NAME);
        mSp.reload();
        this.mPkName = mSp.getString(MainActivity.KEY_PACKAGE, "");
        if (TextUtils.isEmpty(mPkName)){
            log("没有指定要Hook的APK");
            return;
        }
        log("SP = "+mPkName);
        if (!loadPackageParam.packageName.equals(mPkName))
            return;
        log("开始Hook "+mPkName);
        initRefect();

//        XposedHelpers.findAndHookMethod(,loadPackageParam.classLoader,"loadClass", Class.forName("java.lang.String"),Boolean.TYPE,);
        Class<?> clz = XposedHelpers.findClass("java.lang.ClassLoader",loadPackageParam.classLoader);
        for (Method method : clz.getDeclaredMethods()) {
            XposedBridge.hookMethod(method, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    StringBuilder sb = new StringBuilder();
                    if (param.args != null){
                        for (Object arg : param.args) {
                            sb.append(arg)
                                    .append(" | ");
                        }
                    }
                    log("[Before]"+param.method+"\n params = "+sb.toString());

                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    log("[After]"+param.method);
                    if (param.method.getName().equals("loadClass")){
                        Class result = (Class) param.getResult();
                        byte[] arr = (byte[]) mDex_getBytes.invoke(mGetDex.invoke(result));
                        File file = new File(mSp.getString(MainActivity.KEY_DIR, "/sdcard"), mPkName + "_" + arr.length + ".dex");
                        if (file.exists()){
                            file.delete();
                        }
                        FIO.writeByte(arr,file.getAbsolutePath());
                        log("Dex Dump完成， output = "+file.getAbsolutePath());
                    }
                }
            });
        }

    }

    private void initRefect() {

        try {
            mDex = Class.forName("com.android.dex.Dex");
            mDex_getBytes = mDex.getDeclaredMethod("getBytes");
            mGetDex = Class.forName("java.lang.Class").getDeclaredMethod("getDex");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void log(String msg) {
        Log.d(TAG, msg);
//        XposedBridge.log(msg);
    }
}
