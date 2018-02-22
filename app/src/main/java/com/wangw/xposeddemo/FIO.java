package com.wangw.xposeddemo;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by wangw on 2018/2/13.
 */

public class FIO
{

    public static void writeByte(byte[] bArr, String str) {
        byte[] bArr2 = bArr;
        try {
            OutputStream outputStream2 = new FileOutputStream(str);
            outputStream2.write(bArr2);
            outputStream2.close();
        } catch (Exception e) {
            Exception exception = e;
        }
    }
}
