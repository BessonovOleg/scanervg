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

import java.util.List;

import ua.com.vg.scanervg.R;
import ua.com.vg.scanervg.adapters.DocInfoRVAdapter;
import ua.com.vg.scanervg.dao.DatabaseManager;
import ua.com.vg.scanervg.documents.DocInfo;
import ua.com.vg.scanervg.utils.DocumentsKind;

public class ListDocsActivity extends AppCompatActivity implements DocInfoRVAdapter.ItemClickListener{
    RecyclerView rvDocList;
    DocInfoRVAdapter docInfoRVAdapter;
    ProgressBar progressBar;
    DocListWorker docListWorker;
    ImageButton btnAddDocument;
    DocumentsKind documentsKind;

    private Intent intentNewDocument;
    private String documentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_docs);

        Intent intent = getIntent();
        documentsKind = (DocumentsKind) intent.getExtras().get("docKind");

        initDocumentByKind(documentsKind);

        TextView tvDocumentCaption = (TextView) findViewById(R.id.tvDocumentCaption);
        tvDocumentCaption.setText(documentName);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rvDocList = (RecyclerView) findViewById(R.id.doclist);

        btnAddDocument = (ImageButton) findViewById(R.id.addDocument);
        btnAddDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intentNewDocument != null){
                    intentNewDocument.putExtra("DOCID",0);
                    startActivityForResult(intentNewDocument,1);
                }else {
                    Toast.makeText(ListDocsActivity.this,R.string.errorInitDocument,Toast.LENGTH_LONG).show();
                }
            }
        });

        fillDocList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fillDocList();
    }

    public void fillDocList(){
        docListWorker = new DocListWorker(this,documentsKind);
                if(docListWorker != null){
                        docListWorker.execute();
                }
    }

    public void initDocumentByKind(DocumentsKind kind){
        if(kind == DocumentsKind.Order){
            documentName = "Заявка";
            intentNewDocument = new Intent(this,OrderActivity.class);
        }
        if(kind == DocumentsKind.Move){
            documentName = "Перемещение";
            intentNewDocument = new Intent(this,MoveActivity.class);
        }
        if(kind == DocumentsKind.Inventorization){
            documentName = "Инвентаризация";
            intentNewDocument = new Intent(this,InventarizationActivity.class);
        }
        if(kind == DocumentsKind.Manufacture){
            documentName = "Производство";
            intentNewDocument = new Intent(this,DocumentActivity.class);
        }
        if(kind == DocumentsKind.Sale){
            documentName = "Реализация";
            intentNewDocument = new Intent(this,SaleActivity.class);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = getIntentByDocType();
                intent.putExtra("DOCID",docInfoRVAdapter.getItem(position).getDocID());
                startActivityForResult(intent,0);
    }

    private Intent getIntentByDocType() {
        Intent result = new Intent(ListDocsActivity.this, ListDocsActivity.class);
        if (documentsKind == null){
            return result;
        }
        if (documentsKind == DocumentsKind.Manufacture) {
            result = new Intent(ListDocsActivity.this, DocumentActivity.class);
        }
        if (documentsKind == DocumentsKind.Sale){
            result = new Intent(ListDocsActivity.this, SaleActivity.class);
        }
        if (documentsKind == DocumentsKind.Inventorization){
            result = new Intent(ListDocsActivity.this, InventarizationActivity.class);
        }
        if (documentsKind == DocumentsKind.Order){
            result = new Intent(ListDocsActivity.this, OrderActivity.class);
        }
        if (documentsKind == DocumentsKind.Move){
            result = new Intent(ListDocsActivity.this, MoveActivity.class);
        }
        return result;
    }


    class DocListWorker extends AsyncTask<Void,Void,Void> {
        private List<DocInfo> docInfoList;
        private String errorMsg = "";
        private Context ctx;
        private int numberDocKind = 0;

        public DocListWorker(Context ctx, DocumentsKind documentsKind) {
            this.ctx = ctx;
            if (documentsKind != null) {
                if (documentsKind == DocumentsKind.Manufacture) {
                    numberDocKind = 1;
                }

                if (documentsKind == DocumentsKind.Move) {
                    numberDocKind = 2;
                }

                if (documentsKind == DocumentsKind.Inventorization) {
                    numberDocKind = 3;
                }

                if (documentsKind == DocumentsKind.Order) {
                    numberDocKind = 4;
                }

                if (documentsKind == DocumentsKind.Sale) {
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

            if (docInfoList == null) {
                Toast.makeText(ctx, errorMsg, Toast.LENGTH_LONG).show();
            } else {
                rvDocList.setLayoutManager(new LinearLayoutManager(ctx));
                docInfoRVAdapter = new DocInfoRVAdapter(ctx, docInfoList);
                docInfoRVAdapter.setClickListener(ListDocsActivity.this);
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
            } catch (Exception e) {
                errorMsg = e.getMessage();
            }
            return null;
        }
    }
}
