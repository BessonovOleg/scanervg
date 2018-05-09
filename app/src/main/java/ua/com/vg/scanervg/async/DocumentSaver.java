package ua.com.vg.scanervg.async;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import ua.com.vg.scanervg.activities.MainActivity;
import ua.com.vg.scanervg.dao.DatabaseManager;
import ua.com.vg.scanervg.documents.Document;
import ua.com.vg.scanervg.utils.DocumentsKind;

public class DocumentSaver extends AsyncTask<Document,Void,Void>{
    private String errorMessage = "";
    private ProgressBar progressBar;
    private Context contextForMessage;
    private DocumentsKind documentsKind;

    public DocumentSaver(ProgressBar progressBar, Context contextForMessage, DocumentsKind documentsKind) {
        this.progressBar = progressBar;
        this.contextForMessage = contextForMessage;
        this.documentsKind = documentsKind;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        if(errorMessage.length() > 0){
            Toast.makeText(contextForMessage,errorMessage,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Void doInBackground(Document... documents) {
        try {
            Context ctx = MainActivity.getContext();
            DatabaseManager dbDatabaseManager = new DatabaseManager(ctx);
            dbDatabaseManager.saveDocument(documentsKind,documents[0]);
        }catch (Exception e){
            errorMessage = e.getMessage();
        }
        return null;
    }
}
