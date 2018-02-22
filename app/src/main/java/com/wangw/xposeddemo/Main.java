package com.wangw.xposeddemo;

import android.util.Log;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by wangw on 2017/12/19.
 */
@Deprecated
public class Main implements IXposedHookLoadPackage{


    private static final String TAG = "Xposed";
    private String hookClz = "com.wangw.huolang.momoxia.ApkConstant";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.wangw.huolang")) {
            log("开始Hook");

            try {
                findAndHookMethod(lpparam);
            }catch (Exception e){
                log("findAndHookMethod 失败+"+e.getMessage());
            }

            try{
                hookMthods(lpparam);
            }catch (Exception e){
                log("HookMethod 失败 = "+e);
            }


        }
    }

    private void hookMthods(final XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {

        log("hookMthods");
        Class<?> clz = XposedHelpers.findClass(hookClz,lpparam.classLoader);
        log("已找到指定Class ="+clz);
        for (Method method : clz.getDeclaredMethods()) {
            log("method = "+method.getName());
            if (method.getName().equals("switchEnv")){
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        log("Before");
                        log("params = "+param.args[0]);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        log("After");
                        log("params = "+param.args[0]);
                    }
                });
            }
        }

    }

    private void findAndHookMethod(final XC_LoadPackage.LoadPackageParam lpparam) {
        log("findAndHookMethod");
            XposedHelpers.findAndHookMethod(hookClz, lpparam.classLoader, "getTitle", new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object result = param.getResult();
                    log("result = " + result);
                    param.setResult(result + "_xPosed");
                    log("修改后的Result = " + param.getResult());
                }
            });
    }


    private void log(String msg){
        Log.d(TAG, msg);
    }
    
}
