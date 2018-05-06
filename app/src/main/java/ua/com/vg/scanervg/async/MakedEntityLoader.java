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

public class MakedEntityLoader extends AsyncTask<Void,Void,List<Entity>> {
    private String errorMessage = "";
    private ProgressBar progressBar;
    private Context contextForMessage;


    public MakedEntityLoader(ProgressBar progressBar,Context contextForMessage) {
        this.progressBar = progressBar;
        this.contextForMessage = contextForMessage;
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<Entity> entities) {
        super.onPostExecute(entities);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        if(errorMessage.length() > 0){
            Toast.makeText(contextForMessage,errorMessage,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected List<Entity> doInBackground(Void... voids) {
        List<Entity> result = new ArrayList<>();
        try {
            Context ctx = MainActivity.getContext();
            DatabaseManager dbDatabaseManager = new DatabaseManager(ctx);
            result = dbDatabaseManager.getAllMakedEntities();
        }catch (Exception e){
            errorMessage = e.getMessage();
        }
        return result;
    }
}
