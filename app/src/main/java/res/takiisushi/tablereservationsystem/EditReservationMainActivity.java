package res.takiisushi.tablereservationsystem;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditReservationMainActivity extends AppCompatActivity
        implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private static final String TAG = "EDIT-RESERVATION";
    Context mContext;

    private EditText editName;
    private EditText editNumber;
    private TextView editDate;
    private TextView editTime;
    private EditText editAdultGuestNum;
    private EditText editChildGuestNum;

    private SQLiteDatabase database;
    private ReservationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation_main);
        mContext = this;

        //Initializing all fields from the form
        editName = findViewById(R.id.editName);
        editNumber = findViewById(R.id.editMobileNum);
        editDate = findViewById(R.id.selectDateNewReservation);
        editTime = findViewById(R.id.selectTimeNewReservation);
        editAdultGuestNum = findViewById(R.id.editAdultGuestNum);
        editChildGuestNum = findViewById(R.id.editChildrenGuestNum);

        //Initializing DB and adaptor
        ReservationDBHelper dbHelper = ReservationDBHelper.getInstance(mContext);
        database = dbHelper.getWritableDatabase();
        adapter = ReservationAdapter.getAdapter(mContext, getAllItems());

        //get item id from details dialog
        final long id = getIntent().getLongExtra("id", -1);
        final int arrived = getIntent().getIntExtra("arrived", 0);

        //set all necessary fields
        setFormFields(id);

        //Sets up action bar menus and title
        setupActionBar();

        //Sets up dialogs for the time and date picker
        setupDialogs();

        //Button and click listener for when add Reservation is clicked
        Button addReservationButton = findViewById(R.id.reserveButton);

        addReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Makes sure fields are correct before adding to db
                boolean isCorrect = checkFields(editNumber, editDate, editTime, editAdultGuestNum, editChildGuestNum);
                if (isCorrect) {
                    //adds reservation to the db
                    editReservationInDB(editName, editNumber, editDate, editTime, editAdultGuestNum, editChildGuestNum, id, arrived);
                }
            }
        });

        //Button and click listener for when cancel Reservation is clicked
        Button cancelReservationButton = findViewById(R.id.cancelButton);

        cancelReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewReservationMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                finish();
            }
        });
    }

    private void setFormFields(long id) {
        Cursor cursor = getItemAtID(id);
        if (cursor.moveToFirst() && cursor.getCount() >= 1) {
            do {
                if (!cursor.getString(cursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_NAME)).equals("")) {
                    editName.setText(cursor.getString(cursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_NAME)));
                }
                editNumber.setText(cursor.getString(cursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_NUMBER)));
                editDate.setText(cursor.getString(cursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_DATE)));
                editTime.setText(cursor.getString(cursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_TIME)));
                List<String> guests = Arrays.asList(cursor.getString(cursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_GUESTS)).split("\\s+"));
                editAdultGuestNum.setText(guests.get(1));
                editChildGuestNum.setText(guests.get(3));
            } while (cursor.moveToNext());
        }
    }

    private Cursor getItemAtID(long id) {
        return database.query(
                ReservationContract.ReservationEntry.TABLE_NAME,
                null,
                ReservationContract.ReservationEntry._ID + "=" + id,
                null,
                null,
                null,
                ReservationContract.ReservationEntry.COLUMN_TIME + " ASC"
        );
    }

    private Cursor getAllItems() {
        return database.query(
                ReservationContract.ReservationEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                ReservationContract.ReservationEntry.COLUMN_TIME + " ASC"
        );
    }

    private void editReservationInDB(EditText editName, EditText editNumber, TextView editDate, TextView editTime, EditText editAdultGuestNum, EditText editChildGuestNum, long id, int arrived) {
        String guests = "V: " + editAdultGuestNum.getText().toString() + " B: " + editChildGuestNum.getText().toString();
        ContentValues values = new ContentValues();
        values.put(ReservationContract.ReservationEntry.COLUMN_NAME, editName.getText().toString().trim());
        values.put(ReservationContract.ReservationEntry.COLUMN_NUMBER, editNumber.getText().toString().trim());
        values.put(ReservationContract.ReservationEntry.COLUMN_TIME, editTime.getText().toString());
        values.put(ReservationContract.ReservationEntry.COLUMN_DATE, editDate.getText().toString());
        values.put(ReservationContract.ReservationEntry.COLUMN_GUESTS, guests);
        values.put(ReservationContract.ReservationEntry.COLUMN_ARRIVED, arrived);

        database.update(ReservationContract.ReservationEntry.TABLE_NAME, values,
                ReservationContract.ReservationEntry._ID + "=" + id, null);

        Intent intent = new Intent(this, ViewReservationMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private boolean checkFields(EditText editNumber, TextView editDate, TextView editTime, EditText editAdultGuestNum, EditText editChildGuestNum) {
        List<Boolean> isCorrectCollection = new ArrayList<>();
        boolean isCorrect = false;

        Calendar c = Calendar.getInstance();
        Date today = c.getTime();

        //Check if required fields are filled out
        if (editNumber.getText().toString().trim().length() < 8 || editNumber.getText().toString().trim().length() > 8) {
            //Number field is empty or less than 8digits which is wrong
            isCorrectCollection.add(false);
            editNumber.setError("Invalid Number");
        } else if (editNumber.getText().toString().trim().isEmpty()) {
            isCorrectCollection.add(false);
            editNumber.setError("Required");
        }

        if (!editDate.getText().toString().isEmpty()) {
            //If date textview is not empty then check if the date is before today
            String datePicked = editDate.getText().toString();
            try {
                Date strDate = new SimpleDateFormat("yyyy/MM/dd").parse(datePicked);

                if (today.after(strDate)) {
                    //date isn't supposed to be before today
                    isCorrectCollection.add(false);
                    editDate.setError("Date has to be today or later");
                } else {
                    isCorrectCollection.add(true);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (editDate.getText().toString().isEmpty()) {
            isCorrectCollection.add(false);
            editDate.setError("Required");
        }

        if (!editTime.getText().toString().isEmpty()) {
            //Check if time is between 13:00 and 22:00
            try {
                String selectedTime = editTime.getText().toString();
                String minTime = "13:00";
                String maxTime = "22:00";

                Date selectedTimeConverted = new SimpleDateFormat("HH:mm").parse(selectedTime);
                Date minTimeConverted = new SimpleDateFormat("HH:mm").parse(minTime);
                Date maxTimeConverted = new SimpleDateFormat("HH:mm").parse(maxTime);

                Calendar calendar = Calendar.getInstance();
                Calendar calendarA = Calendar.getInstance();
                Calendar calendarB = Calendar.getInstance();

                calendar.setTime(selectedTimeConverted);
                calendarA.setTime(minTimeConverted);
                calendarB.setTime(maxTimeConverted);

                if (calendar.getTime().after(calendarA.getTime()) && calendar.getTime().before(calendarB.getTime())) {
                    isCorrectCollection.add(true);
                } else {
                    isCorrectCollection.add(false);
                    editTime.setError("Time is not between 13:00 and 22:00");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (editTime.getText().toString().isEmpty()) {
            //Check if time is empty
            isCorrectCollection.add(false);
            editTime.setError("Required");
        } else {
            isCorrectCollection.add(false);
            editTime.setError("Incorrect Time");
        }

        if (!editAdultGuestNum.getText().toString().isEmpty()) {
            if (Integer.parseInt(editAdultGuestNum.getText().toString()) == 0) {
                //check if guest number is 0
                isCorrectCollection.add(false);
                editAdultGuestNum.setError("Guest Number should be 1 or Above");
            } else if (Integer.parseInt(editAdultGuestNum.getText().toString()) > 0) {
                isCorrectCollection.add(true);
            }
        } else {
            isCorrectCollection.add(false);
            editAdultGuestNum.setError("Required");
        }

        if (editChildGuestNum.getText().toString().isEmpty()) {
            editChildGuestNum.setText("0");
            isCorrectCollection.add(true);
        } else if (!editChildGuestNum.getText().toString().isEmpty() && Integer.parseInt(editChildGuestNum.getText().toString()) >= 0) {
            isCorrectCollection.add(true);
        } else {
            isCorrectCollection.add(false);
            editChildGuestNum.setError("Required");
        }

        isCorrect = !isCorrectCollection.contains(false);

        return isCorrect;
    }

    private void setupActionBar() {
        getSupportActionBar().setTitle(getString(R.string.reservation_details_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setupDialogs() {
        //Button and method to open the timepicker dialog to choose a time for new reservation
        TextView editTime = findViewById(R.id.selectTimeNewReservation);

        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "new reservation time picker");
            }
        });

        //Button and method to open the datepicker dialog to choose a date for new reservation
        TextView editDate = findViewById(R.id.selectDateNewReservation);

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "new reservation date picker");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String selectedDateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());

        TextView editDate = findViewById(R.id.selectDateNewReservation);
        editDate.setText(selectedDateString);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView editTime = findViewById(R.id.selectTimeNewReservation);
        editTime.setText(hourOfDay + ":" + minute);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, ViewReservationMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
                this.finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
