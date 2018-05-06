package ua.com.vg.scanervg.async;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ua.com.vg.scanervg.activities.MainActivity;
import ua.com.vg.scanervg.dao.DatabaseManager;
import ua.com.vg.scanervg.model.Entity;
import ua.com.vg.scanervg.utils.ScanKind;

public class EntityLoader extends AsyncTask<String,Void,List<Entity>> {
    private String errorMessage = "";
    private ProgressBar progressBar;
    private Context contextForMessage;
    ScanKind scKind;

    public EntityLoader(ProgressBar progressBar, Context contextForMessage,ScanKind scanKind) {
        this.progressBar = progressBar;
        this.contextForMessage = contextForMessage;
        this.scKind = scanKind;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(ProgressBar.VISIBLE);
        if(errorMessage.length() > 0){
            Toast.makeText(contextForMessage,errorMessage,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPostExecute(List<Entity> entities) {
        super.onPostExecute(entities);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    protected List<Entity> doInBackground(String... strings) {
        List<Entity> result = new ArrayList<>();
        try {
            Context ctx = MainActivity.getContext();
            DatabaseManager dbDatabaseManager = new DatabaseManager(ctx);
            result = dbDatabaseManager.getEntityByCode(strings[0],scKind);
        }catch (Exception e){
            errorMessage = e.getMessage();
        }
        return result;
    }
}
