package res.takiisushi.tablereservationsystem;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rajasharan.layout.RearrangeableLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RestaurantLayoutMainActivity extends AppCompatActivity implements TableStatusDialog.TableStatusDialogListener {
    private static final String TAG = "REST-REARRANGEABLE-LOUT";
    private Context mContext;
    String dateToday = "";
    private SQLiteDatabase tableStatusDatabase;
    private SQLiteDatabase reservationDatabase;
    private String childName;
    private long currentDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        //Initializing DB
        TableStatusDBHelper dbHelper = TableStatusDBHelper.getInstance(this);
        tableStatusDatabase = dbHelper.getWritableDatabase();

        //Initializing DB
        ReservationDBHelper dbHelper1 = ReservationDBHelper.getInstance(this);
        reservationDatabase = dbHelper1.getWritableDatabase();

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day == 1) {
            reservationDatabase.execSQL("delete from " + ReservationContract.ReservationEntry.TABLE_NAME);
        }

        getSupportActionBar().setTitle(getString(R.string.table_layout_title));

        setContentView(R.layout.restaurant_main);

        checkAndApplyReservations();

        checkAndApplyTableStatutes();

        final RearrangeableLayout root = findViewById(R.id.restaurant_layout);
        root.setChildPositionListener(new RearrangeableLayout.ChildPositionListener() {
            @Override
            public void onChildMoved(View childView, Rect oldPosition, Rect newPosition) {
                Log.d(TAG, childView.toString());
                Log.d(TAG, oldPosition.toString() + " -> " + newPosition.toString());
                //saveTable(root.getContext(), childView);//Doesn't seem to work
            }
        });

        /*File res = mContext.getFileStreamPath("tables.jpeg");
        if (res != null) {
            getSavedCanvas(); //Doesn't work
        }*/

        for (int counter = 0; counter < root.getChildCount(); counter++) {
            LinearLayout layout = (LinearLayout) root.getChildAt(counter);
            MyTouchListener listener = new MyTouchListener();
            layout.setOnTouchListener(listener);
        }
        //getSavedTables(root);//doesn't work
    }

    public void openTableStatusDialog() {
        Log.d(TAG, "openTableStatusDialog: opened");
        TableStatusDialog dialog = new TableStatusDialog();
        dialog.show(getSupportFragmentManager(), "table status dialog");
    }

    private void checkAndApplyTableStatutes() {
        List<String> statusList = new ArrayList<>();
        List<String> tablesList = new ArrayList<>();

        Cursor cursor = null;

        try {
            cursor = getAllItemsTableStatus();

            while (cursor.moveToNext()) {
                String status = cursor.getString(cursor.getColumnIndex(TableStatusContract.TableStatusEntry.COLUMN_STATUS));

                statusList.add(status);

                String table = cursor.getString(cursor.getColumnIndex(TableStatusContract.TableStatusEntry.COLUMN_TABLENUM));

                tablesList.add(table);
            }
        } catch (Exception ex) {
            // Handle exception
        } finally {
            if (cursor != null) cursor.close();
        }

        for (int counter = 0; counter < tablesList.size(); counter++) {
            if (tablesList.get(counter).equals("Table 1")) {
                LinearLayout linearLayout = findViewById(R.id.table1Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table1");
            }
            if (tablesList.get(counter).equals("Table 2")) {
                LinearLayout linearLayout = findViewById(R.id.table2Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table2");
            }
            if (tablesList.get(counter).equals("Table 3")) {
                LinearLayout linearLayout = findViewById(R.id.table3Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table3");
            }
            if (tablesList.get(counter).equals("Table 4")) {
                LinearLayout linearLayout = findViewById(R.id.table4Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table4");
            }
            if (tablesList.get(counter).equals("Table 5")) {
                LinearLayout linearLayout = findViewById(R.id.table5Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table5");
            }
            if (tablesList.get(counter).equals("Table 6")) {
                LinearLayout linearLayout = findViewById(R.id.table6Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table6");
            }
            if (tablesList.get(counter).equals("Table 7")) {
                LinearLayout linearLayout = findViewById(R.id.table7Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table7");
            }
            if (tablesList.get(counter).equals("Table 8")) {
                LinearLayout linearLayout = findViewById(R.id.table8Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table8");
            }
            if (tablesList.get(counter).equals("Table 9")) {
                LinearLayout linearLayout = findViewById(R.id.table9Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table9");
            }
            if (tablesList.get(counter).equals("Table 10")) {
                LinearLayout linearLayout = findViewById(R.id.table10Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table10");
            }
            if (tablesList.get(counter).equals("Table 11")) {
                LinearLayout linearLayout = findViewById(R.id.table11Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table11");
            }
            if (tablesList.get(counter).equals("Table 12")) {
                LinearLayout linearLayout = findViewById(R.id.table12Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table12");
            }
            if (tablesList.get(counter).equals("Table 13")) {
                LinearLayout linearLayout = findViewById(R.id.table13Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table13");
            }
            if (tablesList.get(counter).equals("Table 14")) {
                LinearLayout linearLayout = findViewById(R.id.table14Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table14");
            }
            if (tablesList.get(counter).equals("Table 15")) {
                LinearLayout linearLayout = findViewById(R.id.table15Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table15");
            }
            if (tablesList.get(counter).equals("Table 16")) {
                LinearLayout linearLayout = findViewById(R.id.table16Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table16");
            }
            if (tablesList.get(counter).equals("Table 17")) {
                LinearLayout linearLayout = findViewById(R.id.table17Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table17");
            }
            if (tablesList.get(counter).equals("Table 18")) {
                LinearLayout linearLayout = findViewById(R.id.table18Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table18");
            }
            if (tablesList.get(counter).equals("Table 19")) {
                LinearLayout linearLayout = findViewById(R.id.table19Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table19");
            }
            if (tablesList.get(counter).equals("Table 20")) {
                LinearLayout linearLayout = findViewById(R.id.table20Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table20");
            }
            if (tablesList.get(counter).equals("Table 21")) {
                LinearLayout linearLayout = findViewById(R.id.table21Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table21");
            }
            if (tablesList.get(counter).equals("Table 22")) {
                LinearLayout linearLayout = findViewById(R.id.table22Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table22");
            }
            if (tablesList.get(counter).equals("Table 23")) {
                LinearLayout linearLayout = findViewById(R.id.table23Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table23");
            }
            if (tablesList.get(counter).equals("Table 24")) {
                LinearLayout linearLayout = findViewById(R.id.table24Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table24");
            }
            if (tablesList.get(counter).equals("Table 25")) {
                LinearLayout linearLayout = findViewById(R.id.table25Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table25");
            }
            if (tablesList.get(counter).equals("Table 26")) {
                LinearLayout linearLayout = findViewById(R.id.table26Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table26");
            }
            if (tablesList.get(counter).equals("Table 27")) {
                LinearLayout linearLayout = findViewById(R.id.table27Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table27");
            }
            if (tablesList.get(counter).equals("Table 28")) {
                LinearLayout linearLayout = findViewById(R.id.table28Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table28");
            }
            if (tablesList.get(counter).equals("Table 29")) {
                LinearLayout linearLayout = findViewById(R.id.table29Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table29");
            }
            if (tablesList.get(counter).equals("Table 30")) {
                LinearLayout linearLayout = findViewById(R.id.table30Linear);
                addTableStatus(linearLayout, statusList.get(counter));
                Log.d(TAG, "checkAndApplyTableStatutes: table30");
            }
        }

    }

    private void addTableStatus(LinearLayout layout, String status) {
        if (status.equals("Taken") || status.equals("Optaget")) {
            layout.getBackground().setColorFilter(getResources().getColor(R.color.colorTaken), PorterDuff.Mode.OVERLAY);
        } else if (status.equals("Available") || status.equals("Ledig")) {
            if (layout.getChildCount() > 1) {
                layout.getBackground().setColorFilter(getResources().getColor(R.color.colorReserved), PorterDuff.Mode.OVERLAY);
            } else {
                layout.getBackground().clearColorFilter();
            }
        } else if (status.equals("Needs Cleaning") || status.equals("Skal Ryddes")) {
            layout.getBackground().setColorFilter(getResources().getColor(R.color.colorCleaning), PorterDuff.Mode.OVERLAY);
        }
    }

    private void checkAndApplyReservations() {
        //getting Todays date
        dateToday = DateFormat.getDateInstance(DateFormat.MEDIUM).format(Calendar.getInstance().getTime());

        String[] columns = new String[]{ReservationContract.ReservationEntry.COLUMN_TABLES, ReservationContract.ReservationEntry.COLUMN_TIME};

        List<String> tableList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        List<String> timeTableList = new ArrayList<>();

        Cursor cursor = null;

        try {
            cursor = getMatchingItemsReservation(columns, dateToday);

            while (cursor.moveToNext()) {
                String tables = cursor.getString(cursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_TABLES));

                tableList.add(tables);
                Log.d(TAG, "checkAndApplyReservations: " + tables);

                String time = cursor.getString(cursor.getColumnIndex(ReservationContract.ReservationEntry.COLUMN_TIME));

                timeList.add(tables);
                Log.d(TAG, "checkAndApplyReservations: " + time);

                timeTableList.add(time + "+" + tables);
            }
        } catch (Exception ex) {
            // Handle exception
        } finally {
            if (cursor != null) cursor.close();
        }

        List<String> timeTablesSeperatedList = new ArrayList<>();

        for (int counter = 0; counter < timeTableList.size(); counter++) {
            List<String> temp = Arrays.asList(timeTableList.get(counter).split("\\+"));
            for (int tempCounter = 1; tempCounter < temp.size(); tempCounter++) {
                if (temp.get(tempCounter).contains(",")) {
                    List<String> tempTables = Arrays.asList(temp.get(tempCounter).split(","));
                    for (int tableCounter = 0; tableCounter < tempTables.size(); tableCounter++) {
                        timeTablesSeperatedList.add(temp.get(0) + "," + tempTables.get(tableCounter));
                    }
                } else {
                    timeTablesSeperatedList.add(temp.get(0) + "," + temp.get(1));
                }
            }
        }

        for (String timeTable : timeTablesSeperatedList) {
            List<String> temp = Arrays.asList(timeTable.split(","));
            Log.d(TAG, "checkAndApplyReservations: " + temp.get(1));
            if (temp.get(1).equals("1")) {
                LinearLayout linearLayout = findViewById(R.id.table1Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table1");
            }
            if (temp.get(1).equals("2")) {
                LinearLayout linearLayout = findViewById(R.id.table2Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table2");
            }
            if (temp.get(1).equals("3")) {
                LinearLayout linearLayout = findViewById(R.id.table3Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table3");
            }
            if (temp.get(1).equals("4")) {
                LinearLayout linearLayout = findViewById(R.id.table4Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table4");
            }
            if (temp.get(1).equals("5")) {
                LinearLayout linearLayout = findViewById(R.id.table5Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table5");
            }
            if (temp.get(1).equals("6")) {
                LinearLayout linearLayout = findViewById(R.id.table6Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table6");
            }
            if (temp.get(1).equals("7")) {
                LinearLayout linearLayout = findViewById(R.id.table7Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table7");
            }
            if (temp.get(1).equals("8")) {
                LinearLayout linearLayout = findViewById(R.id.table8Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table8");
            }
            if (temp.get(1).equals("9")) {
                LinearLayout linearLayout = findViewById(R.id.table9Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table9");
            }
            if (temp.get(1).equals("10")) {
                LinearLayout linearLayout = findViewById(R.id.table10Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table10");
            }
            if (temp.get(1).equals("11")) {
                LinearLayout linearLayout = findViewById(R.id.table11Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table11");
            }
            if (temp.get(1).equals("12")) {
                LinearLayout linearLayout = findViewById(R.id.table12Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table12");
            }
            if (temp.get(1).equals("13")) {
                LinearLayout linearLayout = findViewById(R.id.table13Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table13");
            }
            if (temp.get(1).equals("14")) {
                LinearLayout linearLayout = findViewById(R.id.table14Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table14");
            }
            if (temp.get(1).equals("15")) {
                LinearLayout linearLayout = findViewById(R.id.table15Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table15");
            }
            if (temp.get(1).equals("16")) {
                LinearLayout linearLayout = findViewById(R.id.table16Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table16");
            }
            if (temp.get(1).equals("17")) {
                LinearLayout linearLayout = findViewById(R.id.table17Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table17");
            }
            if (temp.get(1).equals("18")) {
                LinearLayout linearLayout = findViewById(R.id.table18Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table18");
            }
            if (temp.get(1).equals("19")) {
                LinearLayout linearLayout = findViewById(R.id.table19Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table19");
            }
            if (temp.get(1).equals("20")) {
                LinearLayout linearLayout = findViewById(R.id.table20Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table20");
            }
            if (temp.get(1).equals("21")) {
                LinearLayout linearLayout = findViewById(R.id.table21Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table21");
            }
            if (temp.get(1).equals("22")) {
                LinearLayout linearLayout = findViewById(R.id.table22Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table22");
            }
            if (temp.get(1).equals("23")) {
                LinearLayout linearLayout = findViewById(R.id.table23Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table23");
            }
            if (temp.get(1).equals("24")) {
                LinearLayout linearLayout = findViewById(R.id.table24Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table24");
            }
            if (temp.get(1).equals("25")) {
                LinearLayout linearLayout = findViewById(R.id.table25Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table25");
            }
            if (temp.get(1).equals("26")) {
                LinearLayout linearLayout = findViewById(R.id.table26Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table26");
            }
            if (temp.get(1).equals("27")) {
                LinearLayout linearLayout = findViewById(R.id.table27Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table27");
            }
            if (temp.get(1).equals("28")) {
                LinearLayout linearLayout = findViewById(R.id.table28Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table28");
            }
            if (temp.get(1).equals("29")) {
                LinearLayout linearLayout = findViewById(R.id.table29Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table29");
            }
            if (temp.get(1).equals("30")) {
                LinearLayout linearLayout = findViewById(R.id.table30Linear);
                addTimeViewAndReserve(linearLayout, temp.get(0));
                Log.d(TAG, "checkAndApplyReservations: table30");
            }
        }
    }

    private void addTimeViewAndReserve(LinearLayout layout, String time) {
        layout.getBackground().setColorFilter(getResources().getColor(R.color.colorReserved), PorterDuff.Mode.OVERLAY);

        TextView timeView = new TextView(mContext);
        timeView.setText(time);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        timeView.setLayoutParams(layoutParams);
        timeView.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        layout.addView(timeView);
    }

    private Cursor getMatchingItemsReservation(String[] columns, String date) {
        return reservationDatabase.query(
                ReservationContract.ReservationEntry.TABLE_NAME,
                columns,
                ReservationContract.ReservationEntry.COLUMN_DATE + "=?",
                new String[]{date},
                null,
                null,
                ReservationContract.ReservationEntry.COLUMN_TIME + " ASC"
        );
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

    private void saveCanvas() {
        View view = findViewById(R.id.restaurant_layout);
        try {
            FileOutputStream fos = mContext.openFileOutput("tables.jpeg", MODE_PRIVATE);
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Cursor getMatchingItemsTableStatus(String table) {
        return tableStatusDatabase.query(
                TableStatusContract.TableStatusEntry.TABLE_NAME,
                null,
                TableStatusContract.TableStatusEntry.COLUMN_TABLENUM + "=?",
                new String[]{table},
                null,
                null,
                TableStatusContract.TableStatusEntry.COLUMN_TABLENUM + " ASC"
        );
    }

    private Cursor getAllItemsTableStatus() {
        return tableStatusDatabase.query(
                TableStatusContract.TableStatusEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                TableStatusContract.TableStatusEntry.COLUMN_TABLENUM + " ASC"
        );
    }

    @Override
    public void putinDB(String status) {
        ContentValues values = new ContentValues();
        values.put(TableStatusContract.TableStatusEntry.COLUMN_TABLENUM, childName);
        values.put(TableStatusContract.TableStatusEntry.COLUMN_STATUS, status);

        if (!checkIfEntryExistsInDB(childName)) {
            tableStatusDatabase.insert(TableStatusContract.TableStatusEntry.TABLE_NAME, null, values);
        } else {
            tableStatusDatabase.update(TableStatusContract.TableStatusEntry.TABLE_NAME, values, TableStatusContract.TableStatusEntry.COLUMN_TABLENUM + "='" + childName + "'", null);
        }

        checkAndApplyTableStatutes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.toReservationItem:
                intent = new Intent(this, ViewReservationMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
                break;
            case R.id.addReservationItem:
                intent = new Intent(this, AddReservationMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurant_layout_menu, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCanvas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        File res = mContext.getFileStreamPath("tables.jpeg");
        if (res != null) {
            //getSavedCanvas(); //Doesn't work
            Log.d(TAG, "onResume: get saved canvas");
        }
    }

    public class MyTouchListener implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int CLICK_ACTION_THRESHHOLD = 200;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                long lastTouchDown = currentDown;
                currentDown = System.currentTimeMillis();
                if (currentDown - lastTouchDown < CLICK_ACTION_THRESHHOLD) {
                    if (v instanceof LinearLayout) {
                        openTableStatusDialog();
                        TextView child = (TextView) ((LinearLayout) v).getChildAt(0);
                        childName = child.getText().toString();
                    }

                }
                Log.d(TAG, "onTouch: Pressed! " + lastTouchDown);
            }
            return true;
        }
    }

    /*public void getSavedCanvas() {
        View view = findViewById(R.id.restaurant_layout);
        File res = mContext.getFileStreamPath("tables.jpeg");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(res), options);
        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        view.draw(canvas);
    }*/

    /*public void getSavedTables(RearrangeableLayout parent) {
        ArrayList<View> al = new ArrayList<>();
        boolean cont = true;
        try {
            // create an ObjectInputStream for the file we need to read from
            ObjectInputStream ois = new ObjectInputStream(parent.getContext().openFileInput("tables"));

            // read objects from file
            while (cont) {
                View view = null;
                try {
                    view = (View) ois.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (view != null)
                    al.add(view);
                else
                    cont = false;
            }

            if (!al.isEmpty()) {
                View childView;

                for (int count = 0; count <= al.size() - 1; count++) {
                    for (int counter = 0; counter <= parent.getChildCount() - 1; counter++) {
                        childView = parent.getChildAt(counter);

                        //check if view on file is same as child view
                        if (getResources().getResourceEntryName(parent.getChildAt(counter).getId()) == getResources().getResourceEntryName(al.get(count).getId())) {
                            //check layout parameters are same or different
                            if (al.get(count).getLayoutParams() != parent.getChildAt(counter).getLayoutParams()) {
                                parent.removeView(childView);
                                parent.addView(al.get(count));
                                //parent.getChildAt(counter).setLeft(al.get(count).getLeft());
                                //parent.getChildAt(counter).setTop(al.get(count).getTop());
                                //parent.getChildAt(counter).setRight(al.get(count).getRight());
                                //parent.getChildAt(counter).setBottom(al.get(count).getBottom());
                            }
                        }

                    }
                }
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/

    /*public void saveTable(Context context, View viewTable) {
        try {
            // create a new file with an ObjectOutputStream
            FileOutputStream out = context.openFileOutput("tables", MODE_APPEND);
            ObjectOutputStream oout = new ObjectOutputStream(out);

            // write something in the file
            oout.writeObject(viewTable);
            oout.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/
}
