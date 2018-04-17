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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import ua.com.vg.scanervg.R;
import ua.com.vg.scanervg.dao.DatabaseManager;
import ua.com.vg.scanervg.documents.DocContentsRVAdapter;
import ua.com.vg.scanervg.documents.Document;
import ua.com.vg.scanervg.documents.Entity;
import ua.com.vg.scanervg.utils.ScanKind;

public class DocumentActivity extends AppCompatActivity implements DocContentsRVAdapter.ItemClickListener{
    EditText editTextDocMemo;
    TextView lbMakedEntity;
    ImageButton btnScanMakedEntity;
    ImageButton btnScanContentdEntity;
    ImageButton btnSaveDocument;
    ProgressBar docProgressBar;
    ScanKind scanKind;
    Context ctx;
    Document document;
    private int selectedPosition = -1;
    DbEntity dbEntity;
    DocumentLoader documentLoader;

    RecyclerView docContents;
    DocContentsRVAdapter docContentsRVAdapter;
    int docID;

    private final int EDIT_CONTENT_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        ctx = MainActivity.getContext();
        lbMakedEntity   = (TextView)     findViewById(R.id.lbMakedEntity);
        docContents     = (RecyclerView) findViewById(R.id.docContents);
        docProgressBar  = (ProgressBar)  findViewById(R.id.docProgressBar);
        editTextDocMemo = (EditText)     findViewById(R.id.editTextDocMemo);

        btnSaveDocument = (ImageButton) findViewById(R.id.btnSaveDocument);
        btnSaveDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDocument();
            }
        });

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
        }else {
            document = getDocumentByID(docID);
        }

        if(document.getMakedEntity() != null){
            lbMakedEntity.setText(document.getMakedEntity().getEntname());
        }
        editTextDocMemo.setText(document.getDocMemo());

        docContents.setLayoutManager(new LinearLayoutManager(this));
        docContentsRVAdapter = new DocContentsRVAdapter(this,document.getContentList());
        docContentsRVAdapter.setClickListener(this);
        docContents.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        docContents.setAdapter(docContentsRVAdapter);
    }


    //TODO Implement this method
    private void saveDocument(){

    }

    public Document getDocumentByID(int docID){
        Document result = new Document();
        try {
            documentLoader = new DocumentLoader(ctx);
            documentLoader.execute(docID);
            result = documentLoader.get();
        }catch (Exception e){
            Toast.makeText(DocumentActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
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

    //TODO сделать диалог для редактирования - удаления строки документа
    @Override
    public void onItemClick(View view, int position) {
        selectedPosition = position;
        Intent intent = new Intent(DocumentActivity.this,DocContentEdit.class);
        intent.putExtra("QTY",docContentsRVAdapter.getItem(position).getQty());
        startActivityForResult(intent,EDIT_CONTENT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == EDIT_CONTENT_CODE){
            if(resultCode == RESULT_FIRST_USER){
                docContentsRVAdapter.removeItem(selectedPosition);
                selectedPosition = -1;
                return;
            }else if(data != null && selectedPosition > -1){
                double qty = Double.valueOf(data.getStringExtra("QTY"));
                docContentsRVAdapter.getItem(selectedPosition).setQty(qty);
                docContentsRVAdapter.notifyDataSetChanged();
                return;
            }
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() != null) {
                dbEntity = new DbEntity(ctx);
                Entity entity = new Entity(0,"","");
                    try{
                        dbEntity.execute(result.getContents());
                        entity = dbEntity.get();
                    }catch (Exception e){
                        Toast.makeText(DocumentActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    if(entity.getEntid() > 0 ){
                        if(scanKind == ScanKind.scanMakedEntity){
                            document.setMakedEntity(entity);
                            lbMakedEntity.setText(entity.getEntname());
                        }
                        if(scanKind == ScanKind.scanContentEntity){
                            document.addRow(entity,1);
                            docContentsRVAdapter.notifyDataSetChanged();
                        }
                    }
            }
        }else {
            super.onActivityResult(requestCode,resultCode,data);
        }

    }

    class DocumentLoader extends AsyncTask<Integer,Void,Document>{
        Context ctx;
        String errorMessage = "";

        public DocumentLoader(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            docProgressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);
            docProgressBar.setVisibility(ProgressBar.INVISIBLE);
            if(errorMessage.length() > 0){
                Toast.makeText(DocumentActivity.this,errorMessage,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Document doInBackground(Integer... params) {
            Document result = new Document();
            try {
                DatabaseManager dbDatabaseManager = new DatabaseManager(ctx);
                result = dbDatabaseManager.getDocumentByID(params[0]);
            }catch (Exception e){
                errorMessage = e.getMessage();
            }
            return result;
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
            Entity result = new Entity(0,"","");
            try {
                DatabaseManager dbDatabaseManager = new DatabaseManager(ctx);
                result = dbDatabaseManager.getEntityByCode(strings[0],scKind);
            }catch (Exception e){
                errorMessage = e.getMessage();
            }
            return result;
        }
    }
}
