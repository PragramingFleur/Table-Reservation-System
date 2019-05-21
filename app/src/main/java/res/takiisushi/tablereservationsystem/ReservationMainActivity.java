package res.takiisushi.tablereservationsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class ReservationMainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener,
        AddReservationDialog.AddReservationDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_main);

        setupActionBar();

        //Buttons and methods to open the datepicker dialogs to choose a date to view of reservations
        FloatingActionButton datePickerButtonA = findViewById(R.id.calendarButton);
        TextView datePickerButtonB = findViewById(R.id.dateViewer);

        datePickerButtonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker a");
            }
        });

        datePickerButtonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker b");
            }
        });
    }

    private void setupActionBar() {
        getSupportActionBar().setTitle(getString(R.string.reservation_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());

        TextView dateTextView = findViewById(R.id.dateViewer);
        dateTextView.setText(currentDateString);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.addReservationItem:
                intent = new Intent(this, AddReservationMainActivity.class);
                this.startActivity(intent);
                this.finish();
                break;
            case R.id.home:
                intent = new Intent(this, RestaurantLayoutMainActivity.class);
                this.startActivity(intent);
                this.finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reservation_layout_menu, menu);
        return true;
    }

    @Override
    public void saveReservation(String name, int mobileNumber, String guestInformation, String datetime, int table) {
        Log.d("saveReservation", "saveReservation: " +
                "TODO: Save " + name + " " + mobileNumber
                + " " + guestInformation
                + " " + datetime
                + " " + table
                + " to DB");
    }
}
