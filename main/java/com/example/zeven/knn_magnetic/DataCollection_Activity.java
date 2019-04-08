package com.example.zeven.knn_magnetic;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorEventListener;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class DataCollection_Activity extends Activity implements SensorEventListener {
    private Button Back;
    private Button Collect;
    private Button Xm1plus;
    private Button Xm1de;
    private Button Ym1plus;
    private Button Ym1de;
    //private Button Record;

    TextView mainText;
    TextView IDT;
    TextView Magnetic;

    EditText EX;
    EditText EY;

    String X ;
    String Y ;

    int iX = 43223;
    int iY = 5741;

    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;

    StringBuilder sb = new StringBuilder();
    StringBuilder csv = new StringBuilder();
    boolean scanFinished = false;
    File a;
    File b;

    point[] points= new point[1000];;

    int ID = 0;

    String[] Routes = new String[200];
    int RouteNumber = 0;

    private SensorManager mSensorManager;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datacollection);

        mainText = (TextView) findViewById(R.id.textView11);
        mainText.setMovementMethod(ScrollingMovementMethod.getInstance());
        IDT = (TextView)findViewById(R.id.textView4);
        IDT.setText("0");
        Magnetic = (TextView)findViewById(R.id.textView);

        EX = (EditText)findViewById(R.id.editText);
        EY = (EditText)findViewById(R.id.editText2);
        //EY.setText(Y);

        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = MainActivity.ProjectName + "_PointsData";
        a= Scan_id_Activity.FileUtils.CreateFile(path + "/demos/file/"+ fileName +".txt");
        String fileName2 = MainActivity.ProjectName + "_Routes";
        //b= Scan_id_Activity.FileUtils.CreateFile(path + "/demos/file/"+ fileName2 +".csv");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        try {

            BufferedReader fout;
            fout = new BufferedReader(new FileReader(path + "/demos/file/"+ fileName2 +".txt" ));

            StringBuilder sb2 = new StringBuilder();
            int i;
            for(i=0;i<200;i++)
            {
                String a = fout.readLine();
                if(a==null )
                {
                    break;
                }
                else
                {
                    Routes[i] = a.replace("\n","");
                    sb2.append(Routes[i]);
                    sb2.append("\n");
                    RouteNumber++;
                }

            }
           sb2.append("RouteNumber:"+RouteNumber+"\n");
            mainText.setText(sb2);

            fout.close();
            //mainText.setText("Successful read routes list.\n");
            //fout.write(Integer.parseInt("123"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        Back = (Button)findViewById(R.id.button15);
        Collect = (Button)findViewById(R.id.button);
        //Record = (Button)findViewById(R.id.button10);
        Xm1plus = (Button)findViewById(R.id.button11);
        Xm1de = (Button)findViewById(R.id.button12);
        Ym1plus = (Button)findViewById(R.id.button13);
        Ym1de = (Button)findViewById(R.id.button14);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent();
                intent .setClass(DataCollection_Activity.this,MainActivity.class);
                startActivity(intent );
                DataCollection_Activity.this.finish();
            }
        });

        Collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainText.setText("start....\n");

                X = EX.getText().toString();
                Y = EY.getText().toString();

                try {
                    iX = Integer.valueOf(X);
                    iY = Integer.valueOf(Y);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                mainWifi.startScan();
                StringBuilder a = new StringBuilder();
                a.append(ID);
                IDT.setText(a);

                //mainText.setText(SB[1]);
                //ID++;
            }
        });

        Xm1plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iX = iX + 1000;
                X = String.valueOf(iX);
                EX.setText(X);
            }
        });

        Xm1de.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iX = iX - 1000;
                X = String.valueOf(iX);
                EX.setText(X);
            }
        });

        Ym1plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iY= iY + 1000;
                Y = String.valueOf(iY);
                EY.setText(Y);
            }
        });

        Ym1de.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iY = iY - 1000;
                Y = String.valueOf(iY);
                EY.setText(Y);
            }
        });
    }

    protected void onResume() {
        super.onResume();

        // 为磁场传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // 取消监听
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        float magetic = 0;
        // 获取传感器类型
        int type = event.sensor.getType();
        magetic = (float) Math.pow(values[0],2) + (float) Math.pow(values[1],2) + (float) Math.pow(values[2],2);
        magetic = (float) Math.sqrt(magetic);
        StringBuilder sb;
        switch (type){

            case Sensor.TYPE_MAGNETIC_FIELD:
                sb = new StringBuilder();
                sb.append(values[0]+",");
                sb.append(values[1]+",");
                sb.append(values[2]);
                //sb.append(magetic);

                Magnetic.setText(sb.toString());
                break;

        }
    }

    // 当传感器精度发生改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            sb = new StringBuilder();
            csv = new StringBuilder();
            wifiList = mainWifi.getScanResults();
            int[] levels = new int[RouteNumber];



            // prepare text for display and CSV table
            sb.append("Number of APs Detected: ");
            sb.append((Integer.valueOf(wifiList.size())).toString());
            sb.append("\n");
            for (int i = 0; i < wifiList.size(); i++) {
                for(int j =0; j< RouteNumber;j++)
                {
                    if(Routes[j].indexOf((wifiList.get(i)).BSSID) !=-1)
                    {
                        levels[j] = (wifiList.get(i)).level;
                    }

                }

                // BSSID
                sb.append("BSSID:").append((wifiList.get(i)).BSSID);
                sb.append("\n");
                //csv.append((wifiList.get(i)).BSSID);
                //csv.append(",");

                sb.append("Level:").append((wifiList.get(i)).level);
                sb.append("\n");
                //csv.append((wifiList.get(i)).level);
                //csv.append(";");

                //points[ID].AddData((wifiList.get(i)).BSSID,(wifiList.get(i)).level);
                //a = new point((wifiList.get(i)).BSSID,(wifiList.get(i)).level);

                int ase = 0;
                try {
                    ase = Integer.valueOf((wifiList.get(i)).level).intValue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }

            //sb.append(levels[2]);
            //csv.append(levels[2]);
            for(int j =0; j< RouteNumber;j++)
            {
                csv.append(levels[j]+",");
            }

            //ID++;




            mainText.setText(sb);


            try {

                BufferedWriter fout;
                fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(a, true)));


                //fout.write(ID+",");
                fout.write(iX+",");
                fout.write(iY+",");
                fout.write(csv.toString());
                fout.write(Magnetic.getText().toString());
                fout.write("\n");


                fout.close();
                //mainText.setText("Successful record points.\n");
                    //fout.write(Integer.parseInt("123"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            // notify that Wi-Fi scan has finished
            ID++;
            scanFinished = true;
        }
    }

    public class point{

        int[] levels = new int[200];
        String[] SSID = new String[200];
        String Data;
        double X = 0.0;
        double Y = 0.0;

        int i;

        public point(String data)
        {
            this.Data = data;
        }

        public void  AddData(String ssid, int level)
        {

            /*for(i = 0;i < 200;i++)
            {
                //mainText.setText(Routes[i]);
                if(Routes[i].indexOf(ssid) !=-1)
                {
                    levels[i] = level;
                }
                else {
                    continue;
                }
            }*/
            this.SSID[i] = ssid;
            this.levels[i] = level;
            i++;

        }
        public void SetXY(double x,double y)
        {
            this.X = x;
            this.Y = y;
        }

        public String return_levels()
        {
            String levs =Data;
            /*int i;
            for(i = 0;i < RouteNumber;i++)
            {
                levs = levs + String.valueOf(levels[i]) + ",";
            }
            levs = levs + "\n";*/
            return levs;
        }

    }
}
