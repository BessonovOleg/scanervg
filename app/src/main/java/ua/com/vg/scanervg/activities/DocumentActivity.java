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
import ua.com.vg.scanervg.documents.Entity;
import ua.com.vg.scanervg.utils.ScanKind;

public class DocumentActivity extends AppCompatActivity {

    TextView lbMakedEntity;
    ImageButton btnScanMakedEntity;
    ImageButton btnScanContentdEntity;
    ScanKind scanKind;
    Context ctx;
    Document document;
    DbEntity dbEntity;
    int docID;

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
                scanCode(ScanKind.scanMakedEntity);
            }
        });

        btnScanContentdEntity = (ImageButton) findViewById(R.id.btnScanContentdEntity);
        btnScanContentdEntity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode(ScanKind.scanContentEntity);
            }
        });

        Intent intent = getIntent();
        docID = intent.getIntExtra("DOCID",0);
        if(docID == 0){
            document = new Document();
        }else
        {
            document = getDocumentByID(docID);
        }

    }

    //TODO Implement method
    public Document getDocumentByID(int docID){
        Document result = new Document();
        return result;
    }

    private void scanCode(ScanKind scanKind){
        this.scanKind = scanKind;
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
            if(result.getContents() != null) {
                //получение кода  result.getContents()
                if(scanKind == ScanKind.scanMakedEntity){
                    dbEntity = new DbEntity(ctx);
                    Entity entity = new Entity(0,"");
                    try{
                        dbEntity.execute(result.getContents());
                        entity = dbEntity.get();
                        lbMakedEntity.setText(entity.getEntname());
                    }catch (Exception e){
                        Toast.makeText(DocumentActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }

            }
        }else {
            super.onActivityResult(requestCode,resultCode,data);
        }

    }

    class DbEntity extends AsyncTask<String,Void,Entity>{
        Context ctx;
        ScanKind scKind;
        String errorMessage = "";

        public DbEntity(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            scKind = scanKind;
        }

        @Override
        protected void onPostExecute(Entity entity) {
            super.onPostExecute(entity);
            if(errorMessage.length() > 0){
                Toast.makeText(DocumentActivity.this,errorMessage,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Entity doInBackground(String... strings) {
            Entity result = new Entity(0,"");
            try {
                DatabaseManager dbDatabaseManager = new DatabaseManager(ctx);
                result = dbDatabaseManager.getEntityByCode(strings[0],scKind);
            }catch (Exception e){
                errorMessage = e.getMessage();
            }
            return result;
        }
    }

    /*
    class DbEntity extends AsyncTask<Void,Void,Void>{
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
*/


}
