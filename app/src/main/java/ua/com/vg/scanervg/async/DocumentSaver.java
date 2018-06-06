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
    private DocumentsKind documentsKind;

    public DocumentSaver(DocumentsKind documentsKind) {
        this.documentsKind = documentsKind;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Document... documents) {
        try {
            Context ctx = MainActivity.getContext();
            DatabaseManager dbDatabaseManager = new DatabaseManager(ctx);
            dbDatabaseManager.saveDocument(documents[0]);
        }catch (Exception e){
            new IllegalStateException(e.getMessage());
        }
        return null;
    }
}
