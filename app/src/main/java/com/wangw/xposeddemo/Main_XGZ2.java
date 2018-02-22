package com.wangw.xposeddemo;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by wangw on 2018/2/2.
 */
@Deprecated
public class Main_XGZ2 implements IXposedHookLoadPackage {

    XC_LoadPackage.LoadPackageParam mLpp;
    String iApp = "s.h.e.l.l.S";
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpp) throws Throwable {
        Log.d("XXXXP", "handleLoadPackage: "+lpp.packageName);
        if (!((lpp.packageName.equals("cn.paigea.live") && lpp.processName.equals("cn.paigea.live")) || (lpp.packageName.equals("com.uzero.poem"))))
            return;
        log("开始Hook XGZ");
        mLpp = lpp;
//        HookDex();
//        HookAttachBase();


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                log("开始Sleep");
//                try {
//                    Thread.currentThread().sleep(1000*30);
//                } catch (InterruptedException e) {
//                    log("Sleep 失败");
//                    e.printStackTrace();
//                }
//                log("Sleep 正常结束");
//            }
//        }).start();

//        log("开始Sleep");
//        try {cle
//            Thread.sleep(1000 * 60);
//            log("sleep 结束");
//            HookUtitls(mLpp.classLoader);
//        }catch (Exception e){
//            log("Sleep 异常");
//        }


    }

    private void HookDex() {
        try {
            Class<?> clz = mLpp.classLoader.loadClass("dalvik.system.DexFile");
            for (Method method : clz.getDeclaredMethods()) {
                log("[Dex Method] = "+method);
                XposedBridge.hookMethod(method, new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
//                        log("Before => " + param.method);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String v = "";
                        if (param.args != null){
                            for (Object arg : param.args) {
                                if (arg != null)
                                    v += arg.toString()+" , ";
                            }
                        }
                        Object obj = "";
                        if (param.getResult() != null){
                            obj = param.getResult().toString();
                        }
                        log("After <=" + param.method+"\nparams = "+v+"\n result = "+obj);

                        if (!isHooked && param.method.getName().equals("defineClass")){
                            ClassLoader cl = (ClassLoader) param.args[1];
                            if (cl.toString().contains("cn.paigea.live")){
                                isHooked = true;
                                HookUtitls(cl);

                            }

                        }
                    }
                });
            }
        } catch (ClassNotFoundException e) {
            log("Hook Dex 失败");
            e.printStackTrace();
        }
    }

    boolean isHooked = false;

    private void HookAttachBase() {
        log("Hook IAPP");
        Class<?> clz = XposedHelpers.findClass(iApp, mLpp.classLoader);
        for (Field field : clz.getDeclaredFields()) {
            log("[IAPP FIELD] = "+field);
        }
        for (Method method : clz.getDeclaredMethods()) {
            log("[IAPP METHOD] = "+ method);

            XposedBridge.hookMethod(method, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    log("["+param.method + "] ===> before");
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    log("["+param.method + "] <=== after");
                    if (param.method.getName().equals("l")){
                        try {
                            HookUtitls(param.args[0].getClass().getClassLoader());
                        }catch (Exception e){
                            log("Hook Utils 失败");
                        }
                    }
                }


            });
        }
    }

    private void HookUtitls(ClassLoader cl) {
        log("Hook Utils");
//        final ClassLoader cl = mLpp.classLoader;//context.getClassLoader();
        Class<?> clz = null;
        try {
            clz = XposedHelpers.findClass("com.fanwe.hybrid.activity.InitActivity", cl);
        }catch (Exception e){
            log("Hook Init失败");
            e.printStackTrace();
            isHooked = false;
        }
        if (clz == null)
            return;
        String v;
        for (Field field : clz.getDeclaredFields()) {
            v = "";
            try{
                v = field.get(null).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            log("[PoemApp Field] = "+field+" | value = "+v);
        }
        for (Method mm : clz.getDeclaredMethods()) {
            log("[PoemApp METHOD] = "+mm);
            XposedBridge.hookMethod(mm, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    log("["+param.method + "] ===> before");
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    log("["+param.method + "] ===> before");
                }
            });
        }
    }

    private void log(String msg) {
        XposedBridge.log(msg);
    }
}
