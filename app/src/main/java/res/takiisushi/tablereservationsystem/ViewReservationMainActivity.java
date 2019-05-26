package res.takiisushi.tablereservationsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class ViewReservationMainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "VIEW-RESERVATION";
    TextView dateTextView;
    private SQLiteDatabase database;
    private ReservationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_main);
        dateTextView = findViewById(R.id.dateViewer);

        RecyclerView recyclerView = findViewById(R.id.reservationRecyclerView);

        ReservationDBHelper dbHelper = ReservationDBHelper.getInstance(this);
        database = dbHelper.getWritableDatabase();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = ReservationAdapter.getAdapter(this, getAllItems());
        recyclerView.setAdapter(adapter);

        String dateToday = DateFormat.getDateInstance(DateFormat.MEDIUM).format(Calendar.getInstance().getTime());
        dateTextView.setText(dateToday);

        setupActionBar();

        //Buttons and methods to open the datepicker dialogs to choose a date to view of reservations
        FloatingActionButton datePickerButton = findViewById(R.id.calendarButton);

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker a");
            }
        });

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker b");
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

        String currentDateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());

        dateTextView.setText(currentDateString);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.addReservationItem:
                intent = new Intent(this, AddReservationMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
                break;
            case android.R.id.home:
                intent = new Intent(this, RestaurantLayoutMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
}
