package ua.com.vg.scanervg.documents;

import java.util.Date;

public class DocInfo {
    private int docID;
    private String strDocDate;
    private String docName;
    private String docNumber;
    private String docMemo;
    private String docAgentName;
    private double docSum;

    public String getStrDocDate() {
        return strDocDate;
    }

    public void setStrDocDate(String strDocDate) {
        this.strDocDate = strDocDate;
    }

    public String getDocAgentName() {
        return docAgentName;
    }

    public void setDocAgentName(String docAgentName) {
        this.docAgentName = docAgentName;
    }

    public DocInfo() {
        docID = 0;
        docName = "";
        docNumber = "";
        docMemo = "";
        docSum = 0;
        docAgentName = "";
        strDocDate = "";
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