package l3pro20162017.domotiquepro;


public final class KeyWords {

    public static String NUMERO_TELEPHONE = "telephone";
    public static  String COMPTEUR = "compteur";

    // ============ DATABASE ============
    public static  String DATABADE_NAME = "DomoPro.db";
    public static int DATABASE_VERSION = 1;

    public static String DATABASE_TABLE_ACTIONS = "actions";
    public  static  String CREATE_DATABASE_ACTIONS = "CREATE TABLE actions (_id INTEGER PRIMARY KEY " +
            "AUTOINCREMENT, libelle TEXT NOT NULL, code TEXT NOT NULL, confirm BOOLEAN NOT NULL);";

    public static String DATABASE_TABLE_LOGS = "domo_logs";
    public static String CREATE_DATABASE_LOGS = "CREATE TABLE domo_logs (_id INTEGER PRIMARY KEY "+
            "AUTOINCREMENT, dateStr TEXT NOT NULL, message TEXT NOT NULL);";

    // ============ SMS READER ============
    public static String SMS_URI_INBOX = "content://sms/inbox";
}
