package ua.com.vg.scanervg.documents;

import ua.com.vg.scanervg.model.Entity;

public class RowContent {
    private Entity entity;
    private double qty;
    private int rowno;
    private double price;
    private double sum;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        calcRowSum();
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
        if(qty != 0 && sum !=0 ){
            price = sum / qty;
        }
    }

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

    public int getEntityID(){
        if(entity == null){
            return 0;
        }
        return entity.getEntid();
    }

    public RowContent() {
        qty = 0;
        price = 0;
        sum = 0;
    }

    public void setQty(double qty) {
        this.qty = qty;
        calcRowSum();
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
        calcRowSum();
    }

    public String getUnitName(){
        if (entity == null){
            return "";
        }else
        {
            return entity.getUnit();
        }
    }

    private void calcRowSum(){
        sum = qty * price;
    }
}
