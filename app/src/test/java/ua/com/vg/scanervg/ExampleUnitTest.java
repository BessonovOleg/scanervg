package ua.com.vg.scanervg;

import org.junit.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import ua.com.vg.scanervg.activities.DocContentEdit;
import ua.com.vg.scanervg.activities.MainActivity;
import ua.com.vg.scanervg.dao.DatabaseManager;
import ua.com.vg.scanervg.documents.DocInfo;
import ua.com.vg.scanervg.documents.Document;
import ua.com.vg.scanervg.model.Entity;

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
    public void testRoundDigit(){
        Double d = 123.45;
        DecimalFormatSymbols dcf = new DecimalFormatSymbols(Locale.US);
        String result = new DecimalFormat("#0.00",dcf).format(d);
        System.out.println(result);
    }

    /*
    @Test
    public void testDocDate(){
        Document document = new Document();
        assertEquals("2018.06.13",document.getStrDocate());
    }
*/
    /*
    @Test
    public void testGetNetPrice(){

        DatabaseManager databaseManager = new DatabaseManager();

        Entity entity = new Entity(75,"Тестовый объект","000");
        double price = databaseManager.getEntityPrice(entity);
        double value = 100.0;
        System.out.println(price);
        assertEquals(value,price,0.0);
    }
*/
}