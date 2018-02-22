package com.wangw.xposeddemo;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by wangw on 2018/2/1.
 */
@Deprecated
public class Main_XGZ implements IXposedHookLoadPackage {

    private static final String TAG = "Xposed";
    private XC_LoadPackage.LoadPackageParam mLpp;

    @Override
    public void handleLoadPackage( XC_LoadPackage.LoadPackageParam lpp) throws Throwable {
        Log.d("XXXXP", "handleLoadPackage: "+lpp.packageName);
        if (!(lpp.packageName.equals("cn.paigea.live") && lpp.processName.equals("cn.paigea.live")))
            return;
        log("开始Hook XGZ");

        this.mLpp = lpp;
        log("classLoader = "+mLpp.classLoader+" | processName = "+mLpp.processName+" | appInfo = "+mLpp.appInfo+" | appInfoClassName = "+mLpp.appInfo.className);
        HookDex();
        try{
            HookMessageDigest(mLpp);
            HookString(mLpp);
        }catch (Exception e){
            log("Sleep 异常");
            e.printStackTrace();
        }
        try{
            HookApp();
        }catch (Exception e){
            e.printStackTrace();
        }

        HookApp2();
    }

    private void HookDex() {
        try {
            log("Hook Dex");
            Class<?> clz = mLpp.classLoader.loadClass("dalvik.system.DexFile");
            for (Method method : clz.getDeclaredMethods()) {
                if (method.getName().equals("defineClass")){
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            log("defineClass Before");
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            log("defineClass After");
                            String name = (String) param.args[0];
                            log("[defineClass] = "+name);
                        }
                    });
                }
            }
        } catch (ClassNotFoundException e) {
            log("Hook Dex 失败");
            e.printStackTrace();
        }
    }

    private void HookApp2() {
        log("Hook App2");
        XposedHelpers.findAndHookMethod("android.app.Application",mLpp.classLoader, "attach", Context.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                log("Hook App2 Before");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                log("Hook App2 After");

                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try{
                    cl.loadClass("com.fanwe.hybrid.activity.InitActivity");
                    log("Hook 成功");
                }catch(Exception e){
                    log("Hook App2 Exception");
                    e.printStackTrace();
                }
            }
        });
    }

    private void HookApp() {
        log("Hook App");
//        final Class<?> clz = XposedHelpers.findClass("s.h.e.l.l.S", mLpp.classLoader);
//        for (Method method : clz.getDeclaredMethods()) {
//            log("[App Method] = "+method.getName());
//            if (method.getName().equals("onCreate")){
//                XposedBridge.hookMethod(method, new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        log("Hook App Before");
//
//                    }
//
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        log("Hook App After");
//                        for (Field field : clz.getDeclaredFields()) {
//                            log("[App field] = "+field.getName()+" | v = ");
//                        }
//                        HookAfter();
//                    }
//                });
//            }
//        }

        XposedHelpers.findAndHookMethod("s.h.e.l.l.S",mLpp.classLoader,"onCreate",new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                log("Hook App Before");

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                log("Hook App After");
                for (Field field : param.thisObject.getClass().getDeclaredFields()) {
                    log("[App field] = "+field.getName()+" | v = ");
                }
                HookAfter();
            }
        });

    }

    private void HookAfter() {
        try {
            hookFlash(mLpp);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            hookInit(mLpp);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            hookHttpUtils(mLpp);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void HookString(XC_LoadPackage.LoadPackageParam fp) {
        log("Hook String");
        Class<?> clz = XposedHelpers.findClass("java.lang.String", fp.classLoader);
        for (Method method : clz.getDeclaredMethods()) {
//            log("[String Method] = "+method.getName());
            if (method.getName().equals("getBytes")){
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        log("[getBytes before] = "+param.thisObject.toString());
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
//                        log("[getBytes after] = "+param.getResult());
                    }
                });
            }
        }
    }

    private void HookMessageDigest(XC_LoadPackage.LoadPackageParam lpp) {
        log("Hook MessageDigest");
        Class<?> clz = XposedHelpers.findClass("java.security.MessageDigest", lpp.classLoader);
        for (Method method : clz.getDeclaredMethods()) {
//            log("[MessageDigest Method] = "+method.getName());
            if (method.getName().equals("update")){
//                Class<?>[] ptys = method.getParameterTypes();
//                if (ptys != null){
//                    for (Class<?> pty : ptys) {
//                        log("[MessageDigest params] = "+pty.getName());
//                    }
//                }
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        log("[MessageDigest before] = "+new String((byte[]) param.args[0]));
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        log("[MessageDigest after] = "+param.args[0].getClass());
                    }
                });
            }
            if (method.getName().equals("getInstance")){
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        log("[getInstance before] = "+param.args[0]);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        log("[getInstance after] = "+param.args[0]);
                    }
                });
                if (method.getName().equals("digest")) {
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            log("[digest before] = " + param.thisObject);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            log("[digest after] = " + param.thisObject);
                        }
                    });
                }
            }
        }
    }

    private void hookFlash(XC_LoadPackage.LoadPackageParam lpp) {
        log("Hook Flash");
        final Class<?> clz = XposedHelpers.findClass("com.fanwe.hybrid.activity.InitActivity", lpp.classLoader);
        for (Method method : clz.getDeclaredMethods()) {
            log("[Flash Method] = "+method.getName());
            if (method.getName().equals("onCreate")){
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        log("[Flash] = "+param.thisObject);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        for (Field field : clz.getDeclaredFields()) {
                            log("[Flash Field] = "+field.getName()+" className="+field.toString());
                        }
                    }
                });
            }
        }
    }

    private void hookHttpUtils(XC_LoadPackage.LoadPackageParam lpp) {
        log("Hook HttpUtils");
        Class<?> clz = XposedHelpers.findClass("com.fanwe.hybrid.http.AppHttpUtil", lpp.classLoader);
        for (Method method : clz.getDeclaredMethods()) {
            log("[HttpUtils] = "+method.getName());
            if (method.getName().equals("postImpl")){
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        log("[postImpl before params] = "+param.args[0]);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        log("[postImpl after params] = "+param.args[0]);
                    }
                });
            }
        }
    }

    private void hookInit(XC_LoadPackage.LoadPackageParam lpp) {
        log("Hook Init");
        Class<?> clz = XposedHelpers.findClass("com.fanwe.live.common.CommonInterface", lpp.classLoader);
        for (Method method : clz.getDeclaredMethods()) {
            log("[CommonInterface] = "+method.getName());
            if (method.getName().equals("requestInit")){
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        log("[init params] = "+param.args[0]);
                    }
                });
            }
        }
    }

    private void log(String msg){
//        Log.d(TAG, msg);
        XposedBridge.log(msg);
    }

}
