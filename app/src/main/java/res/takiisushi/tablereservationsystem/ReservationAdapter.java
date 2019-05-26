package res.takiisushi.tablereservationsystem;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {
    private static ReservationAdapter adapter;
    private Context mContext;
    private Cursor mCursor;

    private ReservationAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public static synchronized ReservationAdapter getAdapter(Context context, Cursor cursor) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (adapter == null) {
            adapter = new ReservationAdapter(context.getApplicationContext(), cursor);
        }
        return adapter;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.reservation_item, viewGroup, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder reservationViewHolder, int i) {
        if (!mCursor.moveToPosition(i)) {
            return;
        }

        String time = mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_TIME));
        String guests = mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_GUESTS));
        String table = mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_TABLES));
        String number = mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_NUMBER));

        reservationViewHolder.timeText.setText(time);
        reservationViewHolder.guestsText.setText(guests);
        reservationViewHolder.tableText.setText(table);
        reservationViewHolder.numberText.setText(number);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    public class ReservationViewHolder extends RecyclerView.ViewHolder {
        public TextView timeText;
        public TextView guestsText;
        public TextView tableText;
        public TextView numberText;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);

            timeText = itemView.findViewById(R.id.reservationTime);
            guestsText = itemView.findViewById(R.id.reservationGuests);
            tableText = itemView.findViewById(R.id.reservationTable);
            numberText = itemView.findViewById(R.id.reservationMobileNum);
        }
    }
}
