package res.takiisushi.tablereservationsystem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ReservationDetailsDialog extends AppCompatDialogFragment {
    private ReservationDetailsDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_reservation_details_dialog, null);

        Bundle bundle = getArguments();
        final long id = bundle.getLong("ID");
        String name = bundle.getString("NAME");
        String number = bundle.getString("NUMBER");
        String date = bundle.getString("DATE");
        String time = bundle.getString("TIME");
        String adults = bundle.getString("ADULTS");
        String children = bundle.getString("CHILDREN");
        final String tables = bundle.getString("TABLES");

        TextView idTextView = view.findViewById(R.id.idTextView);
        idTextView.setText(String.valueOf(id));
        TextView nameTextView = view.findViewById(R.id.nameTextView);
        nameTextView.setText(name);
        TextView numberTextView = view.findViewById(R.id.numberTextView);
        numberTextView.setText(number);
        TextView dateTextView = view.findViewById(R.id.dateTextView);
        dateTextView.setText(date);
        TextView timeTextView = view.findViewById(R.id.timeTextView);
        timeTextView.setText(time);
        TextView adultsTextView = view.findViewById(R.id.adultGuestsTextView);
        adultsTextView.setText(adults);
        TextView childrenTextView = view.findViewById(R.id.childGuestsTextView);
        childrenTextView.setText(children);
        TextView tablesTextView = view.findViewById(R.id.tablesTextView);
        tablesTextView.setText(tables);

        builder.setView(view)
                .setTitle(getString(R.string.reservation_details_title))
                .setNegativeButton(getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(getString(R.string.arrived_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String status = "Taken";
                listener.updateTableStatus(status, tables, id);
            }
        }).setNeutralButton(getString(R.string.edit_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.changeReservationDetails(id);
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ReservationDetailsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ReservationDetailsDialogListener");
        }
    }

    public interface ReservationDetailsDialogListener {
        void updateTableStatus(String status, String tableNum, long id);

        void changeReservationDetails(long id);
    }
}
