package ua.com.vg.scanervg.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ua.com.vg.scanervg.documents.Agent;
import ua.com.vg.scanervg.documents.DocInfo;
import ua.com.vg.scanervg.documents.Document;
import ua.com.vg.scanervg.documents.Entity;
import ua.com.vg.scanervg.documents.RowContent;
import ua.com.vg.scanervg.utils.ScanKind;

public class DatabaseManager {
    private Connection connection;
    private Context ctx;

    public DatabaseManager(Context ctx) {
        this.ctx = ctx;
        try {
            connect();
        }catch (SQLException|ClassNotFoundException ex){
            throw new RuntimeException(ex);
        }
    }

    public void connect() throws SQLException,ClassNotFoundException{

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        Class.forName("net.sourceforge.jtds.jdbc.Driver");

        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:jtds:sqlserver://");
        sb.append(sp.getString("ipAddress",""));
        sb.append(":");
        sb.append(sp.getString("port",""));
        sb.append("/");
        sb.append(sp.getString("dbName",""));
        sb.append(";");

        String dbUrl = sb.toString();
        String login = sp.getString("login","");
        String password = sp.getString("password","");

      //  String dbUrl = "jdbc:jtds:sqlserver://192.168.0.128:1433/android";
      //  String login = "admin";
      //  String password = "123";

        connection = DriverManager.getConnection(dbUrl, login, password);
    }

    public List<DocInfo> getDocList(int numberDocKind){
        List<DocInfo> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        if(connection == null){
            try {
                connect();
            }catch (SQLException|ClassNotFoundException ex){
                throw new RuntimeException(ex);
            }
        }

        try{
            ps = connection.prepareStatement("exec getAndroidDocList ?");
            ps.setInt(1,numberDocKind);
            rs = ps.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    DocInfo tmpDocInfo = new DocInfo();
                    tmpDocInfo.setDocID(rs.getInt("docId"));
                    tmpDocInfo.setDocName(rs.getString("docName"));
                    tmpDocInfo.setDocNumber(rs.getString("docNo"));
                    tmpDocInfo.setDocMemo(rs.getString("docMemo"));
                    tmpDocInfo.setDocSum(rs.getDouble("docSum"));
                    result.add(tmpDocInfo);
                }
            }
        }catch (SQLException ex){
            throw new RuntimeException();
        }finally {
            if(ps!= null){
                try {
                    ps.close();
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    public List<Entity> getEntityByCode(String code, ScanKind scanKind){
        List<Entity> result = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        int entityKind = 0;

        if(scanKind == ScanKind.scanContentEntity){
            entityKind = 1;
        }else if(scanKind == ScanKind.scanMakedEntity){
            entityKind = 2;
        }

        if(connection == null){
            try {
                connect();
            }catch (SQLException|ClassNotFoundException ex){
                throw new RuntimeException(ex);
            }
        }

        try{
            st = connection.createStatement();
            rs = st.executeQuery("exec getAndroidEntityByCode '" + code + "'," + entityKind);
            if (rs != null) {
                while (rs.next()) {
                    Entity entity = new Entity(0,"","");
                    entity.setEntid(rs.getInt("ID"));
                    entity.setEntname(rs.getString("NAME"));
                    entity.setEntCode(rs.getString("CODE"));
                    entity.setUnit(rs.getString("UNIT"));
                    result.add(entity);
                }
            }
        }catch (SQLException ex){
            throw new RuntimeException(ex);
        }finally {
            if(st!= null){
                try {
                    st.close();
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    public Document getDocumentByID(int docID){
        Document result = new Document();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RowContent> rowContents = new ArrayList<>();

        if(connection == null){
            try {
                connect();
            }catch (SQLException|ClassNotFoundException ex){
                throw new RuntimeException(ex);
            }
        }

        //Get document header
        try{
            ps = connection.prepareStatement("exec getAndroidDocumentHeader ?");
            ps.setInt(1,docID);
            rs = ps.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    Entity makedEntity = new Entity();

                    makedEntity.setEntid(rs.getInt("ENTID"));
                    makedEntity.setEntname(rs.getString("ENTNAME"));
                    makedEntity.setEntCode(rs.getString("ENTCODE"));
                    result.setMakedEntity(makedEntity);

                    result.setDocId(rs.getInt("DOCID"));
                    result.setDocMemo(rs.getString("DOCMEMO"));
                }
            }
        }catch (SQLException ex){
            throw new RuntimeException(ex);
        }finally {
            if(ps!= null){
                try {
                    ps.close();
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }

        //Get document contents
        ps = null;
        rs = null;
        try{
            ps = connection.prepareStatement("exec getAndroidDocumentContent ?");
            ps.setInt(1,docID);
            rs = ps.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    Entity entity = new Entity();
                    RowContent rowContent = new RowContent();

                    entity.setEntid(rs.getInt("ENTID"));
                    entity.setEntname(rs.getString("ENTNAME"));
                    entity.setEntCode(rs.getString("ENTCODE"));
                    entity.setUnit(rs.getString("UN"));

                    rowContent.setEntity(entity);
                    rowContent.setRowno(rs.getInt("ROWNO"));
                    rowContent.setQty(rs.getDouble("QTY"));

                    rowContents.add(rowContent);
                }
            }
        }catch (SQLException ex){
            throw new RuntimeException(ex);
        }finally {
            if(ps!= null){
                try {
                    ps.close();
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        result.setContentList(rowContents);
        return result;
    }

    public int saveDocument(Document document){
        PreparedStatement ps = null;
        ResultSet rs = null;
        int result = -1;
        List<RowContent> rowContents = new ArrayList<>();

        if(document == null){
            return result;
        }
        rowContents = document.getContentList();

        if(connection == null){
            try {
                connect();
            }catch (SQLException|ClassNotFoundException ex){
                throw new RuntimeException(ex);
            }
        }

        //Save caption
        try{
            ps = connection.prepareStatement("exec AndroidUpdateDocuments ?,?,?");
            ps.setInt(1,document.getDocId());
            ps.setInt(2,document.getMakedEntity().getEntid());
            ps.setString(3,document.getDocMemo());
            rs = ps.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    document.setDocId(rs.getInt("DOCID"));
                    result = rs.getInt("DOCID");
                }
            }
        }catch (SQLException ex){
            throw new RuntimeException(ex);
        }finally {
            if(ps!= null){
                try {
                    ps.close();
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }

        //Save contents
        if(rowContents.size() > 0){
            for(RowContent rc:rowContents){
                try{
                    ps = connection.prepareStatement("exec AndroidUpdateJournal ?,?,?,?");
                    ps.setInt(1,document.getDocId());
                    ps.setInt(2,rc.getRowno());
                    ps.setInt(3,rc.getEntityID());
                    ps.setDouble(4,rc.getQty());
                    ps.executeQuery();
                }catch (SQLException ex){
                    throw new RuntimeException(ex);
                }finally {
                    if(ps!= null){
                        try {
                            ps.close();
                        }catch (Exception e){
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        return result;
    }

    public List<Agent> getWarehouses(){
        List<Agent> result = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;

        if(connection == null){
            try {
                connect();
            }catch (SQLException|ClassNotFoundException ex){
                throw new RuntimeException(ex);
            }
        }

        try{
            st = connection.createStatement();
            rs = st.executeQuery("exec getAndroidWarehouses");
            if (rs != null) {
                while (rs.next()) {
                    Agent warehouse = new Agent();
                    warehouse.setId(rs.getInt("id"));
                    warehouse.setName(rs.getString("name"));
                    result.add(warehouse);
                }
            }
        }catch (SQLException ex){
            throw new RuntimeException();
        }finally {
            if(st!= null){
                try {
                    st.close();
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    public List<Agent> findAgentByName(String filter){
        List<Agent> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        if(connection == null){
            try {
                connect();
            }catch (SQLException|ClassNotFoundException ex){
                throw new RuntimeException(ex);
            }
        }

        try{
            ps = connection.prepareStatement("exec getAndroidAgentByName ?");
            ps.setString(1,filter);
            rs = ps.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    Agent agent = new Agent();
                    agent.setId(rs.getInt("id"));
                    agent.setName(rs.getString("name"));
                    result.add(agent);
                }
            }
        }catch (SQLException ex){
            throw new RuntimeException(ex);
        }finally {
            if(ps!= null){
                try {
                    ps.close();
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    public Agent getAgentById(int id){
        Agent result = new Agent();
        PreparedStatement ps = null;
        ResultSet rs = null;

        if(connection == null){
            try {
                connect();
            }catch (SQLException|ClassNotFoundException ex){
                throw new RuntimeException(ex);
            }
        }

        try{
            ps = connection.prepareStatement("exec getAndroidAgentById ?");
            ps.setInt(1,id);
            rs = ps.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    result.setId(rs.getInt("id"));
                    result.setName(rs.getString("name"));
                }
            }
        }catch (SQLException ex){
            throw new RuntimeException(ex);
        }finally {
            if(ps!= null){
                try {
                    ps.close();
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }



}
