package res.takiisushi.tablereservationsystem;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import res.takiisushi.tablereservationsystem.ReservationContract.ReservationEntry;

public class AddReservationMainActivity extends AppCompatActivity
        implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = "ADD-RESERVATION";
    Context mContext;

    String tableSelected = "";
    int spinnerId = 0;

    private EditText editName;
    private EditText editNumber;
    private TextView editDate;
    private TextView editTime;
    private EditText editAdultGuestNum;
    private EditText editChildGuestNum;

    private SQLiteDatabase database;
    private ReservationAdapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation_main);
        mContext = this;
        //initialize the layout for adding the spinner to
        final LinearLayout root = findViewById(R.id.spinnerLayout);

        //Initializing all fields from the form
        editName = findViewById(R.id.editName);
        editNumber = findViewById(R.id.editMobileNum);
        editDate = findViewById(R.id.selectDateNewReservation);
        editTime = findViewById(R.id.selectTimeNewReservation);
        editAdultGuestNum = findViewById(R.id.editAdultGuestNum);
        editChildGuestNum = findViewById(R.id.editChildrenGuestNum);

        //Adding value to the date and time fields. Adding date today and also time to 17:00
        Calendar cal = Calendar.getInstance();
        editDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(cal.getTime()));
        editTime.setText("17:00");

        //Initializing DB and adaptor
        ReservationDBHelper dbHelper = ReservationDBHelper.getInstance(mContext);
        database = dbHelper.getWritableDatabase();
        adapter = ReservationAdapter.getAdapter(mContext, getAllItems());

        //Sets up action bar menus and title
        setupActionBar();

        //Sets up dialogs for the time and date picker
        setupDialogs();

        //Method for deleting the last spinner and adding a spinner for choosing tables
        createDeleteTableSpinner(root);

        //Button and click listener for when add Reservation is clicked
        Button addReservationButton = findViewById(R.id.reserveButton);

        addReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Makes sure fields are correct before adding to db
                boolean isCorrect = checkFields(editNumber, editDate, editTime, editAdultGuestNum, editChildGuestNum);
                if (isCorrect) {
                    //adds reservation to the db
                    addReservationToDB(editName, editNumber, editDate, editTime, editAdultGuestNum, editChildGuestNum);
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

    private void addReservationToDB(EditText editName, EditText editNumber, TextView editDate, TextView editTime, EditText editAdultGuestNum, EditText editChildGuestNum) {
        String guests = "V: " + editAdultGuestNum.getText().toString() + " B: " + editChildGuestNum.getText().toString();
        ContentValues values = new ContentValues();
        values.put(ReservationEntry.COLUMN_NAME, editName.getText().toString().trim());
        values.put(ReservationEntry.COLUMN_NUMBER, editNumber.getText().toString().trim());
        values.put(ReservationEntry.COLUMN_TIME, editTime.getText().toString());
        values.put(ReservationEntry.COLUMN_DATE, editDate.getText().toString());
        values.put(ReservationEntry.COLUMN_GUESTS, guests);
        values.put(ReservationEntry.COLUMN_TABLES, tableSelected);

        database.insert(ReservationEntry.TABLE_NAME, null, values);
        adapter.swapCursor(getAllItems());

        Intent intent = new Intent(this, ViewReservationMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    private boolean checkFields(EditText editNumber, TextView editDate, TextView editTime, EditText editAdultGuestNum, EditText editChildGuestNum) {
        List<Boolean> isCorrectCollection = new ArrayList<>();
        boolean isCorrect;

        Calendar c = Calendar.getInstance();
        Date today = c.getTime();

        TextView tableWarning = findViewById(R.id.tableTextView);

        //Check if required fields are filled out
        if (editNumber.getText().toString().trim().length() < 8) {
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
                @SuppressLint("SimpleDateFormat") Date strDate = new SimpleDateFormat("yyyy/MM/dd").parse(datePicked);

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

                @SuppressLint("SimpleDateFormat") Date selectedTimeConverted = new SimpleDateFormat("HH:mm").parse(selectedTime);
                @SuppressLint("SimpleDateFormat") Date minTimeConverted = new SimpleDateFormat("HH:mm").parse(minTime);
                @SuppressLint("SimpleDateFormat") Date maxTimeConverted = new SimpleDateFormat("HH:mm").parse(maxTime);

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

        if (!tableSelected.isEmpty()) {
            //Checking for duplicate tables
            List<String> tableList = Arrays.asList(tableSelected.split(","));

            if (tableList.size() > 1) {
                final Set<Integer> set = new HashSet<>();

                for (String string : tableList) {
                    if (!set.add(Integer.parseInt(string))) {
                        tableWarning.setError("Cannot Repeat Table number");
                        isCorrectCollection.add(false);
                        break;
                    } else {
                        isCorrectCollection.add(true);
                    }
                }
            }
        } else {
            isCorrectCollection.add(true);
            tableWarning.setError("Need Table");
        }

        isCorrect = !isCorrectCollection.contains(false);

        return isCorrect;
    }

    @SuppressLint("NewApi")
    private void setupActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.add_reservation_title));
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

    private void createDeleteTableSpinner(final LinearLayout layout) {
        final AdapterView.OnItemSelectedListener listener = this;
        //Button and logic to add new spinner when add new table1 button is pressed
        FloatingActionButton addTableButton = findViewById(R.id.addTableButton);

        addTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner newSpinner = new Spinner(AddReservationMainActivity.this);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AddReservationMainActivity.this, R.array.table_Numbers, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                newSpinner.setAdapter(adapter);
                newSpinner.setId(spinnerId);
                spinnerId = spinnerId + 1;
                newSpinner.setOnItemSelectedListener(listener);
                layout.addView(newSpinner);
            }
        });

        //Button and logic to remove last spinner when remove table1 button is pressed
        FloatingActionButton removeTableButton = findViewById(R.id.removeTableButton);

        removeTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeViewAt(layout.getChildCount() - 1);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView editTime = findViewById(R.id.selectTimeNewReservation);
        editTime.setText(hourOfDay + ":" + minute);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        if (item.getItemId() == android.R.id.home) {
            intent = new Intent(this, ViewReservationMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
            this.finish();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        LinearLayout superParent = (LinearLayout) parent.getParent();
        int childCount = superParent.getChildCount();
        tableSelected = null;
        if (childCount == 1) {
            tableSelected = parent.getItemAtPosition(position).toString();
        } else {
            tableSelected = null;
            for (childCount = superParent.getChildCount(); childCount > 0; childCount--)
                if (tableSelected == null) {
                    parent = (AdapterView<?>) superParent.getChildAt(childCount - 1);
                    tableSelected = parent.getSelectedItem().toString();
                } else {
                    parent = (AdapterView<?>) superParent.getChildAt(childCount - 1);
                    tableSelected += "," + parent.getSelectedItem().toString();
                }
        }
        Log.d(TAG, "onItemSelected: Tables Selected: " + tableSelected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
