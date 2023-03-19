package com.omar.facerecognitionapps.customs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.helpers.OnSaveDate;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class DatePickerDialog extends DialogFragment {
    private final OnSaveDate onSaveDate;

    public DatePickerDialog(OnSaveDate onSaveDate) {
        this.onSaveDate = onSaveDate;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date_picker_dialog,container,false);
        DatePicker calendar = view.findViewById(R.id.calendar_picker);
        view.findViewById(R.id.mb_save_date_picker).setOnClickListener(item->{
            //onSaveDate.save(calendar.getYear(), calendar.getMonth(),calendar.getDayOfMonth());
        });
    return view;
    }


}
