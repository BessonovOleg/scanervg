package ua.com.vg.scanervg.documents;

import java.util.ArrayList;
import java.util.List;

public class Document {
    private int docId;
    private Entity makedEntity;
    private List<RowContent> contentList;
    private String docMemo;

    public List<RowContent> getContentList() {
        return contentList;
    }

    public void setContentList(List<RowContent> contentList) {
        this.contentList = contentList;
    }

    public Document() {
        contentList = new ArrayList<>();
        docId = 0;
        docMemo = "";
    }

    public Entity getMakedEntity() {
        return makedEntity;
    }

    public void setMakedEntity(Entity makedEntity) {
        this.makedEntity = makedEntity;
    }

    public void addRow(Entity entity,double qty){
        if (entity == null){
            return;
        }
        int rowno = contentList.size() + 1;

        RowContent row = new RowContent();
        row.setRowno(rowno);
        row.setEntity(entity);
        row.setQty(qty);

        for(RowContent rc:contentList){
            if(rc.getEntity().equals(entity)){
                rc.addQty(1);
                return;
            }
        }
        contentList.add(row);
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
}
