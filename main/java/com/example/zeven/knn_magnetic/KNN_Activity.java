package com.example.zeven.knn_magnetic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.hardware.SensorEventListener;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class KNN_Activity extends Activity implements SensorEventListener {
    private Button Pluse;
    private Button Start;
    private Button de;
    private Button Stop;
    private Button Back;
    private Button Ym1de;


    File a;

    String[] Points = new String[1000];
    String[] Routes = new String[200];

    int[][] lel = new int[1000][200];
    double[][] Magn = new double[1000][3];
    double[] Magn_total = new double[1000];

    int[] X = new int[1000];
    int[] Y = new int[1000];

    String[][] PL = new String[1000][200];

    int pointNumber = 0;
    int RouteNumber = 0;
    int ID = 0;
    int K = 2;

    double XC;
    double YC;

    double XC_old;
    double YC_old;

    double XCw;
    double YCw;

    double XCm;
    double YCm;

    double Magnetic[] = new double[3];

    int SX;
    int SY;

    int maxX = 46035 + 4176;
    int maxY = 16943 + 617;


    TextView mainText;
    TextView XT;
    TextView YT;
    TextView KT;

    TextView XTw;
    TextView YTw;

    TextView XTm;
    TextView YTm;



    private ImageView mainView;

    private Bitmap baseBitmap;
    private Bitmap baseBitmap2;
    private Canvas canvas;
    private Paint paint;


    StringBuilder sb2 = new StringBuilder();
    StringBuilder sb = new StringBuilder();
    StringBuilder csv = new StringBuilder();

    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;

    boolean scanFinished = false;

    private SensorManager mSensorManager;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler.postDelayed(this, 100 * 1);// 间隔120秒
        }
        void update() {
            mainWifi.startScan();


            canvas.drawPoint((TranferX(XC)),TranferY(YC), paint);
            //canvas.drawPoint((TranferX(22381)),TranferY(12392), paint);
            //canvas.drawPoint(15046,10438, paint);
            mainView.setImageBitmap(baseBitmap);
            //mainText.setText("Starting Scan...\n");
            //mainText.setText(routelist.returnlist());
        }
    };



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knn);

        CameraPreview mPreview = new CameraPreview(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview2);
        preview.addView(mPreview);


        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mainText = (TextView) findViewById(R.id.textView18);
        mainText.setMovementMethod(ScrollingMovementMethod.getInstance());
        XT = (TextView)findViewById(R.id.textView13);
        YT = (TextView)findViewById(R.id.textView17);

        XTw = (TextView)findViewById(R.id.textView22);
        YTw = (TextView)findViewById(R.id.textView24);

        XTm = (TextView)findViewById(R.id.textView27);
        YTm = (TextView)findViewById(R.id.textView29);

        KT = (TextView)findViewById(R.id.textView8);
        KT.setText(String.valueOf(K));



        Start = (Button)findViewById(R.id.button2);
        Pluse = (Button)findViewById(R.id.button16);
        de = (Button)findViewById(R.id.button17);
        Stop = (Button)findViewById(R.id.button10);
        Back = (Button)findViewById(R.id.button19);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = MainActivity.ProjectName + "_PointsData";
        String fileName2 = MainActivity.ProjectName + "_Routes";
        a = Scan_id_Activity.FileUtils.CreateFile(path + "/demos/file/" + fileName + ".txt");

        initComponent();
        paint = new Paint();
        paint.setStrokeWidth(80);
        paint.setColor(Color.RED);
        baseBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.e4).copy(Bitmap.Config.ARGB_8888, true);
        baseBitmap2 = BitmapFactory.decodeResource(getResources(),
                R.mipmap.e4).copy(Bitmap.Config.ARGB_8888, true);

        canvas = new Canvas(baseBitmap);
        canvas.drawColor(Color.TRANSPARENT);
        //canvas.drawPoint((int)XC*10,(int)YC*10, paint);
        mainView.setImageBitmap(baseBitmap);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);




        try {

            BufferedReader fout;
            fout = new BufferedReader(new FileReader(path + "/demos/file/"+ fileName +".txt" ));


            int i;
            for(i=0;i<1000;i++)
            {
                String a = fout.readLine();
                if(a==null )
                {
                    break;
                }
                else
                {
                    Points[i] = a.replace("\n","");
                    sb2.append(Points[i]);
                    sb2.append("\n");
                    pointNumber++;
                }

            }
            sb2.append("RouteNumber:"+pointNumber+"\n");
            //mainText.setText(sb2);

            fout.close();
            //mainText.setText("Successful read routes list.\n");
            //fout.write(Integer.parseInt("123"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            BufferedReader fout;
            fout = new BufferedReader(new FileReader(path + "/demos/file/"+ fileName2 +".txt" ));

            //StringBuilder sb2 = new StringBuilder();
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
            //mainText.setText(sb2);

            fout.close();
            //mainText.setText("Successful read routes list.\n");
            //fout.write(Integer.parseInt("123"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i=0;i<pointNumber;i++)
        {
            PL[i] = Points[i].split(",");
            sb2.append(PL[i][2]);
        }
        mainText.setText(sb2);

        for(int i =0; i<pointNumber;i++)
        {
            try {
                X[i] = Integer.valueOf(PL[i][0]);
                Y[i] = Integer.valueOf(PL[i][1]);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            for(int j = 0;j<RouteNumber ;j++)
            {
                try {
                    lel[i][j] = Integer.valueOf(PL[i][j+2]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

        }

        for (int i =0; i<pointNumber;i++)
        {
            for(int j =0; j < 3; j++)
            {

                Magn[i][j] = Double.valueOf(PL[i][j+2+RouteNumber]);
                //sb2.append(PL[i][j+2+RouteNumber]);

                //Magn_total[i] = Magn[i];
            }
            Magn_total[i] = Math.pow(Magn[i][0],2) + Math.pow(Magn[i][1],2) + Math.pow(Magn[i][2],2);
            Magn_total[i] = Math.sqrt(Magn_total[i]);
        }

        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(runnable, 100 * 1);


                mainText.setText("start....\n");
                //mainWifi.startScan();


                //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                //mainView.setImageBitmap(baseBitmap2);

                //canvas.drawPoint((TranferX(XC)),TranferY(YC), paint);
                //if(XC_old!=0)
                //{
                //    canvas.drawLine((TranferX(XC_old)),TranferY(YC_old),(TranferX(XC)),TranferY(YC), paint);
               // }

                //canvas.drawPoint((TranferX(22381)),TranferY(12392), paint);
                //canvas.drawPoint(15046,10438, paint);
                mainView.setImageBitmap(baseBitmap);

            }
        });

        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeMessages(0);
            }
        });

        Pluse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                K++;
                KT.setText(String.valueOf(K));

            }
        });

        de.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                K--;
                KT.setText(String.valueOf(K));

            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent();
                intent .setClass(KNN_Activity.this,MainActivity.class);
                startActivity(intent );
                KNN_Activity.this.finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 取消监听
        mSensorManager.unregisterListener(this);
    }

    // 当传感器的值改变的时候回调该方法
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        // 获取传感器类型
        int type = event.sensor.getType();
        StringBuilder sb;
        switch (type){

            case Sensor.TYPE_MAGNETIC_FIELD:
                Magnetic[0] = values[0];
                Magnetic[1] = values[1];
                Magnetic[2] = values[2];
                //mTxtValue1.setText(sb.toString());
                break;

        }
    }

    // 当传感器精度发生改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    private void initComponent() {
        mainView = (ImageView) findViewById(R.id.imageView);
    }

    public int TranferX(double X)
    {
        double scale = maxX/baseBitmap.getWidth();
        int x = (int)((X + 4176)/scale);
        return x;

    }

    public int TranferY(double Y)
    {
        double scale = maxY/baseBitmap.getHeight();
        int y = (int)((maxY - 167 - Y)/scale);
        return y;

    }




    class WifiReceiver extends BroadcastReceiver {
        @SuppressLint("ResourceType")
        public void onReceive(Context c, Intent intent) {
            sb = new StringBuilder();
            csv = new StringBuilder();
            wifiList = mainWifi.getScanResults();
            int[] levels = new int[RouteNumber];
            double[] distance = new double[pointNumber];
            double[] MAX = new double[K];
            int[] MAXin = new int[K];

            double[] MAX_w = new double[K];
            int[] MAXin_w = new int[K];

            double[] MAX_m = new double[K];
            int[] MAXin_m = new int[K];



            double Magn_Now = Math.pow(Magnetic[0],2) + Math.pow(Magnetic[1],2) + Math.pow(Magnetic[2],2);
            Magn_Now = Math.sqrt(Magn_Now);
            double[] Man_distance = new double[pointNumber];
            double[] Man_distance2 = new double[pointNumber];
            double[] Man_distance3 = new double[pointNumber];

            double[] maxdistance = new double[K];

            long timeuse = 0;
            long currenttime1 = 0;
            long currenttime2 = 0;

            currenttime1 = System.currentTimeMillis();

            //sb.append(TranferX(22381));
            //prepare text for display and CSV table
            sb.append("Number of APs Detected: ");
            sb.append((Integer.valueOf(wifiList.size())).toString());
            sb.append("      ");
            for (int i = 0; i < wifiList.size(); i++) {
                for(int j =0; j< RouteNumber;j++)
                {
                    if(Routes[j].indexOf((wifiList.get(i)).BSSID) !=-1)
                    {
                        levels[j] = (wifiList.get(i)).level;
                    }

                }

                // BSSID
                //sb.append("BSSID:").append((wifiList.get(i)).BSSID);
                //sb.append("\n");
                //csv.append((wifiList.get(i)).BSSID);
                //csv.append(",");

                //sb.append("Level:").append((wifiList.get(i)).level);
                //sb.append("\n");
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

            //mainText.setText(sb);


            for(int i=0; i < pointNumber;i++)
           {
               distance[i] = 0;
               for(int j = 0; j<RouteNumber;j++)
               {
                   distance[i] = (int) (distance[i] + Math.pow((lel[i][j] - levels[j]),2));
               }

               Man_distance[i] = 0.0;

               Man_distance[i] = (double) (Man_distance[i] + Math.pow((Magn_total[i] - Magn_Now),2));
               Man_distance3[i] = (double) (Man_distance3[i] + Math.pow((Magn[i][2] - Magnetic[2]),2));

               Man_distance2[i] = 0.0;
               for(int j = 0; j<3 ;j++)
               {
                   Man_distance2[i] = Man_distance2[i] + Math.pow((Magn[i][j] - Magnetic[j]),2);
               }


               distance[i] = Math.sqrt(distance[i]);
               Man_distance[i] = Math.sqrt(Man_distance[i]);
               Man_distance2[i] = Math.sqrt(Man_distance2[i]);

               //sb.append(distance[i] + "," + Man_distance[i]);
               //sb.append("\n");
           }

            int[] di = new int[pointNumber];
            //double[] maxdistance = new double[K];
            //di = distance;
            //Arrays.sort(di);
            for(int i =0 ;i<K;i++)
            {
                double[] new_distance = distance;
                double[] new_madistance = Man_distance;
                MAX[i] = 1000000000;
                for(int j=0;j<pointNumber;j++)
                {
                    if((new_distance[j] + new_madistance[j]) < MAX[i])
                    {
                        MAX[i] = new_distance[j] + new_madistance[j];
                        MAXin[i] = j;

                    }

                }
                maxdistance[i] = distance[MAXin[i]];
                //new_madistance[MAXin[i]] = 500000000;
                new_distance[MAXin[i]] = 500000000;

            }

            currenttime2 = System.currentTimeMillis();
            timeuse = currenttime2 - currenttime1;

            for(int i =0 ;i<K;i++)
            {
                double[] new_distance = distance;
                double[] new_madistance = Man_distance3;
                MAX_w[i] = 1000000000;
                for(int j=0;j<pointNumber;j++)
                {

                    if((new_distance[j] + new_madistance[j]) < MAX_w[i])
                    {
                        MAX_w[i] = new_distance[j] + new_madistance[j];
                        MAXin_w[i] = j;

                    }

                }
                maxdistance[i] = distance[MAXin_w[i]];
                new_madistance[MAXin_w[i]] = 500000000;
                new_distance[MAXin_w[i]] = 500000000;

            }

            for(int i =0 ;i<K;i++)
            {
                double[] new_distance = distance;
                double[] new_madistance = Man_distance2;
                MAX_m[i] = 1000000000;
                for(int j=0;j<pointNumber;j++)
                {
                    if((new_distance[j] + new_madistance[j]) < MAX[i])
                    {
                        MAX_m[i] = new_distance[j] + new_madistance[j];
                        MAXin_m[i] = j;

                    }
                }
                maxdistance[i] = distance[MAXin_m[i]];
                new_madistance[MAXin_m[i]] = 500000000;
                new_distance[MAXin_m[i]] = 500000000;

            }


            currenttime1 = System.currentTimeMillis();

            int sumX = 0;
            int sumY = 0;

            int sumXw = 0;
            int sumYw = 0;

            int sumXm = 0;
            int sumYm = 0;

            for(int i =0 ;i<K;i++)
            {
                sumX = sumX + X[MAXin[i]];
                sumY = sumY + Y[MAXin[i]];

                sumXw = sumXw + X[MAXin_w[i]];
                sumYw = sumYw + Y[MAXin_w[i]];

                sumXm = sumXm + X[MAXin_m[i]];
                sumYm = sumYm + Y[MAXin_m[i]];
                //sb.append(maxdistance[i] + "," + Man_distance[MAXin[i]]);
                //sb.append("\n");

            }




            XC = sumX/K;
            YC = sumY/K;

            XCw = sumXw/K;
            YCw = sumYw/K;

            XCm = sumXm/K;
            YCm = sumYm/K;

            XT.setText(String.valueOf(XC));
            YT.setText(String.valueOf(YC));

            XTw.setText(String.valueOf(XCw));
            YTw.setText(String.valueOf(YCw));

            XTm.setText(String.valueOf(XCm));
            YTm.setText(String.valueOf(YCm));

            SX = (int)XC;
            SY = (int)YC;

            currenttime2 = System.currentTimeMillis();
            timeuse = timeuse + (currenttime2 - currenttime1);




            //sb.append(levels[2]);
            //csv.append(levels[2]);
            mainText.setText("");
            sb.append("TimeUse: " + timeuse + "\n");
            sb.append("finish");




            //ID++;
            mainText.setText(sb);

            // notify that Wi-Fi scan has finished
            ID++;
            scanFinished = true;
        }
    }
}
