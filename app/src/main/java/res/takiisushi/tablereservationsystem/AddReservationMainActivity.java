package res.takiisushi.tablereservationsystem;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddReservationMainActivity extends AppCompatActivity
        implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener,
        AdapterView.OnItemSelectedListener {
    private static final String TAG = "ADD-RESERVATION";
    String tableSelected;
    int spinnerId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation_main);
        final LinearLayout root = findViewById(R.id.spinnerLayout);

        setupActionBar();

        setupDialogs();

        createDeleteTableSpinner(root);
    }

    private void setupActionBar() {
        getSupportActionBar().setTitle(getString(R.string.add_reservation_title));
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
        //Button and logic to add new spinner when add new table button is pressed
        FloatingActionButton addTableButton = findViewById(R.id.addTableButton);

        addTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner newSpinner = new Spinner(AddReservationMainActivity.this);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AddReservationMainActivity.this, R.array.table_Numbers, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                newSpinner.setAdapter(adapter);
                newSpinner.setId(spinnerId);
                spinnerId += 1;
                newSpinner.setOnItemSelectedListener(listener);
                layout.addView(newSpinner);
            }
        });

        //Button and logic to remove last spinner when remove table button is pressed
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
        TextView editDate = findViewById(R.id.selectDateNewReservation);
        editDate.setText(year + "-" + month + "-" + dayOfMonth);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView editTime = findViewById(R.id.selectTimeNewReservation);
        editTime.setText(hourOfDay + ":" + minute);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, ViewReservationMainActivity.class);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LinearLayout superParent = (LinearLayout) parent.getParent();
        int childCount = superParent.getChildCount();
        tableSelected = null;
        if (childCount == 1) {
            tableSelected = parent.getItemAtPosition(position).toString();
        } else {
            tableSelected = null;
            for (childCount = superParent.getChildCount(); childCount > 0; childCount--) {
                if (tableSelected == null) {
                    parent = (AdapterView<?>) superParent.getChildAt(childCount - 1);
                    tableSelected = parent.getSelectedItem().toString();
                } else {
                    parent = (AdapterView<?>) superParent.getChildAt(childCount - 1);
                    tableSelected += "," + parent.getSelectedItem().toString();
                }
            }
        }
        Log.d(TAG, "onItemSelected: Tables Selected: " + tableSelected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
