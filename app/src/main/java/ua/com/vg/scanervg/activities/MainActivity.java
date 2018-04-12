package ua.com.vg.scanervg.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import ua.com.vg.scanervg.R;
import ua.com.vg.scanervg.dao.DatabaseManager;
import ua.com.vg.scanervg.documents.DocInfo;
import ua.com.vg.scanervg.documents.DocInfoRVAdapter;


public class MainActivity extends AppCompatActivity implements DocInfoRVAdapter.ItemClickListener{
    TextView tv;
    RecyclerView rvDocList;
    DocInfoRVAdapter docInfoRVAdapter;
    ProgressBar progressBar;
    DocListWorker docListWorker;
    ImageButton btnAddDocument;
    private static Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rvDocList = (RecyclerView) findViewById(R.id.doclist);
        tv = (TextView) findViewById(R.id.tv);
        ImageButton btnSetting = (ImageButton) findViewById(R.id.btnSetting);

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(intent,0);
            }
        });

        btnAddDocument = (ImageButton) findViewById(R.id.addDocument);

        btnAddDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DocumentActivity.class);
                startActivityForResult(intent,1);
            }
        });

        fillListDoc();


/*
                Start scaner

                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt("Наведите камеру на код");
                integrator.setCameraId(0);
                integrator.setOrientationLocked(true);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.setCaptureActivity(CaptureActivityPortrait.class);
                integrator.initiateScan();
 */
        //------------------------------------------------------------------
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //После вызова настроек или работы с докуменотом перезаполним список документов
        fillListDoc();

        /*
Read scaner result
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
            }
        }else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    */
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this,docInfoRVAdapter.getItem(position).getDocName(),Toast.LENGTH_SHORT).show();
        //System.out.println(docInfoRVAdapter.getItem(position).getDocName());
        //Здесь будем открывать документ
    }

    private void fillListDoc(){
        docListWorker = new DocListWorker(this);
        if(docListWorker != null){
            docListWorker.execute();
        }
    }

    public static Context getContext(){
        return MainActivity.context;
    }

    class DocListWorker extends AsyncTask<Void,Void,Void>{
        List<DocInfo> docInfoList;
        String errorMsg = "";
        Context ctx;

        public DocListWorker(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (docInfoList == null){
                Toast.makeText(ctx,errorMsg,Toast.LENGTH_LONG).show();
            }else {
                rvDocList.setLayoutManager(new LinearLayoutManager(ctx));
                docInfoRVAdapter = new DocInfoRVAdapter(ctx, docInfoList);
                docInfoRVAdapter.setClickListener(MainActivity.this);
                rvDocList.addItemDecoration(new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL));
                rvDocList.setAdapter(docInfoRVAdapter);
            }
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                DatabaseManager dbDatabaseManager = new DatabaseManager(getApplicationContext());
                docInfoList = dbDatabaseManager.getDocList();
            }catch (Exception e){
                errorMsg = e.getMessage();
            }
            return null;
        }
    }
}
