package com.example.zeven.knn_magnetic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private Button BUScanEV;
    private Button DataCollection;
    private Button KNN;
    private Button Plus;
    private Button CreatProject;
    //private Button Show;

    EditText Project_Name;

    TextView PName;
    TextView ProjectN;


    static String ProjectName_in = "MagneticProject";
    static String ProjectName = ProjectName_in;
    int number = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PName = (TextView)findViewById(R.id.textView19) ;
        ProjectN = (TextView)findViewById(R.id.textView20) ;
        ProjectN.setText(ProjectName);

        CreatProject = (Button)findViewById(R.id.button20);
        Plus = (Button)findViewById(R.id.button18);

        Project_Name = (EditText)findViewById(R.id.editText5);
        Project_Name.setFocusable(true);
        Project_Name.setFocusableInTouchMode(true);
        Project_Name.requestFocus();

        Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number++;
                ProjectName = ProjectName_in + number;
                ProjectN.setText(ProjectName);
                Project_Name.setText(ProjectName);
            }
        });

        CreatProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectName = Project_Name.getText().toString();
                ProjectN.setText(ProjectName);
            }
        });

        BUScanEV = (Button)findViewById(R.id.button5);

        BUScanEV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent();
                intent .setClass(MainActivity.this,Scan_id_Activity.class);
                startActivity(intent );
                MainActivity.this.finish();
            }
        });

        DataCollection = (Button)findViewById(R.id.button6);
        DataCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent();
                intent .setClass(MainActivity.this,DataCollection_Activity.class);
                startActivity(intent );
                MainActivity.this.finish();
            }
        });

        KNN = (Button)findViewById(R.id.button9);
        KNN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent();
                intent .setClass(MainActivity.this,KNN_Activity.class);
                startActivity(intent );
                MainActivity.this.finish();
            }
        });

        //Show = (Button)findViewById(R.id.button23);
        //Show.setOnClickListener(new View.OnClickListener() {
            //@Override
        //    public void onClick(View v) {
          //      Intent intent  = new Intent();
         //       intent .setClass(MainActivity.this,Show_Activity.class);
         //       startActivity(intent );
         //       MainActivity.this.finish();
        //    }
       // });
    }
}
