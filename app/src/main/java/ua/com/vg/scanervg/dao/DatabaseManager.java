package ua.com.vg.scanervg.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ua.com.vg.scanervg.documents.DocInfo;

public class DatabaseManager {
    private Connection conncetion;
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

        conncetion = DriverManager.getConnection(dbUrl, login, password);
    }

    public List<DocInfo> getDocList(){
        List<DocInfo> result = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;

        if(conncetion == null){
            try {
                connect();
            }catch (SQLException|ClassNotFoundException ex){
                throw new RuntimeException(ex);
            }
        }

        try{
            st = conncetion.createStatement();
            rs = st.executeQuery("exec getAndroidDocList");
            if (rs != null) {
                while (rs.next()) {
                    DocInfo tmpDocInfo = new DocInfo();
                    tmpDocInfo.setDocID(rs.getInt("docId"));
                    tmpDocInfo.setDocName(rs.getString("docName"));
                    tmpDocInfo.setDocNumber(rs.getString("docNo"));
                    //tmpDocInfo.setDocDate(rs.getDate("docDate"));
                    result.add(tmpDocInfo);
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


    public void test(Context context, TextView tv,String code){
        String res = "";
        String log = "";
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Connection con = null;
            Statement st = null;
            ResultSet rs = null;

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
            String passw = sp.getString("password","");


            try {
                con = DriverManager.getConnection(dbUrl, login, passw);
                if (con != null) {
                    log += "\n connect";
                    st = con.createStatement();
                    rs = st.executeQuery("select name from Entity where code = '"+code+"'");
                    if (rs != null) {
                        while (rs.next()) {
                            res = rs.getString(1);
                            tv.setText(res);
                        }
                    }
                }
            } catch (SQLException e) {
                log += "\n" + e.getMessage();
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (st != null) st.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
