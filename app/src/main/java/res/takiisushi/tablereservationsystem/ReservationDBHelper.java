package res.takiisushi.tablereservationsystem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import res.takiisushi.tablereservationsystem.ReservationContract.ReservationEntry;

public class ReservationDBHelper extends SQLiteOpenHelper {
    private static ReservationDBHelper instance;

    public static final String DATABASE_NAME = "reservationlist.db";
    public static final int DATABASE_VERSION = 4;

    private ReservationDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized ReservationDBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (instance == null) {
            instance = new ReservationDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_RESERVATION_TABLE = "CREATE TABLE " +
                ReservationEntry.TABLE_NAME + " (" +
                ReservationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReservationEntry.COLUMN_NAME + " TEXT, " +
                ReservationEntry.COLUMN_NUMBER + " TEXT NOT NULL, " +
                ReservationEntry.COLUMN_TIME + " TEXT NOT NULL, " +
                ReservationEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                ReservationEntry.COLUMN_GUESTS + " TEXT NOT NULL, " +
                ReservationEntry.COLUMN_TABLES + " TEXT NOT NULL );";

        db.execSQL(SQL_CREATE_RESERVATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ReservationEntry.TABLE_NAME);
        onCreate(db);
    }
}
