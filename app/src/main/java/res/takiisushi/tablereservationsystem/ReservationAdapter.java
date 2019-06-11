package res.takiisushi.tablereservationsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {
    private static ReservationAdapter adapter;
    private Context mContext;
    private Cursor mCursor;
    private OnItemClickListener mListener;
    private String TAG = "RESERVATION-ADAPTOR";

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }

    private ReservationAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public static synchronized ReservationAdapter getAdapter(Context context, Cursor cursor) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
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

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder reservationViewHolder, int i) {
        if (!mCursor.moveToPosition(i)) {
            return;
        }

        String time = mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_TIME));
        String guests = mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_GUESTS));
        String number = mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_NUMBER));
        long id = mCursor.getLong(mCursor.getColumnIndex(ReservationContract.ReservationEntry._ID));
        int isWindow = mCursor.getInt(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_WINDOW));
        int isBirthday = mCursor.getInt(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_BIRTHDAY));
        int isSofa = mCursor.getInt(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_SOFA));

        reservationViewHolder.timeText.setText(time);
        reservationViewHolder.guestsText.setText(guests);
        reservationViewHolder.numberText.setText(number);
        reservationViewHolder.itemView.setTag(id);
        if (isWindow == 1) {
            reservationViewHolder.windowIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getDrawable(R.drawable.ic_window_black_12dp), null);
        }
        if (isBirthday == 1) {
            reservationViewHolder.birthdayIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getDrawable(R.drawable.ic_birthday_black_12dp), null);
        }
        if (isSofa == 1) {
            reservationViewHolder.sofaIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getDrawable(R.drawable.ic_sofa_black_12dp), null);
        }

        List<String> afterTimes = new ArrayList<>();
        List<String> beforeTimes = new ArrayList<>();

        String dateFormat = "HH:mm";
        //13:00-13:59
        String after13 = "12:59";
        String before14 = "14:00";
        afterTimes.add(after13);
        beforeTimes.add(before14);

        //14:00-14:59
        String after14 = "13:59";
        String before15 = "15:00";
        afterTimes.add(after14);
        beforeTimes.add(before15);

        //15:00-15:59
        String after15 = "14:59";
        String before16 = "16:00";
        afterTimes.add(after15);
        beforeTimes.add(before16);

        //16:00-16:59
        String after16 = "15:59";
        String before17 = "17:00";
        afterTimes.add(after16);
        beforeTimes.add(before17);

        //17:00-17:59
        String after17 = "16:59";
        String before18 = "18:00";
        afterTimes.add(after17);
        beforeTimes.add(before18);

        //18:00-18:59
        String after18 = "17:59";
        String before19 = "19:00";
        afterTimes.add(after18);
        beforeTimes.add(before19);

        //19:00-19:59
        String after19 = "18:59";
        String before20 = "20:00";
        afterTimes.add(after19);
        beforeTimes.add(before20);

        //20:00-20:59
        String after20 = "19:59";
        String before21 = "21:00";
        afterTimes.add(after20);
        beforeTimes.add(before21);

        //21:00-22:00
        String after21 = "20:59";
        String before22 = "22:01";
        afterTimes.add(after21);
        beforeTimes.add(before22);

        Calendar afterTime = Calendar.getInstance();
        Calendar beforeTime = Calendar.getInstance();
        Calendar nowTime = Calendar.getInstance();
        try {
            nowTime = setTimeToCalendar(dateFormat, time, false);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date curDate = nowTime.getTime();

        for (int counter = 0; counter < afterTimes.size(); counter++) {
            try {
                afterTime = setTimeToCalendar(dateFormat, afterTimes.get(counter), false);
                beforeTime = setTimeToCalendar(dateFormat, beforeTimes.get(counter), false);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (curDate.after(afterTime.getTime()) && curDate.before(beforeTime.getTime())) {
                Log.d(TAG, "onBindViewHolder: after time: " + afterTime.getTime().toString() + " before time: " + beforeTime.getTime().toString() + "now time: " + nowTime.getTime().toString());

                if (counter % 2 == 0) {
                    reservationViewHolder.itemView.setBackground(mContext.getDrawable(R.drawable.rounded_darkbluegrey_shape));
                } else {
                    reservationViewHolder.itemView.setBackground(mContext.getDrawable(R.drawable.rounded_lightbluegrey_shape));
                }
            }
        }
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
        public TextView numberText;
        public TextView windowIcon;
        public TextView birthdayIcon;
        public TextView sofaIcon;

        public ReservationViewHolder(@NonNull final View itemView) {
            super(itemView);

            timeText = itemView.findViewById(R.id.reservationTime);
            guestsText = itemView.findViewById(R.id.reservationGuests);
            numberText = itemView.findViewById(R.id.reservationMobileNum);
            windowIcon = itemView.findViewById(R.id.reservationWindow);
            birthdayIcon = itemView.findViewById(R.id.reservationBirthday);
            sofaIcon = itemView.findViewById(R.id.reservationSofa);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position, itemView);
                        }
                    }
                }
            });
        }
    }

    private Calendar setTimeToCalendar(String dateFormat, String date, boolean addADay) throws ParseException {
        Date time = new SimpleDateFormat(dateFormat).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);

        if (addADay) {
            cal.add(Calendar.DATE, 1);
        }
        return cal;
    }
}
