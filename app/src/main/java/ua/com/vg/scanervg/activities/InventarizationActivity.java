package ua.com.vg.scanervg.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import ua.com.vg.scanervg.R;
import ua.com.vg.scanervg.adapters.InventContentRVAdapter;
import ua.com.vg.scanervg.async.DocumentLoader;
import ua.com.vg.scanervg.async.EntityLoader;
import ua.com.vg.scanervg.async.WarehouseLoader;
import ua.com.vg.scanervg.documents.Document;
import ua.com.vg.scanervg.model.Agent;
import ua.com.vg.scanervg.model.Entity;
import ua.com.vg.scanervg.utils.ScanKind;

public class InventarizationActivity extends AppCompatActivity implements InventContentRVAdapter.ItemClickListener{

    private Document document;
    private int docID;
    ProgressBar inventProgressBar;
    TextView lbSubdivInventarization;
    private final int EDIT_CONTENT_CODE = 1234;
    private int selectedPosition = -1;
    private InventContentRVAdapter inventContentRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventarization);
        inventProgressBar = (ProgressBar) findViewById(R.id.inventProgressbar);
        lbSubdivInventarization = (TextView) findViewById(R.id.lbSubdivInventarization);

        Intent intent = getIntent();
        docID = intent.getIntExtra("DOCID",0);
        if(docID == 0){
            document = new Document();
        }else {
            document = getDocumentByID(docID);
            Agent agentTo = document.getAgentTo();
            if(agentTo != null){
                lbSubdivInventarization.setText(agentTo.getName());
            }
        }

        Button btnInventSelectSubdiv = (Button) findViewById(R.id.btnInventSelectSubdiv);
        btnInventSelectSubdiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSubdiv();
            }
        });
    }


    private void scan(){
        IntentIntegrator integrator = new IntentIntegrator(InventarizationActivity.this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == EDIT_CONTENT_CODE){
            if(resultCode == RESULT_FIRST_USER){
                inventContentRVAdapter.removeItem(selectedPosition);
                selectedPosition = -1;
                return;
            }
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() != null) {
                EntityLoader entityLoader = new EntityLoader(inventProgressBar,InventarizationActivity.this, ScanKind.scanMakedEntity);
                List<Entity> entities = new ArrayList<>();
                try{
                    entityLoader.execute(result.getContents());
                    entities = entityLoader.get();
                }catch (Exception e){
                    Toast.makeText(InventarizationActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
                if(entities.size() > 0 ){
                    Entity entity = new Entity(0,"","");
                    if(entities.size() == 1){
                        entity = entities.get(0);
                        document.addDistinctRow(entity,1);
                        inventContentRVAdapter.notifyDataSetChanged();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(InventarizationActivity.this);
        builder.setTitle(R.string.captionDialogSelectSubDiv);
        builder.setItems(names.toArray(new String[names.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                document.addRow(entities.get(which),1);
                inventContentRVAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void selectSubdiv(){
        List<Agent> tmpWarehouses = null;
        try {
            WarehouseLoader warehouseLoader = new WarehouseLoader(inventProgressBar,InventarizationActivity.this);
            warehouseLoader.execute();
            tmpWarehouses = warehouseLoader.get();
        }catch (Exception e){
            Toast.makeText(InventarizationActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

        if(tmpWarehouses != null){
            final List<Agent> wareHoses = tmpWarehouses;
            List<String> names = new ArrayList<>();
            for(Agent agent:wareHoses){
                names.add(agent.getName());
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(InventarizationActivity.this);
            builder.setTitle(R.string.captionDialogSelectAgent);
            builder.setItems(names.toArray(new String[names.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Agent selectedAgent = wareHoses.get(which);
                    document.setAgentTo(selectedAgent);
                    lbSubdivInventarization.setText(selectedAgent.getName());
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    public Document getDocumentByID(int docID){
        Document result = new Document();
        try {
            DocumentLoader documentLoader = new DocumentLoader(inventProgressBar,InventarizationActivity.this);
            documentLoader.execute(docID);
            result = documentLoader.get();
        }catch (Exception e){
            Toast.makeText(InventarizationActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return result;
    }

    @Override
    public void onItemClick(View view, int position) {
        selectedPosition = position;
        Intent intent = new Intent(InventarizationActivity.this,DocContentEdit.class);
        intent.putExtra("QTY",inventContentRVAdapter.getItem(position).getQty());
        intent.putExtra("ENTNAME",inventContentRVAdapter.getItem(position).getEntName());
        startActivityForResult(intent,EDIT_CONTENT_CODE);
    }
}
