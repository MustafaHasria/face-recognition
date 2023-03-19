package com.omar.facerecognitionapps.customs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.helpers.OnSaveDate;
import org.jetbrains.annotations.NotNull;

public class FilterByDateSheet extends BottomSheetDialogFragment {
    private DatePicker datePicker,datePickerTo;
    private MaterialButton mbConfirm;
    private final OnSaveDate onSaveDate;

    public FilterByDateSheet(OnSaveDate onSaveDate) {
        this.onSaveDate = onSaveDate;
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
            view.measure(0, 0);
            behavior.setPeekHeight(view.getMeasuredHeight());
        }
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_by_date_fragment,container,false);
        initViews(view);
        view.findViewById(R.id.cancel_filter).setOnClickListener(item-> getDialog().dismiss());
        datePicker.setMaxDate(System.currentTimeMillis());
        mbConfirm.setOnClickListener(item->{
            onSaveDate.from(datePicker.getYear(), datePicker.getMonth(),datePicker.getDayOfMonth());
            onSaveDate.to(datePickerTo.getYear(), datePickerTo.getMonth(),datePickerTo.getDayOfMonth());
            onSaveDate.complete();
        });
        return view;
    }

    private void initViews(View view) {
        mbConfirm = view.findViewById(R.id.mb_confirm_filter);
        datePicker = view.findViewById(R.id.calendar_picker);
        datePickerTo = view.findViewById(R.id.to_calendar_picker);
    }
}
