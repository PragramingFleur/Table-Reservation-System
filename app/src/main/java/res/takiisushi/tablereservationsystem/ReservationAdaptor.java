package res.takiisushi.tablereservationsystem;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReservationAdaptor extends RecyclerView.Adapter<ReservationAdaptor.ReservationViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    public ReservationAdaptor(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.reservation_item, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReservationViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String time = mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_TIME));
        String guest = mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_GUESTS));
        String table = mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_TABLENUM));
        String mobileNumber = mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_NUMBER));

        holder.timeText.setText(time);
        holder.guestText.setText(guest);
        holder.tableText.setText(table);
        holder.mobileNumberText.setText(String.valueOf(mobileNumber));
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
        public TextView guestText;
        public TextView tableText;
        public TextView mobileNumberText;

        public ReservationViewHolder(View itemView) {
            super(itemView);

            timeText = itemView.findViewById(R.id.reservationTime);
            guestText = itemView.findViewById(R.id.reservationGuests);
            tableText = itemView.findViewById(R.id.reservationTable);
            mobileNumberText = itemView.findViewById(R.id.reservationMobileNum);
        }
    }
}
