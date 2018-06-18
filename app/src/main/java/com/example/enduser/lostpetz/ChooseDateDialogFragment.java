package com.example.enduser.lostpetz;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

/**
 * Created by EndUser on 6/2/2018.
 */

public class ChooseDateDialogFragment extends DialogFragment implements View.OnClickListener{

    private DatePicker mDatePicker;
    private TextView mDoneTextView;
    private TextView mCancelTextView;

    private onClicked mOnClicked;
    public interface onClicked{
        void onClicked(boolean didUserSelectDate);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choose_date, container,false);

        initUI(rootView);
        return rootView;
    }

    /*
    This method initializes Ui elements by the rootView reference
     */
    private void initUI(View rootView){
        mDatePicker = (DatePicker) rootView.findViewById(R.id.choose_date_picker);
        mDoneTextView = (TextView) rootView.findViewById(R.id.choose_date_done_textview);
        mDoneTextView.setOnClickListener(this);
        mCancelTextView = (TextView) rootView.findViewById(R.id.choose_date_cancel_textview);
        mCancelTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.choose_date_done_textview:
                mOnClicked.onClicked(true);
                getDialog().dismiss();
                break;
            case R.id.choose_date_cancel_textview:
                mOnClicked.onClicked(false);
                getDialog().dismiss();
                break;
        }
    }
    /*
    Creates a string in the correct format to return to the caller
    the getMonth() method starts at 0 so we must add one
     */

    public String getDateFromPicker(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mDatePicker.getMonth()+1)
                .append("/")
                .append(mDatePicker.getDayOfMonth())
                .append("/")
                .append(mDatePicker.getYear());
        Log.e("Date string ", stringBuilder.toString());
        return stringBuilder.toString();
    }

    //sets the interface
    public void setOnClicked(onClicked mOnCliked){
        this.mOnClicked = mOnCliked;
    }
}
