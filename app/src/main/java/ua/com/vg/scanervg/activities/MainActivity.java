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


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static Context context;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        Button btnSetting = (Button) findViewById(R.id.buttonSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        Button buttonDocOrder = (Button) findViewById(R.id.buttonDocOrder);
        Button buttonDocSale  = (Button) findViewById(R.id.buttonDocSale);
        Button buttonDocManuf = (Button) findViewById(R.id.buttonDocManuf);
        Button buttonDocMove  = (Button) findViewById(R.id.buttonDocMove);
        Button buttonDocInvent = (Button) findViewById(R.id.buttonDocInvent);

        buttonDocOrder.setOnClickListener(this);
        buttonDocSale.setOnClickListener(this);
        buttonDocManuf.setOnClickListener(this);
        buttonDocMove.setOnClickListener(this);
        buttonDocInvent.setOnClickListener(this);
    }

    public static Context getContext(){
        return MainActivity.context;
    }

    private void startDocListActivity(DocumentsKind documentsKind){
        Intent intent = new Intent(this,ListDocsActivity.class);
        intent.putExtra("docKind",documentsKind);
        startActivity(intent);
    }

    private boolean isSettingCorrect() {
        boolean result = true;
        if(sp == null) return false;

        if (sp.getString("ipAddress", "").equals("")  ||
                sp.getString("port", "").equals("")   ||
                sp.getString("port", "").equals("")   ||
                sp.getString("dbName", "").equals("") ||
                sp.getString("login", "").equals("")) {
            Toast.makeText(this, R.string.msgNotSetConfig, Toast.LENGTH_SHORT).show();
            result = false;
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        if(isSettingCorrect()) {
            if(v.getId() == R.id.buttonDocOrder){
                startDocListActivity(DocumentsKind.Order);
            }
            if(v.getId() == R.id.buttonDocSale){
                startDocListActivity(DocumentsKind.Sale);
            }
            if(v.getId() == R.id.buttonDocManuf){
                startDocListActivity(DocumentsKind.Manufacture);
            }
            if(v.getId() == R.id.buttonDocMove){
                startDocListActivity(DocumentsKind.Move);
            }
            if(v.getId() == R.id.buttonDocInvent){
                startDocListActivity(DocumentsKind.Inventorization);
            }
        }
    }

}
