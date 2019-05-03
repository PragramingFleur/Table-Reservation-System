package res.takiisushi.tablereservationsystem;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class AddReservationDialog extends AppCompatDialogFragment
        implements AdapterView.OnItemSelectedListener,
        DatePickerDialog.OnDateSetListener {
    private EditText editTextName;
    private EditText editTextNumber;
    private EditText editTextAdultGuestNum;
    private EditText editTextChildGuestNumber;
    private EditText editTextTimeHour;
    private EditText editTextTimeMinute;
    private EditText editTextDate;
    private AddReservationDialogListener listener;
    private Spinner tableDropdown;
    private int tableNum = 0;
    private View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.layout_add_reservation_dialog, null);

        openDialogs(view);

        builder.setView(view);
        builder.setTitle("Add Reservation");
        builder.setNegativeButton("Afbrud", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Reserver", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editTextName.getText().toString();
                int mobileNumber = 00000000;
                try {
                    mobileNumber = Integer.parseInt(editTextNumber.getText().toString());
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
                String guestInformation = editTextAdultGuestNum.getText().toString() + "V + " + editTextChildGuestNumber.getText().toString() + "B";
                String date = editTextDate.getText().toString();
                String time = editTextTimeHour.getText().toString() + ":" + editTextTimeMinute.getText().toString();
                listener.saveReservation(name, mobileNumber, guestInformation, date, time, tableNum);
            }
        });
        editTextName = view.findViewById(R.id.editName);
        editTextNumber = view.findViewById(R.id.editMobileNum);
        editTextAdultGuestNum = view.findViewById(R.id.editAdultGuestNum);
        editTextChildGuestNumber = view.findViewById(R.id.editChildrenGuestNum);
        editTextTimeHour = view.findViewById(R.id.hourSpinner);
        editTextTimeMinute = view.findViewById(R.id.minuteSpinner);
        editTextDate = view.findViewById(R.id.editDate);

        //Table dropdown
        tableDropdown = view.findViewById(R.id.tableSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.table_Numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableDropdown.setAdapter(adapter);
        tableDropdown.setOnItemSelectedListener(this);

        return builder.create();
    }

    private void openDialogs(View v) {
        //'Button' for opening the time picker and date picker for reservation
        EditText reservationDatePicker = v.findViewById(R.id.editDate);

        reservationDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getActivity().getSupportFragmentManager(), "date picker");
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (AddReservationDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AddReservationDialogListener");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            tableNum = Integer.parseInt(parent.getItemAtPosition(position).toString());
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDateSet(DatePicker dView, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());

        TextView dateTextView = view.findViewById(R.id.editDate);
        dateTextView.setText(currentDateString);
    }

    public interface AddReservationDialogListener {
        void saveReservation(String name, int mobileNumber, String guestInformation, String date, String time, int table);
    }
}
