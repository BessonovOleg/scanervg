package ua.com.vg.scanervg.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import ua.com.vg.scanervg.R;
import ua.com.vg.scanervg.utils.DocumentsKind;

public class ListDocsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_docs);

        Intent intent = getIntent();
        DocumentsKind documentsKind = (DocumentsKind) intent.getExtras().get("docKind");

        String documentName = getDocumentNameByKind(documentsKind);

        TextView tvDocumentCaption = (TextView) findViewById(R.id.tvDocumentCaption);
        tvDocumentCaption.setText(documentName);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

                /*

        rvDocList = (RecyclerView) findViewById(R.id.doclist);

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
*/

    }


    public String getDocumentNameByKind(DocumentsKind kind){
        String result = "";
        if(kind == DocumentsKind.Order){
            result = "Заявка";
        }
        if(kind == DocumentsKind.Move){
            result = "Перемещение";
        }
        if(kind == DocumentsKind.Inventorization){
            result = "Инвентаризация";
        }
        if(kind == DocumentsKind.Manufacture){
            result = "Производство";
        }
        if(kind == DocumentsKind.Sale){
            result = "Реализация";
        }
        return result;
    }



}
