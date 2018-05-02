package ua.com.vg.scanervg.async;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ua.com.vg.scanervg.activities.MainActivity;
import ua.com.vg.scanervg.dao.DatabaseManager;
import ua.com.vg.scanervg.documents.Agent;

public class AgentFinder extends AsyncTask<String,Void,List<Agent>> {
    String errorMessage = "";
    private ProgressBar progressBar;
    private Context contextForMessage;

    public AgentFinder(ProgressBar progressBar,Context contextForMessage) {
        this.progressBar = progressBar;
        this.contextForMessage = contextForMessage;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected void onPostExecute(List<Agent> agents) {
        super.onPostExecute(agents);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        if(errorMessage.length() > 0){
            Toast.makeText(contextForMessage,errorMessage,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected List<Agent> doInBackground(String... strings) {
        List<Agent> result = new ArrayList<>();
        try {
            Context ctx = MainActivity.getContext();
            DatabaseManager dbDatabaseManager = new DatabaseManager(ctx);
            result = dbDatabaseManager.findAgentByName(strings[0]);
        }catch (Exception e){
            errorMessage = e.getMessage();
        }
        return result;
    }
}