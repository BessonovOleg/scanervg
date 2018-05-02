package ua.com.vg.scanervg.documents;

import java.util.Date;

public class DocInfo {
    private int docID;
    private String docName;
    private String docNumber;
    private String docMemo;
    private double docSum;

    public DocInfo() {
        docID = 0;
        docName = "";
        docNumber = "";
        docMemo = "";
        docSum = 0;
    }

    public double getDocSum() {
        return docSum;
    }

    public void setDocSum(double docSum) {
        this.docSum = docSum;
    }

    public String getDocMemo() {
        return docMemo;
    }

    public void setDocMemo(String docMemo) {
        this.docMemo = docMemo;
    }

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }
}