package res.takiisushi.tablereservationsystem;

import android.app.DatePickerDialog;
import android.content.ContentValues;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ViewReservationMainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener,
        ReservationDetailsDialog.ReservationDetailsDialogListener {
    private static final String TAG = "VIEW-RESERVATION";
    Context mContext;
    Cursor mCursor;
    int itemPosition;
    TextView dateTextView;
    RecyclerView recyclerView;
    private SQLiteDatabase database;
    private SQLiteDatabase tableStatusDB;
    private ReservationAdapter adapter;
    String dateToday = "";
    private Bundle bundle;

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

        //getting table status db
        TableStatusDBHelper dbHelperTable = TableStatusDBHelper.getInstance(mContext);
        tableStatusDB = dbHelperTable.getWritableDatabase();


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

    private void openReservationDetailsDialog(int position, View view) {
        bundle = new Bundle();
        mCursor = getMatchingItems(ReservationContract.ReservationEntry._ID, dateToday, String.valueOf(view.getTag()));
        mCursor.moveToPosition(position);
        bundle.putLong("ID", mCursor.getLong(mCursor.getColumnIndex(ReservationContract.ReservationEntry._ID)));
        bundle.putString("NAME", mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_NAME)));
        bundle.putString("NUMBER", mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_NUMBER)));
        bundle.putString("DATE", mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_DATE)));
        bundle.putString("TIME", mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_TIME)));
        List<String> guests = Arrays.asList(mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_GUESTS)).split("\\s+"));
        bundle.putString("ADULTS", guests.get(1));
        bundle.putString("CHILDREN", guests.get(3));
        bundle.putString("TABLES", mCursor.getString(mCursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_TABLES)));

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
        adapter = ReservationAdapter.getAdapter(mContext, getMatchingItems(ReservationContract.ReservationEntry.COLUMN_DATE, dateToday));
        adapter.swapCursor(getMatchingItems(ReservationContract.ReservationEntry.COLUMN_DATE, dateToday));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ReservationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                openReservationDetailsDialog(position, view);
                itemPosition = position;
            }
        });

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

    private Cursor getMatchingItems(String column, String text) {
        return database.query(
                ReservationContract.ReservationEntry.TABLE_NAME,
                null,
                column + "=?",
                new String[]{text},
                null,
                null,
                ReservationContract.ReservationEntry.COLUMN_TIME + " ASC"
        );
    }

    private Cursor getMatchingItems(String column, String date, String text) {
        if (column.equals(ReservationContract.ReservationEntry._ID)) {
            long id = Long.parseLong(text);
            return database.query(
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

    private void setupActionBar() {
        getSupportActionBar().setTitle(getString(R.string.reservation_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void removeItem(long id) {
        database.delete(ReservationContract.ReservationEntry.TABLE_NAME,
                ReservationContract.ReservationEntry._ID + "=" + id, null);
        adapter.swapCursor(getMatchingItems(ReservationContract.ReservationEntry.COLUMN_DATE, dateTextView.getText().toString()));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());

        dateTextView.setText(currentDateString);

        adapter.swapCursor(getMatchingItems(ReservationContract.ReservationEntry.COLUMN_DATE, currentDateString));
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

    @Override
    public void updateTableStatus(String status, String tableNum, long id) {
        String[] tables = tableNum.split(",");
        ContentValues values = new ContentValues();
        for (String table : tables) {
            values.put(TableStatusContract.TableStatusEntry.COLUMN_TABLENUM, "Table " + table);
            values.put(TableStatusContract.TableStatusEntry.COLUMN_STATUS, status);

            if (!checkIfEntryExistsInDB("Table " + table)) {
                tableStatusDB.insert(TableStatusContract.TableStatusEntry.TABLE_NAME, null, values);
            } else {
                tableStatusDB.update(TableStatusContract.TableStatusEntry.TABLE_NAME, values,
                        TableStatusContract.TableStatusEntry.COLUMN_TABLENUM + "='Table " + table + "'",
                        null);
            }

            database.delete(ReservationContract.ReservationEntry.TABLE_NAME,
                    ReservationContract.ReservationEntry._ID + "=" + id,
                    null);
        }

        Intent intent = new Intent(mContext, RestaurantLayoutMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        this.finish();
    }

    private boolean checkIfEntryExistsInDB(String tableSelected) {
        Cursor cursor = getMatchingItemsTableStatus(tableSelected);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    private Cursor getMatchingItemsTableStatus(String table) {
        return tableStatusDB.query(
                TableStatusContract.TableStatusEntry.TABLE_NAME,
                null,
                TableStatusContract.TableStatusEntry.COLUMN_TABLENUM + "=?",
                new String[]{table},
                null,
                null,
                TableStatusContract.TableStatusEntry.COLUMN_TABLENUM + " ASC"
        );
    }

    @Override
    public void changeReservationDetails(long id) {
        Intent intent = new Intent(mContext, EditReservationMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("id", bundle.getLong("ID"));
        this.startActivity(intent);
        this.finish();
    }
}
