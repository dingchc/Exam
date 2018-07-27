package com.cmcc.library.wrapper.retrofit.util;

import android.os.Environment;

import com.cmcc.library.wrapper.retrofit.CMStaticWrapper;

import java.io.File;

/**
 * SD卡目录创建工具类
 * Created by ding on 11/2/15.
 */
public class CMDirUtil {

    private static final String ROOT = "library";

    private static final String UPLOAD = "upload";

    private static final String DOWNLOAD = "download";

    private static final String UNZIP = "unzip";

    private static final String FILE = "file";

    private static final String LOG = "log";

    /**
     * 获取SD卡存储根目录
     *
     * @return 路径
     */
    public static String getRootDir() {

        if (!isExternalStorageMounted())
            return null;

        String filePath = CMStaticWrapper.getAppContext().getExternalCacheDir() + File.separator + ROOT;

        if (!createFileDir(filePath)) {
            filePath = null;
        }

        return filePath;
    }

    /**
     * 是否外部存储可用
     *
     * @return true 可用， false 不可用
     */
    private static boolean isExternalStorageMounted() {

        return Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡文件上传目录
     *
     * @return 路径
     */
    public static String getUploadDir() {

        if (!isExternalStorageMounted())
            return null;

        String filePath = getRootDir() + File.separator + UPLOAD;

        if (!createFileDir(filePath)) {
            filePath = null;
        }

        return filePath;
    }

    /**
     * 获取SD卡文件下载目录
     *
     * @return 路径
     */
    public static String getDownloadDir() {

        if (!isExternalStorageMounted())
            return null;

        String filePath = getRootDir() + File.separator + DOWNLOAD;

        if (!createFileDir(filePath)) {
            filePath = null;
        }

        return filePath;
    }

    /**
     * 获取SD卡解压目录
     *
     * @return 路径
     */
    public static String getUnZipDir() {

        if (!isExternalStorageMounted())
            return null;

        String filePath = getRootDir() + File.separator + UNZIP;

        if (!createFileDir(filePath)) {
            filePath = null;
        }

        return filePath;
    }

    /**
     * 获取SD卡日志目录
     *
     * @return 路径
     */
    public static String getLogDir() {

        if (!isExternalStorageMounted())
            return null;

        String filePath = getRootDir() + File.separator + LOG;

        if (!createFileDir(filePath)) {
            filePath = null;
        }

        return filePath;
    }

    /**
     * 获取SD卡文件缓存目录
     *
     * @return 路径
     */
    public static String getFileCacheDir() {

        if (!isExternalStorageMounted())
            return null;

        String filePath = getRootDir() + File.separator + FILE;

        if (!createFileDir(filePath)) {
            filePath = null;
        }

        return filePath;
    }

    /**
     * 创建文件目录
     *
     * @param filePath 文件
     * @return true 创建成功或已存在 false 创建失败
     */
    private static boolean createFileDir(String filePath) {

        boolean createSuccess = true;

        if (filePath == null)
            return false;

        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }

        File file = new File(filePath);


        if (!file.exists()) {
            createSuccess = file.mkdirs();
        }

        return createSuccess;
    }


    /**
     * 根据目录名字及文件名，得到正确的全路径字符串
     *
     * @param dir      目录
     * @param fileName 文件名
     * @return 路径
     */
    public static String getValidPath(String dir, String fileName) {

        String path = null;

        if (dir == null || fileName == null) {
            return null;
        }

        if (dir.endsWith(File.separator)) {
            path = dir + fileName;
        } else {
            path = dir + File.separator + fileName;
        }

        return path;
    }


    /**
     * 清除缓存
     *
     * @return
     */
    public static boolean deleteCache() {
        File file = new File(getRootDir());
        return deleteDir(file);
    }

    private static boolean deleteDir(File dir) {
        if (!dir.exists())
            return true;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // 目录此时为空，可以删除
        return dir.delete();
    }
}
