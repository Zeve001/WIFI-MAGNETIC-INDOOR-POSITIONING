package com.example.zeven.knn_magnetic;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class Scan_id_Activity extends Activity {

    TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    Routes routelist;

    boolean scanFinished = false;


    File a;
    int i=1;

    StringBuilder sb = new StringBuilder();
    StringBuilder csv = new StringBuilder();

    private Button Back;
    private Button Start;
    private Button Stop;
    private Button Record;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler.postDelayed(this, 1000 * 1);// 间隔120秒
        }
        void update() {
            mainWifi.startScan();
            //mainText.setText("Starting Scan...\n");
            //mainText.setText(routelist.returnlist());
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanid);

        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mainWifi.setWifiEnabled(true);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = MainActivity.ProjectName + "_Routes";
        a= FileUtils.CreateFile(path + "/demos/file/"+ fileName +".txt");


        //mainWifi.startScan();
        //mainText.setText("Starting Scan...\n");

        routelist = new Routes();

        Back = (Button)findViewById(R.id.button3);
        Start = (Button)findViewById(R.id.button4);
        Stop = (Button)findViewById(R.id.button7);
        Record = (Button)findViewById(R.id.button8);

        mainText = (TextView) findViewById(R.id.textView2);
        mainText.setMovementMethod(ScrollingMovementMethod.getInstance());




        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent();
                intent .setClass(Scan_id_Activity.this,MainActivity.class);
                startActivity(intent );
                Scan_id_Activity.this.finish();
            }
        });

        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.postDelayed(runnable, 1000 * 1);
                //mainWifi.startScan();
                //mainText.setText("AAA\n");


                }
        });

        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeMessages(0);
                mainText.setText(routelist.returnlist());
            }
        });

        Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    BufferedWriter fout;
                    fout = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(a, true)));
                    //fout.write(routelist.return_length() + "\n");
                    fout.write(routelist.returnlist().toString());
                    fout.close();
                    mainText.setText("Successful record routes list.\n");
                    //fout.write(Integer.parseInt("123"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }




    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifi);

        // To return CSV-formatted text back to calling activity (e.g., MIT App
        // Inventor App)
        Intent scanResults = new Intent();
        scanResults.putExtra("AP_LIST", csv.toString());
        setResult(RESULT_OK, scanResults);
        finish();
    }

    protected void onResume() {
        super.onResume();
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        // if (scanFinished == true) {
        // // wait until Wi-Fi scan is finished
        // // Handler handler = new Handler();
        // // handler.postDelayed(new R unnable() {
        // // public void run() {
        // // // TODO: Add runnable later
        // // }
        // // }, 1000);
        // // To return results back to calling activity (e.g., MIT App
        // // Inventor App)
        // Intent scanResults = new Intent();
        // scanResults.putExtra("AP_LIST", sb.toString());
        // setResult(RESULT_OK, scanResults);
        // finish();
        // }
    }

    protected void onDestroy() {
        handler.removeCallbacks(runnable); //停止刷新
        super.onDestroy();
    }


    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            sb = new StringBuilder();
            csv = new StringBuilder();
            wifiList = mainWifi.getScanResults();

            boolean a = false;
            boolean b = false;

            // prepare text for display and CSV table
            sb.append("Number of APs Detected: ");
            sb.append((Integer.valueOf(wifiList.size())).toString());
            sb.append("\n\n");
            for (int i = 0; i < wifiList.size(); i++) {

                sb.append("BSSID:").append((wifiList.get(i)).BSSID);
                sb.append("\n");
                csv.append((wifiList.get(i)).BSSID);
                csv.append(",");

                routelist.scanner((wifiList.get(i)).BSSID);


                int ase = 0;
                try {
                    ase = Integer.valueOf((wifiList.get(i)).level).intValue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            mainText.setText(sb);
            scanFinished = true;


        }
    }


    public class Routes{
        String[] routes = new String[200];
        int number =0;
        int j;




        public void scanner(String ssid)
        {
            boolean have = false;
            for(j=0;j<number;j++)
            {
                if(ssid.equals(routes[j]))
                {
                    mainText.setText(routes[j]);
                    have = true;

                }
            }
            if(!have)
            {
                routes[number] = ssid;
                number++;
            }
        }

        public String returnlist()
        {
            int a;
            String list = routes[0] + "\n";
            for(a=1;a<number;a++)
            {
                list = list + routes[a] + "\n";

            }

            return list;

        }

        public int return_length(){
            return this.number;
        }




    }

    public static class FileUtils {

        private static final String TAG = "FileUtils";

        public static final int FLAG_SUCCESS = 1;//创建成功
        public static final int FLAG_EXISTS = 2;//已存在
        public static final int FLAG_FAILED = 3;//创建失败


        /**
         * 创建 单个 文件
         * @param filePath 待创建的文件路径
         * @return 结果码
         */
        public static File CreateFile(String filePath) {
            File file = new File(filePath);
            if (file.exists()) {
                Log.e(TAG,"The file [ " + filePath + " ] has already exists");
                //return FLAG_EXISTS;
            }
            if (filePath.endsWith(File.separator)) {// 以 路径分隔符 结束，说明是文件夹
                Log.e(TAG,"The file [ " + filePath + " ] can not be a directory");
                //return FLAG_FAILED;
            }

            //判断父目录是否存在
            if (!file.getParentFile().exists()) {
                //父目录不存在 创建父目录
                Log.d(TAG,"creating parent directory...");
                if (!file.getParentFile().mkdirs()) {
                    Log.e(TAG,"created parent directory failed.");
                    //return FLAG_FAILED;
                }
            }

            //创建目标文件
            try {
                if (file.createNewFile()) {//创建文件成功
                    Log.i(TAG, "create file [ " + filePath + " ] success");
                    //return FLAG_SUCCESS;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,"create file [ " + filePath + " ] failed");
                //return FLAG_FAILED;
            }

            return file;
        }

        /**
         * 创建 文件夹
         * @param dirPath 文件夹路径
         * @return 结果码
         */
        public int createDir (String dirPath) {

            File dir = new File(dirPath);
            //文件夹是否已经存在
            if (dir.exists()) {
                Log.w(TAG,"The directory [ " + dirPath + " ] has already exists");
                return FLAG_EXISTS;
            }
            if (!dirPath.endsWith(File.separator)) {//不是以 路径分隔符 "/" 结束，则添加路径分隔符 "/"
                dirPath = dirPath + File.separator;
            }
            //创建文件夹
            if (dir.mkdirs()) {
                Log.d(TAG,"create directory [ "+ dirPath + " ] success");
                return FLAG_SUCCESS;
            }

            Log.e(TAG,"create directory [ "+ dirPath + " ] failed");
            return FLAG_FAILED;
        }
    }
}
