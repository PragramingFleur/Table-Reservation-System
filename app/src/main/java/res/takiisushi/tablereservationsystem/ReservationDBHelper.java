package res.takiisushi.tablereservationsystem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import res.takiisushi.tablereservationsystem.ReservationContract.ReservationEntry;

public class ReservationDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "reservationlist.db";
    public static final int DATABASE_VERSION = 1;

    public ReservationDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_RESERVATIONLIST_TABLE = "CREATE TABLE " +
                ReservationEntry.TABLE_NAME + " (" +
                ReservationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReservationEntry.COLUMN_TIME + " TEXT NOT NULL, " +
                ReservationEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                ReservationEntry.COLUMN_NAME + " TEXT, " +
                ReservationEntry.COLUMN_GUESTS + " TEXT NOT NULL, " +
                ReservationEntry.COLUMN_TABLENUM + " TEXT NOT NULL, " +
                ReservationEntry.COLUMN_NUMBER + " INTEGER NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_RESERVATIONLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ReservationEntry.TABLE_NAME);
        onCreate(db);
    }
}
