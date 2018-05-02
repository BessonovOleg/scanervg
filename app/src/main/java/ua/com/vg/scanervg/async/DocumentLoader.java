package ua.com.vg.scanervg.async;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import ua.com.vg.scanervg.activities.MainActivity;
import ua.com.vg.scanervg.dao.DatabaseManager;
import ua.com.vg.scanervg.documents.Document;

public class DocumentLoader extends AsyncTask<Integer,Void,Document> {
    private String errorMessage = "";
    private ProgressBar progressBar;
    private Context contextForMessage;

    public DocumentLoader(ProgressBar progressBar,Context contextForMessage) {
        this.progressBar = progressBar;
        this.contextForMessage = contextForMessage;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected void onPostExecute(Document document) {
        super.onPostExecute(document);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        if(errorMessage.length() > 0){
            Toast.makeText(contextForMessage,errorMessage,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Document doInBackground(Integer... params) {
        Document result = new Document();
        try {
            Context ctx = MainActivity.getContext();
            DatabaseManager dbDatabaseManager = new DatabaseManager(ctx);
            result = dbDatabaseManager.getDocumentByID(params[0]);
        }catch (Exception e){
            errorMessage = e.getMessage();
        }
        return result;
    }
}
