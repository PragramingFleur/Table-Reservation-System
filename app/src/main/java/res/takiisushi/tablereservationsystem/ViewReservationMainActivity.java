package res.takiisushi.tablereservationsystem;

import android.app.DatePickerDialog;
import android.content.Context;
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
import android.widget.SearchView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class ViewReservationMainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "VIEW-RESERVATION";
    Context mContext;
    TextView dateTextView;
    RecyclerView recyclerView;
    private SQLiteDatabase database;
    private ReservationAdapter adapter;
    String dateToday = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_main);
        //Initialize some values
        mContext = this;
        dateTextView = findViewById(R.id.dateViewer);

        //Putting todays date into the textview
        dateToday = DateFormat.getDateInstance(DateFormat.MEDIUM).format(Calendar.getInstance().getTime());
        dateTextView.setText(dateToday);

        setUpDBRecyclerView();

        setupActionBar();

        initializeDialogs();

        setUpSearchView();

    }

    public void setUpSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter = ReservationAdapter.getAdapter(mContext, getMatchingItems(ReservationContract.ReservationEntry.COLUMN_NUMBER, dateToday, query));
                adapter.swapCursor(getMatchingItems(ReservationContract.ReservationEntry.COLUMN_NUMBER, dateToday, query));
                recyclerView.setAdapter(adapter);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter = ReservationAdapter.getAdapter(mContext, getMatchingItems(ReservationContract.ReservationEntry.COLUMN_NUMBER, dateToday, newText));
                adapter.swapCursor(getMatchingItems(ReservationContract.ReservationEntry.COLUMN_NUMBER, dateToday, newText));
                recyclerView.setAdapter(adapter);

                return false;
            }
        });
    }

    public void setUpDBRecyclerView() {
        //updating recycler view to view data from DB for today
        //getting recycler view
        recyclerView = findViewById(R.id.reservationRecyclerView);

        //getting database
        ReservationDBHelper dbHelper = ReservationDBHelper.getInstance(mContext);
        database = dbHelper.getWritableDatabase();

        //making sure data will be listed
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        //getting the adaptor instance and then putting adaptor to the the recycler view
        adapter = ReservationAdapter.getAdapter(mContext, getMatchingDateItems(ReservationContract.ReservationEntry.COLUMN_DATE, dateToday));
        adapter.swapCursor(getMatchingDateItems(ReservationContract.ReservationEntry.COLUMN_DATE, dateToday));
        recyclerView.setAdapter(adapter);
    }

    private void initializeDialogs() {
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

    private Cursor getMatchingDateItems(String column, String text) {
        String[] selectionArgs = {};
        return database.query(
                ReservationContract.ReservationEntry.TABLE_NAME,
                null,
                column + "=?",
                selectionArgs = new String[]{text},
                null,
                null,
                ReservationContract.ReservationEntry.COLUMN_TIME + " ASC"
        );
    }

    private Cursor getMatchingItems(String column, String date, String text) {
        String[] selectionArgs = {};
        return database.query(
                ReservationContract.ReservationEntry.TABLE_NAME,
                null,
                ReservationContract.ReservationEntry.COLUMN_DATE + "=? AND " + column + " LIKE ?",
                selectionArgs = new String[]{date, "%" + text + "%"},
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

        adapter.swapCursor(getMatchingDateItems(ReservationContract.ReservationEntry.COLUMN_DATE, currentDateString));
        dateToday = currentDateString;
        recyclerView.setAdapter(adapter);
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
