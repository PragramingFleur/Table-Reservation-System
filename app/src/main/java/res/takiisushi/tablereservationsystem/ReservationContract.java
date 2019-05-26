package res.takiisushi.tablereservationsystem;

import android.provider.BaseColumns;

public class ReservationContract {

    private ReservationContract() {
    }

    public static final class ReservationEntry implements BaseColumns {
        public static final String TABLE_NAME = "reservationList";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NUMBER = "number";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_GUESTS = "guests";
        public static final String COLUMN_TABLES = "tables";
    }

}
