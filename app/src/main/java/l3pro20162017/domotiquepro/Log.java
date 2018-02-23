package l3pro20162017.domotiquepro;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private String dateStr;
    private String message;

    public Log(){}
    public Log(String dateStr, String message){this.dateStr = dateStr; this.message = message; }

    public String getDateStr() {
        return dateStr;
    }

    public void setDate(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static SimpleDateFormat getSimpleDateFormat(){
        return new SimpleDateFormat("dd MM yyyy 'à'  hh:mm:ss");
    }

    public static String getDateString(Date date){
        SimpleDateFormat ft =  new SimpleDateFormat("dd MM yyyy 'a'  hh:mm:ss");
        return ft.format(date);
    }
}
