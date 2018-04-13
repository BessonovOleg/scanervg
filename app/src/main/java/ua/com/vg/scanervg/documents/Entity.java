package ua.com.vg.scanervg.documents;

public class Entity {
    private int entid;
    private String entname;
    private String entCode;

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


    public String getEntCode() {
        return entCode;
    }

    public void setEntCode(String entCode) {
        this.entCode = entCode;
    }
}
