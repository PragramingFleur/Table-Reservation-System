package res.takiisushi.tablereservationsystem;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ViewReservationHistoryMainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener,
        ReservationDetailsDialog.ReservationDetailsDialogListener {

    private static final String TAG = "VIEW-RESERVATION";
    Context mContext;
    Cursor mCursor;

    int itemPosition;

    TextView dateTextView;
    RecyclerView recyclerView;
    String dbDate;

    private SQLiteDatabase database;
    private ReservationAdapter adapter;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_main);

        //Initialize some values
        mContext = this;
        dateTextView = findViewById(R.id.dateViewer);

        //Putting todays date into the textview
        Calendar cal = Calendar.getInstance();
        String dateToday = DateFormat.getDateInstance(DateFormat.MEDIUM).format(cal.getTime());
        dateTextView.setText(dateToday);
        dbDate = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);

        RecyclerView recyclerView = setUpDBRecyclerView();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                removeItem((Long) viewHolder.itemView.getTag());
            }

        }).attachToRecyclerView(recyclerView);

        setupActionBar();

        initializeDialogs();

        setUpSearchView();

    }

    private void openReservationDetailsDialog(View view) {
        bundle = new Bundle();
        mCursor = getMatchingItems(ReservationContract.ReservationEntry._ID, dbDate, String.valueOf(view.getTag()));
        mCursor.moveToFirst();
        bundle.putLong("ID", mCursor.getLong(mCursor.getColumnIndex(ReservationContract.ReservationEntry._ID)));
        bundle.putString("NAME", mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_NAME)));
        bundle.putString("NUMBER", mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_NUMBER)));
        bundle.putString("DATE", mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_DATE)));
        bundle.putString("TIME", mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_TIME)));
        List<String> guests = Arrays.asList(mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_GUESTS)).split("\\s+"));
        bundle.putString("ADULTS", guests.get(1));
        bundle.putString("CHILDREN", guests.get(3));
        bundle.putInt("ARRIVED", 1);

        Log.d(TAG, "openReservationDetailsDialog: opened");
        ReservationDetailsDialog dialog = new ReservationDetailsDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "reservation details dialog");
    }

    public void setUpSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter = ReservationAdapter.getAdapter(mContext, getMatchingItems(ReservationContract.ReservationEntry.COLUMN_NUMBER, dbDate, query));
                adapter.swapCursor(getMatchingItems(ReservationContract.ReservationEntry.COLUMN_NUMBER, dbDate, query));
                recyclerView.setAdapter(adapter);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter = ReservationAdapter.getAdapter(mContext, getMatchingItems(ReservationContract.ReservationEntry.COLUMN_NUMBER, dbDate, newText));
                adapter.swapCursor(getMatchingItems(ReservationContract.ReservationEntry.COLUMN_NUMBER, dbDate, newText));
                adapter.swapCursor(getMatchingItems(ReservationContract.ReservationEntry.COLUMN_NUMBER, dbDate, newText));
                recyclerView.setAdapter(adapter);

                return false;
            }
        });
    }

    public RecyclerView setUpDBRecyclerView() {
        //updating recycler view to view data from DB for today
        //getting recycler view
        recyclerView = findViewById(R.id.reservationRecyclerView);

        //getting reservations database
        ReservationDBHelper dbHelper = ReservationDBHelper.getInstance(mContext);
        database = dbHelper.getWritableDatabase();

        //making sure data will be listed
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        //getting the adaptor instance and then putting adaptor to the the recycler view
        adapter = ReservationAdapter.getAdapter(mContext, getTodaysReservationItems(dbDate));
        adapter.swapCursor(getTodaysReservationItems(dbDate));
        adapter.setOnItemClickListener(new ReservationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                openReservationDetailsDialog(view);
                itemPosition = position;
                Log.d(TAG, "onItemClick: Reservation Item with id: " + view.getTag() + " pressed.");
            }
        });

        recyclerView.setAdapter(adapter);

        return recyclerView;
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

    private Cursor getTodaysReservationItems(String date) {
        return database.query(
                ReservationContract.ReservationEntry.TABLE_NAME,
                null,
                ReservationContract.ReservationEntry.COLUMN_DATE + "=? AND " +
                        ReservationContract.ReservationEntry.COLUMN_ARRIVED + "=1",
                new String[]{date},
                null,
                null,
                ReservationContract.ReservationEntry.COLUMN_TIME + " ASC"
        );
    }

    private Cursor getMatchingItems(String column, String date, String text) {
        if (column.equals(ReservationContract.ReservationEntry._ID)) {
            long id = Long.parseLong(text);
            return database.query(
                    //Getting reservation item by id. For viewing the reservation details
                    ReservationContract.ReservationEntry.TABLE_NAME,
                    null,
                    ReservationContract.ReservationEntry.COLUMN_DATE + "=? AND " + column + "=" + id,
                    new String[]{date},
                    null,
                    null,
                    ReservationContract.ReservationEntry.COLUMN_TIME + " ASC"
            );
        } else {
            return database.query(
                    //For searching through mobile numbers
                    ReservationContract.ReservationEntry.TABLE_NAME,
                    null,
                    ReservationContract.ReservationEntry.COLUMN_DATE + "=? AND " + column + " LIKE ?",
                    new String[]{date, "%" + text + "%"},
                    null,
                    null,
                    ReservationContract.ReservationEntry.COLUMN_TIME + " ASC"
            );
        }
    }

    @SuppressLint("NewApi")
    private void setupActionBar() {
        Objects.requireNonNull(
                getSupportActionBar()).setTitle(getString(R.string.reservation_history_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void removeItem(long id) {
        //TODO: Add confirmation for deleting a reservation
        database.delete(ReservationContract.ReservationEntry.TABLE_NAME,
                ReservationContract.ReservationEntry._ID + "=" + id, null);
        adapter.swapCursor(getTodaysReservationItems(dbDate));
        Toast toast = Toast.makeText(mContext, "Item " + id + " deleted!", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());

        dateTextView.setText(currentDateString);

        adapter.swapCursor(getTodaysReservationItems(currentDateString));
        dbDate = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        adapter.swapCursor(getTodaysReservationItems(dbDate));
        Toast toast = Toast.makeText(mContext, "Reservation List Updated!", Toast.LENGTH_SHORT);
        toast.show();
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
    public void changeReservationDetails(long id) {
        Intent intent = new Intent(mContext, EditReservationMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("id", bundle.getLong("ID"));
        intent.putExtra("arrived", 1);
        this.startActivity(intent);
        this.finish();
    }

    @Override
    public void moveToHistory(long id, boolean isArrived) {
        Toast toast = Toast.makeText(mContext, "Can't do that.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
