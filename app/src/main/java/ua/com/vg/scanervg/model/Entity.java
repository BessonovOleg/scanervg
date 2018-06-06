package ua.com.vg.scanervg.model;

public class Entity {
    private int entid;
    private String entname;
    private String entCode;
    private String unit;
    private int seriesId;

    public Entity() {
        entid = 0;
        entname = "";
        entCode = "";
        unit = "";
        seriesId = 0;
    }

    public Entity(int entid, String entname, String entCode) {
        this.entid = entid;
        this.entname = entname;
        this.entCode = entCode;
    }

    public int getEntid() {
        return entid;
    }

    public void setEntid(int entid) {
        this.entid = entid;
    }

    public String getEntname() {
        return entname;
    }

    public void setEntname(String entname) {
        this.entname = entname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return entid == entity.entid;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getEntCode() {
        return entCode;
    }

    public void setEntCode(String entCode) {
        this.entCode = entCode;
    }

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
    }
}
