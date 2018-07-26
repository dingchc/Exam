package com.aspirecn.library.wrapper.retrofit.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by ding on 1/4/17.
 */

public class MSUtil {

    public static DateFormat ymdhmsDateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static DateFormat ymdhmDateFormat =
            new SimpleDateFormat("yy.MM.dd HH:mm", Locale.getDefault());


    /**
     * 检查对象是否为空
     *
     * @param obj 对象
     * @return true 不为空、 false为空
     */
    public static boolean checkObjNotNull(Object obj) {

        return obj == null ? false : true;
    }

    /**
     * 检查list是否为空
     *
     * @param dataList 数据表
     * @return true 不为空、 false为空
     */
    public static boolean checkListNotNull(List dataList) {

        return dataList != null;
    }

    /**
     * 检查list size是否大于0
     *
     * @param dataList 数据表
     * @return true 大于0、 false 小于等于0
     */
    public static boolean checkListNotEmpty(List dataList) {

        return (checkListNotNull(dataList) && dataList.size() > 0) ? true : false;
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return true 文件存在、 false 文件不存在
     */
    public static boolean checkFileExist(String filePath) {

        boolean ret = false;
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            ret = file.exists() && file.isFile();
        }

        return ret;
    }

    /**
     * 保存文件
     *
     * @param inputStream    输入流
     * @param path           输出文件路径
     * @param isSupportRange 是否支持断点
     * @return true 保存成功、false 保存失败
     */
    public static boolean writeFile(InputStream inputStream, String path, boolean isSupportRange) {

        boolean ret = false;

        File saveFile = new File(path);

        OutputStream outputStream = null;

        try {
            byte[] buffer = new byte[4096];

            outputStream = new FileOutputStream(saveFile, isSupportRange);

            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) {
                    break;
                }
                outputStream.write(buffer, 0, read);

            }

            outputStream.flush();

            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    /**
     * @param tempPath 临时文件路径
     * @param destPath 目标文件路径
     * @return true 成功、false 失败
     */
    public static boolean moveFile(String tempPath, String destPath) {

        if (TextUtils.isEmpty(tempPath) || TextUtils.isEmpty(destPath)) {
            return false;
        }

        File file = new File(tempPath);

        return file.renameTo(new File(destPath));
    }

    /**
     * 压缩并保存文件
     *
     * @param json 字符串
     * @return 文件路径
     */
    public static boolean saveFile(String json, String path) {

        boolean ret = true;

        FileOutputStream fos = null;

        try {

            byte[] dataArray = json.getBytes("utf-8");

            fos = new FileOutputStream(new File(path));

            fos.write(dataArray);
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    /**
     * 压缩并保存文件
     *
     * @param json 字符串
     * @return 文件路径
     */
    public static boolean zipToSaveFile(String json, String path) {

        boolean ret = true;

        FileOutputStream fos = null;

        try {

            byte[] dataArray = MSZLibUtil.compress(json.getBytes("utf-8"));

            fos = new FileOutputStream(new File(path));

            fos.write(dataArray);
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    /**
     * 解压文件并存储
     *
     * @param inputFilePath 压缩文件地址
     * @return 解压后的文件地址
     */
    public static String unzipToFile(String inputFilePath) {

        String outputPath = "";
        FileOutputStream fos = null;
        FileInputStream fis = null;

        try {
            byte[] data = readBytesFromFile(inputFilePath);

            byte[] unzipDataArray = MSZLibUtil.decompress(data);

            String fileName = MSUtil.getFileName(inputFilePath);

            outputPath = MSDirUtil.getValidPath(MSDirUtil.getUnZipDir(), fileName);

            fos = new FileOutputStream(new File(outputPath));

            fos.write(unzipDataArray);

            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return outputPath;
    }

    /**
     * 从文件中读取字节数组
     *
     * @param filePath 文件路径
     * @return 字节数组
     */
    public static byte[] readBytesFromFile(String filePath) {

        FileInputStream fis = null;

        byte[] data = null;

        try {

            fis = new FileInputStream(filePath);
            data = new byte[fis.available()];

            fis.read(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    /**
     * 读取文件内容到字符串
     *
     * @param filePath 文件路径
     * @return 字符串
     */
    public static String readStringFromFile(String filePath) {

        String ret = "";

        try {
            ret = new String(readBytesFromFile(filePath), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 获取厂商，型号，sdk版本
     */
    public static String getMobileModel() {

        return Build.MANUFACTURER + ", " + Build.MODEL + ", " + Build.VERSION.RELEASE;
    }

    // 根据指定格式返回相应的日期字符串
    public static String getSpecialFromData(DateFormat dataformat, Date date) {

        return dataformat.format(date);
    }

    public static String getFormatTime(int time) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SSS");

        return sdf.format(Math.abs(time));
    }

    public static String getFormatNumber(int time) {
        DecimalFormat format = new DecimalFormat("000");
        return format.format(time);
    }

    /**
     * 日期转时间
     *
     * @param date 日期
     * @return 时间字符串
     */
    public static String dateToString(Date date, String format) {

        String dateString = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            dateString = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateString;
    }

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date stringToDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 解析单精度字符串
     *
     * @param value 字符串
     * @return 数值
     */
    public static float parseFloat(String value) {

        float ret = 0.0f;

        try {
            if (!TextUtils.isEmpty(value)) {
                ret = Float.parseFloat(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 解析长整型字符串
     *
     * @param value 字符串
     * @return 数值
     */
    public static long parseLong(String value) {

        long ret = 0;

        try {
            if (!TextUtils.isEmpty(value)) {
                ret = Long.parseLong(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 解析整型字符串
     *
     * @param value 字符串
     * @return 数值
     */
    public static int parseInt(String value) {

        int ret = 0;

        try {
            if (!TextUtils.isEmpty(value)) {
                ret = Integer.parseInt(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String filePath) {

        String ret = null;

        if (!TextUtils.isEmpty(filePath)) {

            int separatorIndex = filePath.lastIndexOf("/");

            if (separatorIndex > 0) {

                ret = filePath.substring(separatorIndex + 1);
            }
        }

        //        MSAppLogger.i(MSAppLogger.TAG, "ret=" + ret);
        return ret;
    }

    /**
     * 根据文件名，生成temp文件名
     *
     * @param fileName 文件名
     * @return temp文件名
     */
    public static String createTempFileName(String fileName) {

        String tempFileName = "";

        if (!TextUtils.isEmpty(fileName)) {

            tempFileName = fileName.concat(".temp");
        } else {
            tempFileName = ".temp";
        }

        return tempFileName;
    }


    /**
     * 获取字符串，为null返回空字符串
     *
     * @param content 内容
     * @return 字符串
     */
    public static String getEmptyString(String content) {

        String ret = "";

        return TextUtils.isEmpty(content) ? ret : content;
    }

    /**
     * 检查传入地址是否为web地址
     *
     * @param url 地址
     * @return true 是、false 否
     */
    public static boolean isWebUrl(String url) {

        boolean ret = false;

        if (!TextUtils.isEmpty(url) && (url.startsWith("http:") || url.startsWith("https:"))) {

            ret = true;
        }

        return ret;
    }

    /**
     * 检查需要Split的字符串，是否合法
     *
     * @param input 输入
     * @param split 分隔符
     */
    public static boolean checkSplitValid(String input, String split) {

        boolean ret = false;

        if (!TextUtils.isEmpty(input) && input.indexOf(split) > 0) {
            ret = true;
        }

        return ret;
    }

    /**
     * 检查密码强弱性
     */
    public static boolean checkPwdStrong(String str) {
        boolean ret = false;
        String regex = "^[a-zA-Z0-9]{6,16}";
        if (Pattern.compile(regex).matcher(str).matches()) {
            ret = true;
            return ret;
        }
        return ret;
    }

    public static long convertToPhoneNum(String num) {
        long phonenum = 0;
        String regex = "^[1][3-8]+\\d{9}";
        try {
            if (num.matches(regex))
                phonenum = Long.parseLong(num);
        } catch (Exception e) {
            MSAppLogger.e("error phone num");
        }
        return phonenum;
    }

    /**
     * 当前版本名称
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return verName;
    }

    /**
     * 判断是否同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }

    public static String stringFilter(String str) throws PatternSyntaxException {
//        String regEx = "[/\\:*?<>|\"\n\t]";
        String regEx = "[\n\t]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    /**
     * SDcard是否可用
     *
     * @return true 可用、false 不可用
     */
    public static boolean isSdcardExists() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 删除一个文件
     * @param path 文件路径
     */
    public static void deleteAFile(String path) {

        try {
            MSAppLogger.i("dcc", "Path=" + path);
            File file = new File(path);
            if (file.isFile() && file.exists()) {
                file.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取ContentRange字段中，服务器提供的下载开始的size
     * 例如：(bytes 1017492-8165173/8165174)
     * @param contentRange 服务器返回的contentRange
     * @return 下载开始的size
     */
    public static long getRangeStartSize(String contentRange) {

        MSAppLogger.i("getRangeStartSize");

        long rangeStart = 0;

        try {

            if (!TextUtils.isEmpty(contentRange)) {

                int start = contentRange.indexOf(" ");
                int end = contentRange.indexOf("-");

//                MSAppLogger.i("start="+start + ", end=" + end);

                if (start > 0 && end > 0) {
                    String rangeStartString = contentRange.substring(start + 1, end);
                    rangeStart = parseLong(rangeStartString);

//                    MSAppLogger.i("rangeStartString="+rangeStartString+", rangeStart="+rangeStart);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rangeStart;
    }

}
