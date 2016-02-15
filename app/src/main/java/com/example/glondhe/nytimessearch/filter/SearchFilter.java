package com.example.glondhe.nytimessearch.filter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.glondhe.nytimessearch.R;

import java.util.Calendar;

public class SearchFilter extends AppCompatActivity
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private String deskValue = "";
    private Boolean cb1 = Boolean.FALSE;
    private Boolean cb2 = Boolean.FALSE;
    private Boolean cb3 = Boolean.FALSE;
    private EditText etBeginDate;
    private Context _context;
    private int _year;
    private int _month;
    private int _day;
    public static StringBuilder date;
    private Button btCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_filter);

        String dateSelected = getIntent().getStringExtra("date");
        int sortOrderPosition = getIntent().getIntExtra("sortOrderPosition", 0);
        String DeskValue = getIntent().getStringExtra("deskValue");
        Boolean cb1 = getIntent().getBooleanExtra("cb1",Boolean.FALSE);
        Boolean cb2 = getIntent().getBooleanExtra("cb2", Boolean.FALSE);
        Boolean cb3 = getIntent().getBooleanExtra("cb3",Boolean.FALSE);

        this.etBeginDate = (EditText) findViewById(R.id.etBeginDate);
        this.btCancel = (Button) findViewById(R.id.btCancel);
        Spinner spSortOrder = (Spinner) findViewById(R.id.spSortOrder);
        final CheckBox cbox1 = (CheckBox) findViewById(R.id.checkbox_1);
        CheckBox cbox2 = (CheckBox) findViewById(R.id.checkbox_2);
        CheckBox cbox3 = (CheckBox) findViewById(R.id.checkbox_3);

        etBeginDate.setText(dateSelected);
        spSortOrder.setSelection(sortOrderPosition);
        Log.e("DEBUG", cb1.toString() + cb2.toString() + cb3.toString());

        cbox1.post(new Runnable() {
            @Override
            public void run() {
                cbox1.setSelected(true);
            }
        });

        cbox2.setSelected(true);
        cbox3.setSelected(true);

        this.etBeginDate.setOnClickListener(this);

    }

    public void onCheckboxClicked1(View view) {

        CheckBox checkBox1 = (CheckBox) view.findViewById(R.id.checkbox_1);

                if (checkBox1.isChecked()) {
                    deskValue += "\"Arts\" ";
                    cb1 = Boolean.TRUE;
                   // Toast.makeText(getApplicationContext(),cb1 + deskValue, Toast.LENGTH_SHORT).show();
                }
    }

    public void onCheckboxClicked2(View view) {

        CheckBox checkBox2 = (CheckBox) view.findViewById(R.id.checkbox_2);

                if (checkBox2.isChecked()) {
                    deskValue += "\"Fashion\" ";
                    cb2 = Boolean.TRUE;
                    //Toast.makeText(getApplicationContext(),cb2 + deskValue, Toast.LENGTH_SHORT).show();
                }
    }

    public void onCheckboxClicked3(View view) {

        CheckBox checkBox3 = (CheckBox) view.findViewById(R.id.checkbox_3);

                if (checkBox3.isChecked()) {
                    deskValue += "\"Sports\" ";
                    cb3 = Boolean.TRUE;
                   // Toast.makeText(getApplicationContext(),cb3 + deskValue, Toast.LENGTH_SHORT).show();

                }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _year = year;
        _month = monthOfYear;
        _day = dayOfMonth;
        updateDisplay();
    }

    private void updateDisplay() {

       this.date = new StringBuilder()
                .append(_year).append("/");

        if (_month < 10 ) this.date.append(0).append(_month + 1).append("/");
        else this.date.append(_month + 1).append("/");

        if (_day < 10 ) this.date.append(0).append(_day);
        else this.date.append(_day + 1);

        etBeginDate.setText(this.date);
    }

    @Override
    public void onClick(View v) {
        final Calendar calendar =  Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);

        Log.d("DEBUG",year+ "/"+month+ "/"+date);

        DatePickerDialog dp = new DatePickerDialog(this,this,year,month,date);
        dp.show();
    }

    public void clickSaveButton(View view){

        Spinner spSortOrder = (Spinner) findViewById(R.id.spSortOrder);

        Intent data = new Intent();
        data.putExtra("sortOrder", spSortOrder.getSelectedItem().toString());
        data.putExtra("sortOrderPosition", spSortOrder.getSelectedItemPosition());
        data.putExtra("deskValue", deskValue);
        data.putExtra("cb1", cb1);
        data.putExtra("cb2", cb2);
        data.putExtra("cb3", cb3);
        data.putExtra("date", date.toString());
        data.putExtra("cancel","false");
        setResult(RESULT_OK, data);
        finish();
    }

    public void clickCancelButton(View view){

        Spinner spSortOrder = (Spinner) findViewById(R.id.spSortOrder);
        Intent data = new Intent();
        data.putExtra("sortOrder", spSortOrder.getSelectedItem().toString());
        data.putExtra("sortOrderPosition", spSortOrder.getSelectedItemPosition());
        data.putExtra("deskValue", "");
        data.putExtra("cb1", "");
        data.putExtra("cb2", "");
        data.putExtra("cb3", "");
        data.putExtra("date", "");
        data.putExtra("cancel", "true");
        setResult(RESULT_OK, data);

    }
}
