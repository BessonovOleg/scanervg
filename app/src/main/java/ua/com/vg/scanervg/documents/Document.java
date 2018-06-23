package ua.com.vg.scanervg.documents;

import android.app.Application;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ua.com.vg.scanervg.async.DocumentSaver;
import ua.com.vg.scanervg.model.Agent;
import ua.com.vg.scanervg.model.Entity;
import ua.com.vg.scanervg.utils.DocumentsKind;

public class Document {
    private int docId;
    private Entity makedEntity;
    private List<RowContent> contentList;
    private String docMemo;
    private Agent agentFrom;
    private Agent agentTo;
    private double docSum;
    private String docNo;
    private DocumentsKind documentsKind;
    private String strDocate;

    public String getStrDocate() {
        return strDocate;
    }

    public void setStrDocate(String strDocate) {
        this.strDocate = strDocate;
    }

    public DocumentsKind getDocumentsKind() {
        return documentsKind;
    }

    public void setDocumentsKind(DocumentsKind documentsKind) {
        this.documentsKind = documentsKind;
    }

    public List<RowContent> getContentList() {
        return contentList;
    }

    public void setContentList(List<RowContent> contentList) {
        this.contentList = contentList;
        calcDocSum();
    }

    public Document(DocumentsKind docKind){
        this();
        setDocumentsKind(docKind);
    }

    public Document() {
        contentList = new ArrayList<>();
        docId = 0;
        docMemo = "";
        makedEntity = new Entity(0,"","");
        docSum = 0;
        agentFrom = new Agent(0,"");
        agentTo = new Agent(0,"");
        docNo = "";
        setNowDate();
    }

    private void setNowDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        strDocate = sdf.format(new Date());
    }

    private void calcDocSum(){
        docSum = 0;
        for(RowContent rc:contentList){
            docSum += rc.getSum();
        }
    }

    public Entity getMakedEntity() {
        return makedEntity;
    }

    public void setMakedEntity(Entity makedEntity) {
        this.makedEntity = makedEntity;
    }



    public void addRowWithPrice(Entity entity,double qty,double price){
        if (entity == null){
            return;
        }
        int rowno = contentList.size() + 1;
        boolean entExists = false;

        RowContent row = new RowContent();
        row.setRowno(rowno);
        row.setEntity(entity);
        row.setQty(qty);
        row.setPrice(price);
        row.setSum(qty * price);

        for(RowContent rc:contentList){
            if(rc.getEntity().equals(entity)){
                rc.addQty(1);
                entExists = true;
            }
        }
        if(!entExists){
            contentList.add(row);
        }
        calcDocSum();

    }


    public void addRow(Entity entity,double qty){
        if (entity == null){
            return;
        }
        int rowno = contentList.size() + 1;
        boolean entExists = false;

        RowContent row = new RowContent();
        row.setRowno(rowno);
        row.setEntity(entity);
        row.setQty(qty);

        for(RowContent rc:contentList){
            if(rc.getEntity().equals(entity)){
                rc.addQty(1);
                entExists = true;
             }
        }
        if(!entExists){
            contentList.add(row);
        }
        calcDocSum();
    }

    public void addDistinctRow(Entity entity,double qty){
        if (entity == null){
            return;
        }
        int rowno = contentList.size() + 1;
        boolean entExists = false;

        RowContent row = new RowContent();
        row.setRowno(rowno);
        row.setEntity(entity);
        row.setQty(qty);


        for(RowContent rc:contentList){
            if(rc.getEntity().equals(entity)){
                entExists = true;
            }
        }
        if(!entExists){
            contentList.add(row);
        }
        calcDocSum();
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public String getDocMemo() {
        return docMemo;
    }

    public void setDocMemo(String docMemo) {
        this.docMemo = docMemo;
    }

    public Agent getAgentFrom() {
        return agentFrom;
    }

    public void setAgentFrom(Agent agentFrom) {
        this.agentFrom = agentFrom;
    }

    public Agent getAgentTo() {
        return agentTo;
    }

    public void setAgentTo(Agent agentTo) {
        this.agentTo = agentTo;
    }

    public double getDocSum() {
        return docSum;
    }

    public void setDocSum(double docSum) {
        this.docSum = docSum;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public int save() throws IllegalStateException{
        int result = 0;
        DocumentSaver documentSaver = new DocumentSaver(documentsKind);
        try {
            documentSaver.execute(this);
            result = documentSaver.get();
        }catch (Exception e){
            new IllegalStateException(e.getMessage());
        }
        return result;
    }

    @Override
    public String toString() {
        return "Document{" +
                "docId=" + docId +
                ", makedEntity=" + makedEntity +
                ", contentList=" + contentList +
                ", docMemo='" + docMemo + '\'' +
                ", agentFrom=" + agentFrom +
                ", agentTo=" + agentTo +
                ", docSum=" + docSum +
                ", docNo='" + docNo + '\'' +
                ", documentsKind=" + documentsKind +
                '}';
    }
}
