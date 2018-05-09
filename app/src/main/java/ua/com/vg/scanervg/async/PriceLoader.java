package ua.com.vg.scanervg.async;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import ua.com.vg.scanervg.activities.MainActivity;
import ua.com.vg.scanervg.dao.DatabaseManager;
import ua.com.vg.scanervg.model.Entity;

public class PriceLoader extends AsyncTask<Entity,Void,Double> {
    private String errorMessage = "";
    private ProgressBar progressBar;
    private Context contextForMessage;

    public PriceLoader(ProgressBar progressBar, Context contextForMessage) {
        this.progressBar = progressBar;
        this.contextForMessage = contextForMessage;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected void onPostExecute(Double v) {
        super.onPostExecute(v);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        if(errorMessage.length() > 0){
            Toast.makeText(contextForMessage,errorMessage,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Double doInBackground(Entity... params) {
        double result = 0.0;
        try {
            Context ctx = MainActivity.getContext();
            DatabaseManager dbDatabaseManager = new DatabaseManager(ctx);
            result = dbDatabaseManager.getEntityPrice(params[0]);
        }catch (Exception e){
            errorMessage = e.getMessage();
        }
        return result;
    }
}
