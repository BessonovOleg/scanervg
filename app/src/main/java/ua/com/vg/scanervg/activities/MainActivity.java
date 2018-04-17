package ua.com.vg.scanervg.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
                intent.putExtra("DOCID",0);
                startActivityForResult(intent,1);
            }
        });
        fillListDoc();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fillListDoc();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(MainActivity.this,DocumentActivity.class);
        intent.putExtra("DOCID",docInfoRVAdapter.getItem(position).getDocID());
        startActivityForResult(intent,0);
    }

    private void fillListDoc(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

         if(sp.getString("ipAddress","").equals("") ||
               sp.getString("port","").equals("")   ||
               sp.getString("port","").equals("")   ||
               sp.getString("dbName","").equals("") ||
               sp.getString("login","").equals("")  ||
               sp.getString("password","").equals("")){
           Toast.makeText(this,R.string.msgNotSetConfig,Toast.LENGTH_SHORT).show();
           btnAddDocument.setEnabled(false);
           return;
        }

        btnAddDocument.setEnabled(true);

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
