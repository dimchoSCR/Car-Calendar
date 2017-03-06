package com.carcalendar.dmdev.carcalendar.dialogs;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


import com.carcalendar.dmdev.carcalendar.AddVehicleCarActivity;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment {

    // Use the current date as the default date in the picker
    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), (AddVehicleCarActivity)getActivity(), year, month, day);
    }



}