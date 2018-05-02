package ua.com.vg.scanervg.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ua.com.vg.scanervg.R;
import ua.com.vg.scanervg.adapters.OrderContentsRVAdapter;
import ua.com.vg.scanervg.async.AgentFinder;
import ua.com.vg.scanervg.async.DocumentLoader;
import ua.com.vg.scanervg.model.Agent;
import ua.com.vg.scanervg.documents.Document;

public class OrderActivity extends AppCompatActivity implements OrderContentsRVAdapter.ItemClickListener{

    private int docID;
    private ProgressBar orderProgressBar;
    private EditText edCustomer;
    private Document document;
    RecyclerView orderContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        TextView tvDocNo = (TextView) findViewById(R.id.tvOrderDocNo);
        orderProgressBar = (ProgressBar) findViewById(R.id.orderProgressBar);
        edCustomer = (EditText) findViewById(R.id.edCustomer);
        orderContents = (RecyclerView) findViewById(R.id.orderContents);


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
                String findName = edCustomer.getText().toString();
                if(findName.length() == 0){
                    return;
                }
                AgentFinder agentFinder = new AgentFinder(orderProgressBar,OrderActivity.this);
                List<Agent> agents = new ArrayList<>();
                try{
                    agentFinder.execute(findName);
                    agents = agentFinder.get();
                }catch (Exception e){
                    Toast.makeText(OrderActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }

                if(agents.size() > 0 ){
                    Agent agent = new Agent(0,"");
                    if(agents.size() == 1){
                        agent = agents.get(0);
                        document.setAgentTo(agent);
                        edCustomer.setText(agent.getName());
                    }else {
                        selectAgentFromDialog(agents);
                    }
                }else {
                    Toast.makeText(OrderActivity.this,R.string.msgAgentNotFound,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectAgentFromDialog(final List<Agent> agents){
        List<String> names = new ArrayList<>();
        for(Agent agent:agents){
            names.add(agent.getName());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
        builder.setTitle(R.string.captionDialogSelectAgent);
        builder.setItems(names.toArray(new String[names.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Agent selectedAgent = agents.get(which);
                document.setAgentTo(selectedAgent);
                edCustomer.setText(selectedAgent.getName());
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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

    @Override
    public void onItemClick(View view, int position) {

    }
}
