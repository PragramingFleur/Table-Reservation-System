package res.takiisushi.tablereservationsystem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableStatusDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "statuslist.db";
    public static final int DATABASE_VERSION = 1;
    private static TableStatusDBHelper instance;

    private TableStatusDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized TableStatusDBHelper getInstance(Context context) {

        if (instance == null) {
            instance = new TableStatusDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_STATUS_TABLE = "CREATE TABLE " +
                TableStatusContract.TableStatusEntry.TABLE_NAME + " (" +
                TableStatusContract.TableStatusEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TableStatusContract.TableStatusEntry.COLUMN_TABLENUM + " TEXT NOT NULL, " +
                TableStatusContract.TableStatusEntry.COLUMN_STATUS + " TEXT NOT NULL );";

        db.execSQL(SQL_CREATE_STATUS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TableStatusContract.TableStatusEntry.TABLE_NAME);
        onCreate(db);
    }
}
