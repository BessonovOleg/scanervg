package ua.com.vg.scanervg.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ua.com.vg.scanervg.R;
import ua.com.vg.scanervg.async.DocumentLoader;
import ua.com.vg.scanervg.documents.Agent;
import ua.com.vg.scanervg.documents.Document;

public class OrderActivity extends AppCompatActivity {

    private int docID;
    private ProgressBar orderProgressBar;
    private EditText edCustomer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        TextView tvDocNo = (TextView) findViewById(R.id.tvOrderDocNo);
        orderProgressBar = (ProgressBar) findViewById(R.id.orderProgressBar);
        Document document;

        Intent intent = getIntent();
        docID = intent.getIntExtra("DOCID",0);
        if(docID == 0){
            document = new Document();
        }else {
            document = getDocumentByID(docID);
            tvDocNo.setText(document.getDocNo());
            Agent agentTo = document.getAgentTo();
            if(agentTo != null){
                edCustomer.setText(agentTo.getName(),TextView.BufferType.EDITABLE);
            }
        }

        ImageButton btnFindCustomer = (ImageButton) findViewById(R.id.btnFindCustomer);
        btnFindCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public Document getDocumentByID(int docID){
        Document result = new Document();
        try {
            DocumentLoader documentLoader = new DocumentLoader(orderProgressBar,OrderActivity.this);
            documentLoader.execute(docID);
            result = documentLoader.get();
        }catch (Exception e){
            Toast.makeText(OrderActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return result;
    }
}
