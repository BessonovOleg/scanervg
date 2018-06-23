package ua.com.vg.scanervg.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class AppUtils {
    public static String roundAndConvertToStringDigit(Double digit){
        if (digit == null){
            return "";
        }
        DecimalFormatSymbols dcf = new DecimalFormatSymbols(Locale.US);
        String result = new DecimalFormat("#0.00",dcf).format(digit);
        result.replace(",",".");
        return result;
    }
}
