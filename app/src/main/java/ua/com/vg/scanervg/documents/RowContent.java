package ua.com.vg.scanervg.documents;

import ua.com.vg.scanervg.documents.Entity;

public class RowContent {
    private Entity entity;
    private double qty;
    private int rowno;

    public String getEntName(){
        String result = "<не указан>";
        if(entity != null){
            result = entity.getEntname();
        }
        return result;
    }

    public String getEntCode(){
        String result = "<не указан>";

        if(entity != null ){
            result = entity.getEntCode();
        }

        return result;
    }


    public RowContent() {
        qty = 0;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public void setRowno(int rowno) {
        this.rowno = rowno;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public double getQty() {
        return qty;
    }

    public int getRowno() {
        return rowno;
    }

    public void addQty(double qtyAdd){
        qty += qtyAdd;
    }
}
