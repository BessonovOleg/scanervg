package ua.com.vg.scanervg.documents;

import java.util.Date;

public class DocInfo {
    private int docID;
    private String docName;
    private Date   docDate;
    private String docNumber;

    @Override
    public String toString() {
        return "DocInfo{" +
                "docID=" + docID +
                ", docName='" + docName + '\'' +
                ", docDate=" + docDate +
                ", docNumber='" + docNumber + '\'' +
                '}';
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

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }
}