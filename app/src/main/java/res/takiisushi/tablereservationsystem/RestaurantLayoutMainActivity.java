package res.takiisushi.tablereservationsystem;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.rajasharan.layout.RearrangeableLayout;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class RestaurantLayoutMainActivity extends AppCompatActivity {
    private static final String TAG = "REST-REARRANGEABLE-LOUT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(getString(R.string.table_layout_title));

        setContentView(R.layout.restaurant_main);

        final RearrangeableLayout root = findViewById(R.id.restaurant_layout);
        root.setChildPositionListener(new RearrangeableLayout.ChildPositionListener() {
            @Override
            public void onChildMoved(View childView, Rect oldPosition, Rect newPosition) {
                Log.d(TAG, childView.toString());
                Log.d(TAG, oldPosition.toString() + " -> " + newPosition.toString());
                saveTable(root.getContext(), childView);
            }
        });

        getSavedTables(root);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
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

    public void getSavedTables(RearrangeableLayout parent) {
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
                                /*parent.getChildAt(counter).setLeft(al.get(count).getLeft());
                                parent.getChildAt(counter).setTop(al.get(count).getTop());
                                parent.getChildAt(counter).setRight(al.get(count).getRight());
                                parent.getChildAt(counter).setBottom(al.get(count).getBottom());*/
                            }
                        }

                    }
                }
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
