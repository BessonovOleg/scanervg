package ua.com.vg.scanervg;

import org.junit.Test;

import java.util.List;

import ua.com.vg.scanervg.activities.MainActivity;
import ua.com.vg.scanervg.dao.DatabaseManager;
import ua.com.vg.scanervg.documents.DocInfo;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testDb(){
        MainActivity mainActivity = new MainActivity();
        DatabaseManager db = new DatabaseManager(mainActivity);
        List<DocInfo> docInfoList = db.getDocList();

        for(DocInfo d:docInfoList){
            System.out.println(d.toString());
        }
    }
}