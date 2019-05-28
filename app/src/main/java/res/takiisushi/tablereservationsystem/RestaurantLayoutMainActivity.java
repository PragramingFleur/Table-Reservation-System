package res.takiisushi.tablereservationsystem;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.rajasharan.layout.RearrangeableLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RestaurantLayoutMainActivity extends AppCompatActivity {
    private static final String TAG = "REST-REARRANGEABLE-LOUT";
    private Context mContext;
    String dateToday = "";
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        getSupportActionBar().setTitle(getString(R.string.table_layout_title));

        setContentView(R.layout.restaurant_main);
        File res = mContext.getFileStreamPath("tables.jpeg");

        checkAndApplyReservations();

        /*if (res != null){
            getSavedCanvas(); //Doesn't work
        }*/


        /*final RearrangeableLayout root = findViewById(R.id.restaurant_layout);
        root.setChildPositionListener(new RearrangeableLayout.ChildPositionListener() {
            @Override
            public void onChildMoved(View childView, Rect oldPosition, Rect newPosition) {
                Log.d(TAG, childView.toString());
                Log.d(TAG, oldPosition.toString() + " -> " + newPosition.toString());
                //saveTable(root.getContext(), childView);//Doesn't seem to work
            }
        });*/

        //getSavedTables(root);//doesn't work
    }

    private void checkAndApplyReservations() {
        //getting Todays date
        dateToday = DateFormat.getDateInstance(DateFormat.MEDIUM).format(Calendar.getInstance().getTime());
        //getting database
        ReservationDBHelper dbHelper = ReservationDBHelper.getInstance(mContext);
        database = dbHelper.getWritableDatabase();
        String[] columns = new String[]{ReservationContract.ReservationEntry.COLUMN_TABLES, ReservationContract.ReservationEntry.COLUMN_TIME};

        List<String> tableList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        List<String> timeTableList = new ArrayList<>();

        Cursor cursor = null;

        try {
            cursor = getMatchingItems(columns, dateToday);

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
            int tableCount = 30;
            for (int counter = 1; counter <= tableCount; counter++) {
                if (temp.get(1).equals("1")) {
                    FrameLayout view = findViewById(R.id.table1);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("2")) {
                    FrameLayout view = findViewById(R.id.table2);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("3")) {
                    FrameLayout view = findViewById(R.id.table3);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("4")) {
                    FrameLayout view = findViewById(R.id.table4);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("5")) {
                    FrameLayout view = findViewById(R.id.table5);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("6")) {
                    FrameLayout view = findViewById(R.id.table6);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("7")) {
                    FrameLayout view = findViewById(R.id.table7);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("8")) {
                    FrameLayout view = findViewById(R.id.table8);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("9")) {
                    FrameLayout view = findViewById(R.id.table9);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("10")) {
                    FrameLayout view = findViewById(R.id.table10);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("11")) {
                    FrameLayout view = findViewById(R.id.table11);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("12")) {
                    FrameLayout view = findViewById(R.id.table12);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("13")) {
                    FrameLayout view = findViewById(R.id.table13);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("14")) {
                    FrameLayout view = findViewById(R.id.table14);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("15")) {
                    FrameLayout view = findViewById(R.id.table15);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("16")) {
                    FrameLayout view = findViewById(R.id.table16);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("17")) {
                    FrameLayout view = findViewById(R.id.table17);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("18")) {
                    FrameLayout view = findViewById(R.id.table18);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("19")) {
                    FrameLayout view = findViewById(R.id.table19);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("20")) {
                    FrameLayout view = findViewById(R.id.table20);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("21")) {
                    FrameLayout view = findViewById(R.id.table21);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("22")) {
                    FrameLayout view = findViewById(R.id.table22);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("23")) {
                    FrameLayout view = findViewById(R.id.table23);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("24")) {
                    FrameLayout view = findViewById(R.id.table24);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("25")) {
                    FrameLayout view = findViewById(R.id.table25);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("26")) {
                    FrameLayout view = findViewById(R.id.table26);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("27")) {
                    FrameLayout view = findViewById(R.id.table28);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("28")) {
                    FrameLayout view = findViewById(R.id.table29);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("29")) {
                    FrameLayout view = findViewById(R.id.table30);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
                if (temp.get(1).equals("30")) {
                    FrameLayout view = findViewById(R.id.table18);
                    view.setBackgroundColor(getResources().getColor(R.color.colorReserved));
                }
            }
        }
    }

    private Cursor getMatchingItems(String[] columns, String date) {
        String[] selectionArgs = {};
        return database.query(
                ReservationContract.ReservationEntry.TABLE_NAME,
                columns,
                ReservationContract.ReservationEntry.COLUMN_DATE + "=?",
                selectionArgs = new String[]{date},
                null,
                null,
                ReservationContract.ReservationEntry.COLUMN_TIME + " ASC"
        );
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

    public void getSavedCanvas() {
        View view = findViewById(R.id.restaurant_layout);
        File res = mContext.getFileStreamPath("tables.jpeg");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(res), options);
        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        view.draw(canvas);
    }

    public void getSavedTables(RearrangeableLayout parent) {
        /*ArrayList<View> al = new ArrayList<>();
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
        }*/
    }

    public void saveTable(Context context, View viewTable) {
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
    protected void onStop() {
        super.onStop();
        saveCanvas();
    }
}
