package com.example.project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class CalendarioFragment extends Fragment {


    FirebaseDatabase database;
    boolean verdad = false;

    // **************************

    private DatePickerDialog datePickerDialog;
    public DatePickerDialog datePickerDialog2;
    private Button dateButtonStart, dateButtonEnd;
    public AppCompatButton btn_start_calendar;
    TextView fechaFin, counterBtnCalendar;
    private int counterCalendar;


    // EL PRIMERO

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, anio, mes, dia) -> {
            mes = mes + 1;
            String date = makeDateString(dia, mes, anio);
            dateButtonStart.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.BUTTON_NEUTRAL;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, anio, mes, dia);
    }

    private String makeDateString(int dia, int mes, int anio){
        return dia + " " + getMothFormat(mes) + " " + anio;
    }

    private String getMothFormat(int mes) {
        if (mes == 1 )
            return "ENE";
        if (mes == 2 )
            return "FEB";
        if (mes == 3 )
            return "MAR";
        if (mes == 4 )
            return "ABR";
        if (mes == 5 )
            return "MAY";
        if (mes == 6 )
            return "JUN";
        if (mes == 7 )
            return "JUL";
        if (mes == 8 )
            return "AGO";
        if (mes == 9 )
            return "SEP";
        if (mes == 10 )
            return "OCT";
        if (mes == 11 )
            return "NOV";
        if (mes == 12 )
            return "DIC";

        // Default
        return "ENE";
    }


    private String getTodaysDate(){
        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        mes = mes + 1;
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(dia, mes , anio);
    }

    // FIN PRIMERO


    private void initDatePicker2(){
        DatePickerDialog.OnDateSetListener dateSetListener2 = (datePicker, anio, mes, dia) -> {
            mes = mes + 1;
            String date = makeDateString(dia, mes, anio);
            dateButtonEnd.setText(date);
            fechaFin.setText(date);

            // FIREBASE
            database = FirebaseDatabase.getInstance();
            DatabaseReference tempRef2 = database.getReference();
            tempRef2.child("Calendario").child("fechaCal").setValue(date);

        };

        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.BUTTON_NEUTRAL;

        datePickerDialog2 = new DatePickerDialog(getActivity(), style, dateSetListener2, anio, mes, dia);
    }

    // **************************

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_calendario, container, false);

        dateButtonStart = root.getRootView().findViewById(R.id.datePickerButton);
        dateButtonEnd = root.getRootView().findViewById(R.id.datePickerButton2);
        btn_start_calendar = root.findViewById(R.id.btn_start_calendar);

        fechaFin = root.findViewById(R.id.fechaFin);

        initDatePicker();
        dateButtonStart.setOnClickListener(view -> datePickerDialog.show());
        dateButtonStart.setText(getTodaysDate());
        initDatePicker2();

        dateButtonEnd.setOnClickListener(view -> datePickerDialog2.show());
        dateButtonEnd.setText(getTodaysDate());
        fechaFin.setText(getTodaysDate());


        counterCalendar = 0;


        // FIREBASE
        database = FirebaseDatabase.getInstance();
        DatabaseReference tempRef2 = database.getReference();

        tempRef2.child("Calendario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    tempRef2.child("Calendario").child("fechaCal").setValue("");
                    tempRef2.child("Calendario").child("countCal").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        btn_start_calendar.setOnClickListener(view -> {
           dateButtonStart.setEnabled(false);
           dateButtonEnd.setEnabled(false);
           // btn_start_calendar.setEnabled(false);
           //counterCalendar += 1;

            // BUNDLE
            /*Bundle calBundle = new Bundle();
            calBundle.putString("countCal", String.valueOf(counterCalendar));
            getParentFragmentManager().setFragmentResult("Calendar", calBundle);*/


            // FIREBASE
            tempRef2.child("Calendario").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String cont = snapshot.child("countCal").getValue().toString();
                        int valorcont = Integer.parseInt(cont);
                        valorcont += 1;

                        if(verdad) {
                            tempRef2.child("Calendario").child("countCal").setValue(valorcont);
                        }
                        verdad = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
            verdad=true;
        });


        return root;
    }
}