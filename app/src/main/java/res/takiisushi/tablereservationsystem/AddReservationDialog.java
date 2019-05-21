package res.takiisushi.tablereservationsystem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddReservationDialog extends AppCompatDialogFragment
        implements AdapterView.OnItemSelectedListener {

    private EditText editTextName;
    private EditText editTextNumber;
    private EditText editTextAdultGuestNum;
    private EditText editTextChildGuestNumber;
    private EditText spinnerTimeHour;
    private EditText spinnerTimeMinute;
    private EditText editTextDate;
    private AddReservationDialogListener listener;
    private Spinner hourDropdown;
    private Spinner minuteDropdown;
    private Spinner tableDropdown;
    private int tableNum = 0;
    private int hour = 13;
    private int minute = 00;
    private View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.layout_add_reservation_dialog, null);

        builder.setView(view);
        builder.setTitle(getString(R.string.reservation_title));
        builder.setNegativeButton(getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton(getString(R.string.reserve_text), new DialogInterface.OnClickListener() {
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
                String datetime = String.format(editTextDate.getText().toString() + " " + hour + ":%tM"), minute;

                listener.saveReservation(name, mobileNumber, guestInformation, datetime, tableNum);
            }
        });
        editTextName = view.findViewById(R.id.editName);
        editTextNumber = view.findViewById(R.id.editMobileNum);
        editTextAdultGuestNum = view.findViewById(R.id.editAdultGuestNum);
        editTextChildGuestNumber = view.findViewById(R.id.editChildrenGuestNum);
        editTextDate = view.findViewById(R.id.editDate);

        //populating Hour dropdown
        hourDropdown = view.findViewById(R.id.hourSpinner);
        ArrayAdapter<CharSequence> adapterHour = ArrayAdapter.createFromResource(this.getContext(), R.array.hours, android.R.layout.simple_spinner_item);
        adapterHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourDropdown.setAdapter(adapterHour);
        hourDropdown.setOnItemSelectedListener(this);

        //populating minute dropdown
        minuteDropdown = view.findViewById(R.id.minuteSpinner);
        ArrayAdapter<CharSequence> adapterMinute = ArrayAdapter.createFromResource(this.getContext(), R.array.minutes, android.R.layout.simple_spinner_item);
        adapterMinute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minuteDropdown.setAdapter(adapterMinute);
        minuteDropdown.setOnItemSelectedListener(this);

        //populating Table dropdown
        tableDropdown = view.findViewById(R.id.tableSpinner);
        ArrayAdapter<CharSequence> adapterTable = ArrayAdapter.createFromResource(this.getContext(), R.array.table_Numbers, android.R.layout.simple_spinner_item);
        adapterTable.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableDropdown.setAdapter(adapterTable);
        tableDropdown.setOnItemSelectedListener(this);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (AddReservationDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddReservationDialogListener");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            switch (parent.getId()) {
                case R.id.tableSpinner:
                    tableNum = Integer.parseInt(parent.getItemAtPosition(position).toString());
                    break;
                case R.id.hourSpinner:
                    hour = Integer.parseInt(parent.getItemAtPosition(position).toString());
                    break;
                case R.id.minuteSpinner:
                    minute = Integer.parseInt(parent.getItemAtPosition(position).toString());
                    break;
                default:
                    Toast.makeText(parent.getContext(), "nothing happened", Toast.LENGTH_SHORT);
                    break;
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface AddReservationDialogListener {
        void saveReservation(String name, int mobileNumber, String guestInformation, String datetime, int table);
    }
}
