package res.takiisushi.tablereservationsystem;

import android.provider.BaseColumns;

public class TableStatusContract {
    private TableStatusContract() {
    }

    public static final class TableStatusEntry implements BaseColumns {
        public static final String TABLE_NAME = "statusList";
        public static final String COLUMN_TABLENUM = "tableNum";
        public static final String COLUMN_STATUS = "status";
    }
}