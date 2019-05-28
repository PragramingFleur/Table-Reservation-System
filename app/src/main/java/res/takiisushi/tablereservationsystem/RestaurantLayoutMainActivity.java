package res.takiisushi.tablereservationsystem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.rajasharan.layout.RearrangeableLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class RestaurantLayoutMainActivity extends AppCompatActivity {
    private static final String TAG = "REST-REARRANGEABLE-LOUT";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        getSupportActionBar().setTitle(getString(R.string.table_layout_title));

        setContentView(R.layout.restaurant_main);
        File res = mContext.getFileStreamPath("tables.jpeg");

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
