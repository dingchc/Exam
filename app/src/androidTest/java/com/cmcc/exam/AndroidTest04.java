package com.cmcc.exam;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ding on 11/2/17.
 */

@RunWith(AndroidJUnit4.class)
public class AndroidTest04 {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        // 读取Assets目录下的文件
        InputStream is = readAssetFile(appContext, "BleUtils.java");

        // 文件路径
        String outputFile = Environment.getExternalStorageDirectory() + File.separator + "out.txt";

        // 输出文件
        writeOutputFile(is, outputFile);
    }

    /**
     * 读取Asset中的文件
     *
     * @param appContext 上下文
     * @param fileName   文件名
     * @return 输入流
     */
    private InputStream readAssetFile(Context appContext, String fileName) {

        AssetManager assetManager = appContext.getAssets();

        InputStream is = null;
        try {
            is = assetManager.open(fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return is;
    }

    /**
     * 输出文件
     *
     * @param filePath 文件路径
     */
    private void writeOutputFile(InputStream is, String filePath) {

        FileOutputStream fos = null;

        if (is == null || TextUtils.isEmpty(filePath)) {
            Log.i("tag", "input params is not valid");
            return;
        }

        try {

            fos = new FileOutputStream(new File(filePath));

            int data;

            while ((data = is.read()) > 0) {

                fos.write(data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
