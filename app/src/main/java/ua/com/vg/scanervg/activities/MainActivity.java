package ua.com.vg.scanervg.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import android.widget.Toast;

import java.util.List;

import ua.com.vg.scanervg.R;
import ua.com.vg.scanervg.dao.DatabaseManager;
import ua.com.vg.scanervg.documents.DocInfo;
import ua.com.vg.scanervg.adapters.DocInfoRVAdapter;
import ua.com.vg.scanervg.utils.DocumentsKind;


public class MainActivity extends AppCompatActivity implements DocInfoRVAdapter.ItemClickListener{
    RecyclerView rvDocList;
    DocInfoRVAdapter docInfoRVAdapter;
    ProgressBar progressBar;
    DocListWorker docListWorker;
    ImageButton btnAddDocument;
    Button btnSelectDocKind;
    private static Context context;
    DocumentsKind docKind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rvDocList = (RecyclerView) findViewById(R.id.doclist);
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
                Intent intent = getIntentByDocType(docKind);
                intent.putExtra("DOCID",0);
                startActivityForResult(intent,1);
            }
        });

        btnSelectDocKind = (Button) findViewById(R.id.btnSelectDocKind);
        btnSelectDocKind.setText(R.string.btnSelectDocKindDefaultText);
        btnSelectDocKind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocKind();
            }
        });
    }

    public void selectDocKind(){
        Resources resources = getResources();
        final String[] arrayDocKind = resources.getStringArray(R.array.docKinds);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.btnSelectDocKindDefaultText);
        builder.setItems(arrayDocKind, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    docKind = DocumentsKind.Manufacture;
                }
                if (which == 1){
                    docKind = DocumentsKind.Move;
                }
                if (which == 2){
                    docKind = DocumentsKind.Inventorization;
                }
                if (which == 3){
                    docKind = DocumentsKind.Order;
                }
                if (which == 4){
                    docKind = DocumentsKind.Sale;
                }

                btnSelectDocKind.setText(arrayDocKind[which]);
                fillListDoc();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fillListDoc();
    }


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = getIntentByDocType(docKind);
        intent.putExtra("DOCID",docInfoRVAdapter.getItem(position).getDocID());
        startActivityForResult(intent,0);
    }

    private Intent getIntentByDocType(DocumentsKind documentsKind){
        Intent result = new Intent(MainActivity.this,DocumentActivity.class);
        if(documentsKind != null){
            if(documentsKind == DocumentsKind.Manufacture){
                result = new Intent(MainActivity.this,DocumentActivity.class);
            }

            /*
            if(documentsKind == DocumentsKind.Move){
                numberDocKind = 2;
            }
            */

            if(documentsKind == DocumentsKind.Inventorization){
                result = new Intent(MainActivity.this,InventarizationActivity.class);
            }

            if(documentsKind == DocumentsKind.Order){
                result = new Intent(MainActivity.this,OrderActivity.class);
            }

            /*
            if(documentsKind == DocumentsKind.Sale){
                numberDocKind = 5;
            }
            */
        }

        return result;
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

        if(docKind == null){
             return;
        }

        btnAddDocument.setEnabled(true);

        docListWorker = new DocListWorker(this,docKind);
        if(docListWorker != null){
            docListWorker.execute();
        }
    }

    public static Context getContext(){
        return MainActivity.context;
    }

    class DocListWorker extends AsyncTask<Void,Void,Void>{
        private List<DocInfo> docInfoList;
        private String errorMsg = "";
        private Context ctx;
        private int numberDocKind = 0;

        public DocListWorker(Context ctx,DocumentsKind documentsKind)
        {
            this.ctx = ctx;
            if(documentsKind != null){
                if(documentsKind == DocumentsKind.Manufacture){
                    numberDocKind = 1;
                }

                if(documentsKind == DocumentsKind.Move){
                    numberDocKind = 2;
                }

                if(documentsKind == DocumentsKind.Inventorization){
                    numberDocKind = 3;
                }

                if(documentsKind == DocumentsKind.Order){
                    numberDocKind = 4;
                }

                if(documentsKind == DocumentsKind.Sale){
                    numberDocKind = 5;
                }
            }
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
                docInfoList = dbDatabaseManager.getDocList(numberDocKind);
            }catch (Exception e){
                errorMsg = e.getMessage();
            }
            return null;
        }
    }
}
