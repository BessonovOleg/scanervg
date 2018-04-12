package ua.com.vg.scanervg.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import ua.com.vg.scanervg.R;
import ua.com.vg.scanervg.dao.DatabaseManager;
import ua.com.vg.scanervg.documents.Document;

public class DocumentActivity extends AppCompatActivity {

    TextView lbMakedEntity;
    ImageButton btnScanMakedEntity;
    ImageButton btnScanContentdEntity;
    int kindScan = 0;
    int kindScanMakedEntity = 1;
    int kindScanContents = 2;
    Context ctx;
    Document document;
    DbWorker dbWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        ctx = MainActivity.getContext();
        lbMakedEntity = (TextView) findViewById(R.id.lbMakedEntity);
        btnScanMakedEntity = (ImageButton) findViewById(R.id.btnScanMakedEntity);
        btnScanMakedEntity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode(kindScanMakedEntity);
            }
        });


        btnScanContentdEntity = (ImageButton) findViewById(R.id.btnScanContentdEntity);
        btnScanContentdEntity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbWorker = new DbWorker(ctx);
                dbWorker.execute();
            }
        });

    }

    private void scanCode(int kindScan){
        this.kindScan = kindScan;
        IntentIntegrator integrator = new IntentIntegrator(DocumentActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Наведите камеру на код");
        integrator.setCameraId(0);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.initiateScan();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() == null) {
                // Toast.makeText(this,"Cancelled", Toast.LENGTH_SHORT).show();
            }else{
                //получение кода  result.getContents()
                //Toast.makeText(this,"Ok) Code = " + result.getContents(),Toast.LENGTH_SHORT).show();
//                DatabaseManager dbManager = new DatabaseManager();
//                dbManager.test(MainActivity.this,tv,result.getContents());
                //DbTask dbTask = new DbTask();
                //dbTask.execute(result.getContents());
                Toast.makeText(this,result.getContents(),Toast.LENGTH_LONG).show();
            }
        }else {
            super.onActivityResult(requestCode,resultCode,data);
        }

    }


    class DbWorker extends AsyncTask<Void,Void,Void>{
        String error = "";
        String result = "";
        Context context;
        DatabaseManager dbmanager;

        public DbWorker(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lbMakedEntity.setText("Подготовка");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(error.length() > 0){
                lbMakedEntity.setText(error);
            }else {
                lbMakedEntity.setText(result);
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                DatabaseManager dbDatabaseManager = new DatabaseManager(getApplicationContext());
                result = dbDatabaseManager.getDocList().toString();
            }catch (Exception e){
                error = e.getMessage();
            }
            return null;
        }
    }

}
