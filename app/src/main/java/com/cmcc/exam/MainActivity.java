package com.cmcc.exam;

import android.animation.Animator;
import android.bluetooth.BluetoothDevice;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewPropertyAnimator;

import com.cmcc.exam.ble.BleUtil;
import com.cmcc.exam.ble.SportSdk;
import com.cmcc.exam.db.AppDatabase;
import com.cmcc.exam.db.dao.ClassDao;
import com.cmcc.exam.db.entry.ClassEntry;
import com.cmcc.exam.db.entry.UserEntry;
import com.cmcc.exam.net.MySocketClient;
import com.cmcc.exam.net.ProtocolUtil;
import com.cmcc.exam.net.TcpClient;
import com.cmcc.library.wrapper.retrofit.CMHttpController;
import com.cmcc.library.wrapper.retrofit.listener.CMHttpCallback;
import com.cmcc.library.wrapper.retrofit.model.CMBaseResponse;
import com.cmcc.library.wrapper.retrofit.util.CMAppLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    WeakReference<String> sr = new WeakReference<String>(new String("hello"));
    ScheduledExecutorService executorService;

    TimerTask timerTask;

    Future future1;

    boolean flag = false;

    private MySocketClient client = new MySocketClient();

    private TcpClient tcpClient = new TcpClient();

    private static int mIndex = 0;

    private Line2TextView line2TextView;

    private View llBody;

    private int state;

    private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        line2TextView = (Line2TextView) findViewById(R.id.line2TextView);

        llBody = findViewById(R.id.ll_body);

        line2TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tcpClient.isConnected()) {
                    tcpClient.disconnect();
                }
            }
        });

        final ViewPropertyAnimator animator = llBody.animate();

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                if (state == 0) {

                } else {
                    llBody.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if (state == 0) {
                    state = 1;
                    llBody.setVisibility(View.GONE);
                } else {
                    state = 0;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

//                printSystem2();

//                if (state == 0) {
//                    animator.translationY(-llBody.getHeight());
//                } else {
//                    animator.translationY(llBody.getHeight());
//                }
//
//                startActivity(new Intent(MainActivity.this, CoordinatorActivity.class));

                getTicket();
            }
        });

    }


    private void getTicket() {

        String url = "https://edu.10086.cn" + "/eduapi/login/user/login";

        CMHttpController.INSTANCE.doPost(url, new TreeMap<String, String>(), new CMHttpCallback() {
            @Override
            public void onSuccess(CMBaseResponse response, String json) {

                CMAppLogger.i("json="+json);
            }

            @Override
            public void onException(Throwable e) {

                e.printStackTrace();
            }
        });
    }

    private void socket() {

        tcpClient.connect("10.2.14.103", 36330, 10000, new TcpClient.TcpClientListener() {
            @Override
            public void onConnected() {
                AppLogger.i("onConnected");
            }

            @Override
            public void onDisconnected() {
                AppLogger.i("onDisconnected");
            }

            @Override
            public void onTimeout() {
                AppLogger.i("onTimeout");
            }

            @Override
            public void onReceiveData(byte[] data) {

                if (data != null) {
                    ProtocolUtil protocolUtil = new ProtocolUtil();
                    ProtocolUtil.Protocol protocol = protocolUtil.unpack(data);

                    AppLogger.i("data=" + ProtocolUtil.getUtfString(protocol.getData()) + "[*]");
                }

            }
        });
    }

    private void scanDevice() {

        SportSdk.INSTANCE.init();

        SportSdk.INSTANCE.addBleListener(new BleUtil.BleListener() {
            @Override
            public void onScanFinish(List<BluetoothDevice> deviceList) {

                for (BluetoothDevice device : deviceList) {
                    AppLogger.i("device=" + device.getName() + ", " + device.getAddress());
                }

                // 手表
                SportSdk.INSTANCE.createBLEConnection("FD:67:CE:ED:6C:42", new BleUtil.BleConfigUUID("6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", "6e400003-b5a3-f393-e0a9-e50e24dcca9e"));

            }

            @Override
            public void onError(BleUtil.BleResult result) {
                AppLogger.i("errorCode=" + result.errorCode + ", errorMsg=" + result.errorMsg);
            }
        });

        SportSdk.INSTANCE.searchBleDevices(3000, null);
    }

    private void getUser() {

        AppDatabase appDatabase = MsApplication.getDb();

        appDatabase.userDao().getAllUserRx().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<UserEntry>>() {
            @Override
            public void accept(List<UserEntry> userEntries) throws Exception {
                AppLogger.i("userEntries= " + userEntries.size());
            }
        });
    }

    private void insertDbRx() {

        io.reactivex.Observable.just("").map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {

                AppDatabase appDatabase = MsApplication.getDb();

                appDatabase.beginTransaction();

                for (int i = 1; i < 10000; i++) {

                    UserEntry user = new UserEntry(i, "用户" + i, i, 1 + "");

                    appDatabase.userDao().insertUser(user);
                }
                appDatabase.setTransactionSuccessful();
                appDatabase.endTransaction();

                return 1;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {

            @Override
            public void accept(Integer o) throws Exception {

                AppLogger.i("complete");
            }
        });
    }

    public void printSystem() {

        Properties properties = System.getProperties();


        if (properties != null) {

            properties.list(System.out);
        }
    }

    public void printSystem2() {

        InputStream is = null;
        try {
            Properties properties = new Properties();
            File file = new File(Environment.getRootDirectory(), "build.prop");

            AppLogger.i("file=" + file.getAbsolutePath());
            is = new FileInputStream(file);
            properties.load(is);

            String miUiVersionCode = properties.getProperty("ro.miui.ui.version.code");
            String miUiVersionName = properties.getProperty("ro.miui.ui.version.name");

            AppLogger.i("miUiVersionCode=" + miUiVersionCode + ", miUiVersionName=" + miUiVersionName);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void testDb() {

        AppDatabase appDatabase = MsApplication.getDb();

        AppLogger.i("begin ... ");

        insertDb();

        AppLogger.i("end ... ");

        List<UserEntry> userList = appDatabase.userDao().getAllUser();

        AppLogger.i("userList.size()=" + userList.size());

    }

    private void insertDb() {

        AppDatabase appDatabase = MsApplication.getDb();

        appDatabase.beginTransaction();

        for (int i = 1; i < 10000; i++) {

            UserEntry user = new UserEntry(i, "用户" + i, i, 1 + "");

            appDatabase.userDao().insertUser(user);
        }
        appDatabase.setTransactionSuccessful();
        appDatabase.endTransaction();
    }

    private void insertClassEntry() {

        AppDatabase appDatabase = MsApplication.getDb();

        ClassEntry classEntry = new ClassEntry(122, "一年级一班", 1);

        long id = appDatabase.classDao().insertClassMember(classEntry);

        AppLogger.i("id=" + id);

        List<ClassEntry> classEntryList = appDatabase.classDao().getAllClass();
        AppLogger.i("classEntryList.size()=" + classEntryList.size());

        List<ClassDao.ClassMember> memberList = appDatabase.classDao().getClassMember(111);

        ClassDao.ClassMember member = memberList.get(0);

        AppLogger.i("className=" + member.className + ", userName=" + member.userName);

    }

    private void insertClassEntryRx() {

        AppDatabase appDatabase = MsApplication.getDb();

        ClassEntry classEntry = new ClassEntry(122, "一年级一班", 1);

//        appDatabase.classDao().insertClassMember2(classEntry).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
//            @Override
//            public void accept(Long aLong) throws Exception {
//                AppLogger.i("id=" + aLong);
//            }
//        });


        List<ClassEntry> classEntryList = appDatabase.classDao().getAllClass();
        AppLogger.i("classEntryList.size()=" + classEntryList.size());

        List<ClassDao.ClassMember> memberList = appDatabase.classDao().getClassMember(111);

        ClassDao.ClassMember member = memberList.get(0);

        AppLogger.i("className=" + member.className + ", userName=" + member.userName);

    }

    private void testTcp() {

        ProtocolUtil protocol = new ProtocolUtil();

        ProtocolUtil.Protocol req = new ProtocolUtil.Protocol();

        try {
            byte[] data = ("[" + mIndex + "]" + ProtocolUtil.data).getBytes("utf-8");
            req.setData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] outputData = protocol.pack(req);

        if (tcpClient.isConnected()) {
            tcpClient.writeData(outputData);
        }
    }

    private void testA() {

//        "A007e2011d0a340c5A"
        byte[] array = hexString2Bytes("A007e2011d0a340c5A");

        for (byte value : array) {
            AppLogger.i("value=" + value);
        }
    }

    public static byte[] hexString2Bytes(String hexStr) {
        byte[] b = new byte[hexStr.length() / 2];
        int j = 0;

        for (int i = 0; i < b.length; ++i) {
            char c0 = hexStr.charAt(j++);
            char c1 = hexStr.charAt(j++);
            b[i] = (byte) (parse(c0) << 4 | parse(c1));
        }

        return b;
    }

    private static int parse(char c) {
        return c >= 97 ? c - 97 + 10 & 15 : (c >= 65 ? c - 65 + 10 & 15 : c - 48 & 15);
    }

    private void test() {

        try {
            Class clazz = Class.forName("com.cmcc.exam.MyUser");

            Object obj = clazz.newInstance();
            Method method = clazz.getDeclaredMethod("reset");
            method.setAccessible(true);
            method.invoke(obj);


            for (Method mm : clazz.getMethods()) {
                AppLogger.i("method name= " + mm.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void test33() {

        List<? extends String> extendsStringList = new ArrayList<>();

//        extendsStringList.add("111"); //Error
//        extendsStringList.set(0, "2222"); //Error

        List<? super String> superStringList = new ArrayList<>();

        superStringList.add("111");
//        stringList.get(0).sub

        List<Object> objectList = new ArrayList<>();

        objectList.addAll(superStringList);
    }


    private void getAppList() {

        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                System.out.println("MainActivity.getAppList, packageInfo=" + packageInfo.packageName + ", " + packageInfo.applicationInfo.loadLabel(pm));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void scheduleTimer() {

        Log.i("dcc", "* start ");

        ThreadFactory threadFactory = new MsThreadFactoryBuilder.Builder().builder();

        if (executorService != null) {
            executorService.shutdownNow();
        }

        executorService = new ScheduledThreadPoolExecutor(1, threadFactory);

        Log.i("dcc", "isTerminated=" + executorService.isTerminated() + ", isShutdown=" + executorService.isShutdown());

        executorService.schedule(new Runnable() {

            int value = 0;

            @Override
            public void run() {

//                while (true) {
                value++;
                Log.i("dcc", "*" + Thread.currentThread().getName() + " value = " + value);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                }

            }
        }, 5, TimeUnit.SECONDS);

        executorService.shutdown();

    }

    private void timerTask() {


        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i("dcc", "* before cancel");

                Log.i("dcc", "* after cancel");
            }
        }, 0);

    }

    private void scheduleTimer2() {

        Log.i("dcc", "* start ");

        ThreadFactory threadFactory = new MsThreadFactoryBuilder.Builder().builder();

        if (executorService == null) {
            executorService = new ScheduledThreadPoolExecutor(1, threadFactory);
        }

        timerTask = new TimerTask() {

            int value = 0;

            @Override
            public void run() {

                while (true) {
                    value++;
                    Log.i("dcc", "*" + Thread.currentThread().getName() + " value = " + value);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        Log.i("dcc", "isTerminated=" + executorService.isTerminated() + ", isShutdown=" + executorService.isShutdown());

        executorService.schedule(timerTask, 5, TimeUnit.SECONDS);

//        executorService.shutdown();

    }

    private void scheduleCall() {

        ThreadFactory threadFactory = new MsThreadFactoryBuilder.Builder().builder();

        if (executorService == null) {
            executorService = new ScheduledThreadPoolExecutor(1, threadFactory);
        }

        future1 = executorService.submit(new Callable<Object>() {

            int cnt = 10;

            @Override
            public Object call() throws Exception {

                int i = 0;

                while (i < cnt) {
                    Log.i("dcc", "future1 i=" + i);
                    i++;

                    Thread.sleep(1000);
                }
                return i;
            }
        });

        try {

            
            Log.i("dcc", "before get");

            Integer value = (Integer) future1.get();

            Log.i("dcc", "after get = " + value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


//        executorService.shutdown();

    }

    private void showBottomDialog() {

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setTitle("提示");
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.executor_layout);

        dialog.show();

    }

}
