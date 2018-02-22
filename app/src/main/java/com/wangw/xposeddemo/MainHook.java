//package com.wangw.xposeddemo;
//
//import de.robv.android.xposed.IXposedHookLoadPackage;
//
//import com.wangw.xposeddemo.MainActivity;
//
//import java.io.File;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//
//import de.robv.android.xposed.IXposedHookLoadPackage;
//import de.robv.android.xposed.XC_MethodHook;
//import de.robv.android.xposed.XC_MethodHook.Unhook;
//import de.robv.android.xposed.XSharedPreferences;
//import de.robv.android.xposed.XposedBridge;
//import de.robv.android.xposed.XposedHelpers;
//import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
//
//public class MainHook implements IXposedHookLoadPackage {
//    Class Dex;
//    Method Dex_getBytes;
//    Method getDex = ((Method) null);
//    XSharedPreferences shared;
//
//    class AnonymousClass100000000 extends XC_MethodHook {
//        private final MainHook this$0;
//        private final String val$aim;
//        private final LoadPackageParam val$p1;
//
//        AnonymousClass100000000(MainHook mainHook, String str, LoadPackageParam loadPackageParam) {
//            String str2 = str;
//            LoadPackageParam loadPackageParam2 = loadPackageParam;
//            XC_MethodHook xC_MethodHook = this;
//            this.this$0 = mainHook;
//            this.val$aim = str2;
//            this.val$p1 = loadPackageParam2;
//        }
//
//        @Override
//        protected void afterHookedMethod(MethodHookParam methodHookParam) {
//            AnonymousClass100000000 anonymousClass100000000 = this;
//            MethodHookParam methodHookParam2 = methodHookParam;
//            XposedBridge.log(" after hook : ");
//            Object obj = 1;
//            Class cls = (Class) methodHookParam2.getResult();
//            if (cls != null) {
//                try {
//                    String name = cls.getName();
//                    ClassLoader classLoader = Class.forName("formatfa.xposed.Fdex2.MainHook").getClassLoader();
//                    Class cls2 = Class.forName(name, false, ClassLoader.getSystemClassLoader());
//                } catch (Throwable e) {
//                    Throwable th = e;
//                    NoClassDefFoundError noClassDefFoundError = r15;
//                    NoClassDefFoundError noClassDefFoundError2 = new NoClassDefFoundError(th.getMessage());
//                    throw noClassDefFoundError;
//                } catch (ClassNotFoundException e2) {
//                    ClassNotFoundException classNotFoundException = e2;
//                    obj = null;
//                }
//                if (obj == null) {
//                    try {
//                        byte[] bArr = (byte[]) anonymousClass100000000.this$0.Dex_getBytes.invoke(
//                                anonymousClass100000000.this$0.getDex.invoke(
//                                        cls, new Object[0]),
//                                new Object[0]);
//                        if (bArr != null) {
//                            StringBuffer stringBuffer = r15;
//                            StringBuffer stringBuffer2 = new StringBuffer();
//                            stringBuffer2 = r15;
//                            StringBuffer stringBuffer3 = new StringBuffer();
//                            stringBuffer3 = r15;
//                            StringBuffer stringBuffer4 = new StringBuffer();
//                            stringBuffer4 = r15;
//                            StringBuffer stringBuffer5 = new StringBuffer();
//                            stringBuffer5 = r15;
//                            StringBuffer stringBuffer6 = new StringBuffer();
//                            String stringBuffer7 = stringBuffer.append(stringBuffer2.append(stringBuffer3.append(stringBuffer4.append(stringBuffer5.append("/data/data/").append(anonymousClass100000000.val$aim).toString()).append("/").toString()).append(anonymousClass100000000.val$aim).toString()).append(bArr.length).toString()).append(".dex").toString();
//                            File file = r15;
//                            String string = anonymousClass100000000.this$0.shared.getString(MainActivity.DIR, "/sdcard");
//                            stringBuffer4 = r15;
//                            stringBuffer5 = new StringBuffer();
//                            stringBuffer5 = r15;
//                            stringBuffer6 = new StringBuffer();
//                            File file2 = new File(string, stringBuffer4.append(stringBuffer5.append(anonymousClass100000000.val$p1.packageName).append(bArr.length).toString()).append(".dex").toString());
//                            File file3 = file;
//                            if (!file3.exists()) {
//                                FIO.writeByte(bArr, file3.getAbsolutePath());
//                            }
//                        }
//                    } catch (Exception e3) {
//                        XposedBridge.log(e3.toString());
//                    }
//                }
//            }
//        }
//
//        @Override
//        protected void beforeHookedMethod(MethodHookParam methodHookParam) {
//        }
//    }
//
//    public MainHook() {
//        MainHook mainHook = this;
//    }
//
//    @Override
//    public void handleLoadPackage(LoadPackageParam loadPackageParam) {
//        LoadPackageParam loadPackageParam2 = loadPackageParam;
//        XSharedPreferences xSharedPreferences = r16;
//        XSharedPreferences xSharedPreferences2 = new XSharedPreferences("formatfa.xposed.Fdex2", "package");
//        this.shared = xSharedPreferences;
//        this.shared.reload();
//        initRefect();
//        String string = this.shared.getString(MainActivity.KEY, (String) null);
//        if (string == null) {
//            XposedBridge.log("没有指定apk,请打开模块选择要脱dex的apk");
//        } else if (loadPackageParam2.packageName.equals(string)) {
//            StringBuffer stringBuffer = r16;
//            StringBuffer stringBuffer2 = new StringBuffer();
//            XposedBridge.log(stringBuffer.append(string).append(" has hook").toString());
//            String str = "java.lang.ClassLoader";
//            ClassLoader classLoader = loadPackageParam2.classLoader;
//            String str2 = "loadClass";
//            Class[] clsArr = new Object[3];
//            Class[] clsArr2 = clsArr;
//            try {
//                clsArr[0] = Class.forName("java.lang.String");
//                clsArr = clsArr2;
//                clsArr2 = clsArr;
//                clsArr[1] = Boolean.TYPE;
//                clsArr = clsArr2;
//                clsArr2 = clsArr;
//                Class[] clsArr3 = clsArr;
//                AnonymousClass100000000 anonymousClass100000000 = r16;
//                AnonymousClass100000000 anonymousClass1000000002 = new AnonymousClass100000000(r0, string, loadPackageParam2);
//                clsArr3[2] = anonymousClass100000000;
//                Unhook findAndHookMethod = XposedHelpers.findAndHookMethod(str, classLoader, str2, clsArr2);
//            } catch (Throwable e) {
//                Throwable th = e;
//                NoClassDefFoundError noClassDefFoundError = r16;
//                NoClassDefFoundError noClassDefFoundError2 = new NoClassDefFoundError(th.getMessage());
//                throw noClassDefFoundError;
//            }
//        }
//    }
//
//    public void initRefect() {
//        try {
//            this.Dex = Class.forName("com.android.dex.Dex");
//            r0.Dex_getBytes = Dex.getDeclaredMethod("getBytes", new Class[0]);
//            if (!MainActivity.h.endsWith("0")) {
//                return;
//            }
//            if (MainActivity.s.length() == 91) {
//                r0.getDex = Class.forName("java.lang.Class").getDeclaredMethod("getDex", new Class[0]);
//            }
//        } catch (Throwable e) {
//            Throwable th = e;
//            NoClassDefFoundError noClassDefFoundError = r8;
//            NoClassDefFoundError noClassDefFoundError2 = new NoClassDefFoundError(th.getMessage());
//            throw noClassDefFoundError;
//        } catch (Exception e2) {
//            XposedBridge.log(e2.toString());
//        }
//    }
//
//    void writeDex(String str, Object obj) {
//        String str2 = str;
//        try {
//            byte[] bArr = (byte[]) Dex_getBytes.invoke(this.getDex.invoke(obj.getClass(), new Object[0]), new Object[0]);
//            if (bArr != null) {
//                File file = r13;
//                String string = r0.shared.getString(MainActivity.DIR, "/sdcard");
//                StringBuffer stringBuffer = r13;
//                StringBuffer stringBuffer2 = new StringBuffer();
//                stringBuffer2 = r13;
//                StringBuffer stringBuffer3 = new StringBuffer();
//                File file2 = new File(string, stringBuffer.append(stringBuffer2.append(str2).append(bArr.length).toString()).append(".dex").toString());
//                File file3 = file;
//                if (!file3.exists()) {
//                    FIO.writeByte(bArr, file3.getAbsolutePath());
//                }
//            }
//        } catch (InvocationTargetException e) {
//            InvocationTargetException invocationTargetException = e;
//        } catch (IllegalAccessException e2) {
//            IllegalAccessException illegalAccessException = e2;
//        } catch (IllegalArgumentException e3) {
//            IllegalArgumentException illegalArgumentException = e3;
//        }
//    }
//}