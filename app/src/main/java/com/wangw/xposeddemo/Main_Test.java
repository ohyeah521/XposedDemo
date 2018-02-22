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
 * Created by wangw on 2018/2/2.
 */
@Deprecated
public class Main_Test implements IXposedHookLoadPackage {

    XC_LoadPackage.LoadPackageParam mLpp;

    String iApp = "s.h.e.l.l.S";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpp) {
        Log.d("XXXXP", "handleLoadPackage: "+lpp.packageName);
        if (!(lpp.packageName.equals("com.uzero.poem")) )
            return;
        mLpp = lpp;
        log("classLoader = "+mLpp.classLoader+" | processName = "+mLpp.processName+" | appInfo = "+mLpp.appInfo+" | appInfoClassName = "+mLpp.appInfo.className);
        HookDex();
//        HookAttachBase();
        HookUtitls();

//        try {
//            HookApp2();
//            hookApp();
////            HookMessageDigest(mLpp.classLoader);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        try {
//            HookString(mLpp);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        new Thread(new TestRun())
//                .start();
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
                            log(param.method+" Before");
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            log(param.method+"  After");
                        }
                    });
            }
        } catch (ClassNotFoundException e) {
            log("Hook Dex 失败");
            e.printStackTrace();
        }
    }

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
//                    if (param.method.getName().equals("l")){
//                        HookUtitls(param.args[0]);
//                    }
                }


            });
        }
    }
    private void HookUtitls() {
        final ClassLoader cl = mLpp.classLoader;//context.getClassLoader();
        Class<?> clz = XposedHelpers.findClass("com.uzero.poem.LvBokeAuthUtils",cl);
        for (Field field : clz.getDeclaredFields()) {
            log("[PoemApp Field] = "+field);
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

    class TestRun implements Runnable{

        @Override
        public void run() {

            log("进入线程等待。。。");
            try {
                Thread.sleep(1000*30);
            } catch (InterruptedException e) {
                log("Sleep 异常");
                e.printStackTrace();
            }finally {
                log("Sleep 结束");
                afterHook();
            }


//            HookMessageDigest(mLpp);
        }
    }

    private void hookApp() {
        log("Hook App");
        XposedHelpers.findAndHookMethod("s.h.e.l.l.S",mLpp.classLoader,"onCreate",new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                log("Hook App Before = "+param.thisObject);

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                log("Hook App After");
                for (Field field : param.thisObject.getClass().getDeclaredFields()) {
                    log("[App field] = "+field.getName()+" | v = ");
                }

            }
        });
    }

    private void afterHook() {

    }

    private void HookMessageDigest(ClassLoader cl) {
        log("Hook MessageDigest");
        Class<?> clz = XposedHelpers.findClass("java.security.MessageDigest", cl);
        for (Method method : clz.getDeclaredMethods()) {
            log("[MessageDigest Method] = "+method.getName());
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

    private void HookApp2() {
        log("Hook App2");
        XposedHelpers.findAndHookMethod("android.app.Application",mLpp.classLoader, "attach", Context.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                log("Hook App2 Before = "+param.thisObject);
                HookMessageDigest(param.thisObject.getClass().getClassLoader());
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

}
