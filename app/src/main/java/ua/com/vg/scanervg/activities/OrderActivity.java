package ua.com.vg.scanervg.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import ua.com.vg.scanervg.R;
import ua.com.vg.scanervg.adapters.OrderContentsRVAdapter;
import ua.com.vg.scanervg.async.AgentFinder;
import ua.com.vg.scanervg.async.DocumentLoader;
import ua.com.vg.scanervg.async.EntityLoader;
import ua.com.vg.scanervg.async.MakedEntityLoader;
import ua.com.vg.scanervg.async.PriceLoader;
import ua.com.vg.scanervg.model.Agent;
import ua.com.vg.scanervg.documents.Document;
import ua.com.vg.scanervg.model.Entity;
import ua.com.vg.scanervg.utils.DocumentsKind;
import ua.com.vg.scanervg.utils.ScanKind;

public class OrderActivity extends AppCompatActivity implements OrderContentsRVAdapter.ItemClickListener{

    private int docID;
    private ProgressBar orderProgressBar;
    private EditText edCustomer;
    private Document document;
    private EditText editTextOrderMemo;
    RecyclerView orderContents;
    OrderContentsRVAdapter orderContentsRVAdapter;
    private int selectedPosition = -1;
    private final int EDIT_CONTENT_CODE = 1234;
    TextView orderDocSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        TextView tvDocNo  = (TextView)     findViewById(R.id.tvOrderDocNo);
        orderProgressBar  = (ProgressBar)  findViewById(R.id.orderProgressBar);
        edCustomer        = (EditText)     findViewById(R.id.edCustomer);
        orderContents     = (RecyclerView) findViewById(R.id.orderContents);
        orderDocSum       = (TextView)     findViewById(R.id.orderDocSum);
        editTextOrderMemo = (EditText)     findViewById(R.id.editTextOrderMemo);

        ImageButton btnSelectEntityFromCatalog = (ImageButton) findViewById(R.id.btnSelectEntityFromCatalog);
        btnSelectEntityFromCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEntityFromCatalog();
            }
        });

        Intent intent = getIntent();
        docID = intent.getIntExtra("DOCID",0);
        if(docID == 0){
            document = new Document(DocumentsKind.Order);
        }else {
            document = getDocumentByID(docID);
            tvDocNo.setText(document.getDocNo());
            Agent agentTo = document.getAgentTo();
            editTextOrderMemo.setText(document.getDocMemo());
            if(agentTo != null){
                edCustomer.setText(agentTo.getName(),TextView.BufferType.EDITABLE);
            }
        }

        ImageButton btnFindCustomer = (ImageButton) findViewById(R.id.btnFindCustomer);
        btnFindCustomer.setOnClickListener(new View.OnClickListener(){
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

        ImageButton btnScanEntity = (ImageButton) findViewById(R.id.btnScanEntityInOrder);
        btnScanEntity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });

        ImageButton btnSaveOrder = (ImageButton) findViewById(R.id.btnSaveOrder);
        btnSaveOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    document.save();
                }catch (Exception e){
                    Toast.makeText(OrderActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        editTextOrderMemo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                document.setDocMemo(editTextOrderMemo.getText().toString());
            }
        });

        orderContents.setLayoutManager(new LinearLayoutManager(this));
        orderContentsRVAdapter = new OrderContentsRVAdapter(this,document.getContentList());
        orderContentsRVAdapter.setClickListener(this);
        orderContents.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        orderContents.setAdapter(orderContentsRVAdapter);
        calcAndDrawDocSum();
    }

    private void calcAndDrawDocSum(){
        double docSum = 0;
        int countRows = orderContentsRVAdapter.getItemCount();

        for(int i = 0;i<countRows;i++){
            docSum += orderContentsRVAdapter.getItem(i).getSum();
        }
        orderDocSum.setText(NumberFormat.getCurrencyInstance().format(docSum));
        document.setDocSum(docSum);
    }

    private void scan(){
        IntentIntegrator integrator = new IntentIntegrator(OrderActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Наведите камеру на код");
        integrator.setCameraId(0);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);

        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.initiateScan();
    }

    private double getEntPrice(Entity entity){
        double result = 0.0;
        try {
            PriceLoader priceLoader = new PriceLoader(orderProgressBar,OrderActivity.this);
            priceLoader.execute(entity);
            result = priceLoader.get();
        }catch (Exception e){
            Toast.makeText(OrderActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == EDIT_CONTENT_CODE){
            if(resultCode == RESULT_FIRST_USER){
                orderContentsRVAdapter.removeItem(selectedPosition);
                selectedPosition = -1;
                calcAndDrawDocSum();
                return;
            }else if(data != null && selectedPosition > -1){
                double qty = Double.valueOf(data.getStringExtra("QTY"));
                double price = getEntPrice(orderContentsRVAdapter.getItem(selectedPosition).getEntity());

                orderContentsRVAdapter.getItem(selectedPosition).setQty(qty);
                orderContentsRVAdapter.getItem(selectedPosition).setPrice(price);
                orderContentsRVAdapter.notifyDataSetChanged();
                calcAndDrawDocSum();
                return;
            }
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() != null) {
                EntityLoader entityLoader = new EntityLoader(orderProgressBar,OrderActivity.this,ScanKind.scanMakedEntity);
                List<Entity> entities = new ArrayList<>();
                try{
                    entityLoader.execute(result.getContents());
                    entities = entityLoader.get();
                }catch (Exception e){
                    Toast.makeText(OrderActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
                if(entities.size() > 0 ){
                    Entity entity = new Entity(0,"","");
                    if(entities.size() == 1){
                        entity = entities.get(0);
                            document.addRow(entity,1);
                            orderContentsRVAdapter.notifyDataSetChanged();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
        builder.setTitle(R.string.captionDialogSelectEntity);
        builder.setItems(names.toArray(new String[names.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                document.addRow(entities.get(which),1);
                orderContentsRVAdapter.notifyDataSetChanged();
                calcAndDrawDocSum();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void selectEntityFromCatalog(){
        List<Entity> tmpEntities = null;
        try {
            MakedEntityLoader makedEntityLoader = new MakedEntityLoader(orderProgressBar,OrderActivity.this);
            makedEntityLoader.execute();
            tmpEntities = makedEntityLoader.get();
        }catch (Exception e){
            Toast.makeText(OrderActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

        if(tmpEntities != null){
            final List<Entity> entities = tmpEntities;
            List<String> names = new ArrayList<>();
            for(Entity entity:entities){
                names.add(entity.getEntname());
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
            builder.setTitle(R.string.captionDialogSelectEntity);
            builder.setItems(names.toArray(new String[names.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Entity selectedEntity = entities.get(which);
                    document.addRow(selectedEntity,1);
                    orderContentsRVAdapter.notifyDataSetChanged();
                    calcAndDrawDocSum();
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void selectAgentFromDialog(final List<Agent> agents){
        List<String> names = new ArrayList<>();
        for(Agent agent:agents){
            names.add(agent.getName());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
        builder.setTitle(R.string.captionDialogSelectEntity);
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
        selectedPosition = position;
        Intent intent = new Intent(OrderActivity.this,DocContentEdit.class);
        intent.putExtra("QTY",orderContentsRVAdapter.getItem(position).getQty());
        intent.putExtra("ENTNAME",orderContentsRVAdapter.getItem(position).getEntName());
        startActivityForResult(intent,EDIT_CONTENT_CODE);
    }

    //TODO Реализовать метод удаления корреспондента
}
