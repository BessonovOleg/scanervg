package ua.com.vg.scanervg.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.List;

import ua.com.vg.scanervg.R;
import ua.com.vg.scanervg.async.DocumentLoader;
import ua.com.vg.scanervg.async.EntityLoader;
import ua.com.vg.scanervg.dao.DatabaseManager;
import ua.com.vg.scanervg.adapters.DocContentsRVAdapter;
import ua.com.vg.scanervg.documents.Document;
import ua.com.vg.scanervg.model.Entity;
import ua.com.vg.scanervg.utils.DocumentsKind;
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
    DbSaveDocument dbSaveDocument;
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

            if(document!= null){
                document.setDocumentsKind(DocumentsKind.Manufacture);
            }
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


    private void saveDocument(){
        document.setDocMemo(editTextDocMemo.getText().toString());
        try {
            if (document.save() == 1){
                finish();
            }
        }catch (Exception e){
            Toast.makeText(DocumentActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public Document getDocumentByID(int docID){
        Document result = new Document();
        try {
            documentLoader = new DocumentLoader(docProgressBar,DocumentActivity.this);
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

    @Override
    public void onItemClick(View view, int position) {
        selectedPosition = position;
        Intent intent = new Intent(DocumentActivity.this,DocContentEdit.class);
        intent.putExtra("QTY",docContentsRVAdapter.getItem(position).getQty());
        intent.putExtra("ENTNAME",docContentsRVAdapter.getItem(position).getEntName());
        startActivityForResult(intent,EDIT_CONTENT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == EDIT_CONTENT_CODE){
            if(resultCode == RESULT_FIRST_USER){
                docContentsRVAdapter.removeItem(selectedPosition);
                selectedPosition = -1;
                document.recalcRowNoInContentList();
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
                EntityLoader entityLoader = new EntityLoader(docProgressBar,DocumentActivity.this,scanKind);
                List<Entity> entities = new ArrayList<>();
                    try{
                        entityLoader.execute(result.getContents());
                        entities = entityLoader.get();
                    }catch (Exception e){
                        Toast.makeText(DocumentActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    if(entities.size() > 0 ){
                        Entity entity = new Entity(0,"","");
                        if(entities.size() == 1){
                            entity = entities.get(0);
                            if(scanKind == ScanKind.scanMakedEntity){
                                document.setMakedEntity(entity);
                                lbMakedEntity.setText(entity.getEntname());
                            }
                            if(scanKind == ScanKind.scanContentEntity){
                                document.addRow(entity,1);
                                docContentsRVAdapter.notifyDataSetChanged();
                            }
                        }else {
                            selectEntityFromDialog(entities);
                        }
                    }else {
                        Toast.makeText(this,R.string.msgEntityNotFound,Toast.LENGTH_SHORT).show();
                    }
            }
        }else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    public void selectEntityFromDialog(final List<Entity> entities){
        List<String> names = new ArrayList<>();
        for(Entity entity:entities){
            names.add(entity.getEntname());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
        builder.setTitle(R.string.captionDialogSelectEntity);
        builder.setItems(names.toArray(new String[names.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                document.addRow(entities.get(which),1);
                docContentsRVAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    class DbSaveDocument extends AsyncTask<Document,Void,Void>{
        Context ctx;
        String errorMessage = "";

        public DbSaveDocument(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            docProgressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            docProgressBar.setVisibility(ProgressBar.INVISIBLE);
            if(errorMessage.length() > 0){
                Toast.makeText(DocumentActivity.this,errorMessage,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Void doInBackground(Document... documents) {
            try {
                DatabaseManager dbDatabaseManager = new DatabaseManager(ctx);
                dbDatabaseManager.saveDocument(document);
            }catch (Exception e){
                errorMessage = e.getMessage();
            }
            return null;
        }
    }
}
