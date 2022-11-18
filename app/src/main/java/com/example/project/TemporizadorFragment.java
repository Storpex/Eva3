package com.example.project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TemporizadorFragment extends Fragment {

    FirebaseDatabase database;

    // ID's del xml.
    TextView text_hrs, text_min, text_seg;
    NumberPicker numPickHrs, numPickMin, numPickSeg;
    AppCompatButton btn_start;

    // Variables inicializadas.
    private long durHour;
    private long durMinute;
    private long durSecond;
    private long durTotal;
    private long sum;
    private int sum4;
    private long totalBundle=0;
    private int counterTemporizer;
    public boolean timerRunning = true;
    private final long milisegundos = 1000;
    boolean verdad = false;


    // Function
    public void ConteoHaciaAtras(long MilisegundosTotal) {
        String tiempo = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(MilisegundosTotal),
                TimeUnit.MILLISECONDS.toMinutes(MilisegundosTotal) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(MilisegundosTotal)),
                TimeUnit.MILLISECONDS.toSeconds(MilisegundosTotal) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(MilisegundosTotal))
        );
        final String[] hourMinSec = tiempo.split(":");
        text_hrs.setText(hourMinSec[0]);
        text_min.setText(hourMinSec[1]);
        text_seg.setText(hourMinSec[2]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_temporizador, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Todos los findViewById.
        btn_start = view.findViewById(R.id.btn_start);

        // **** 00:00:00
        text_hrs = view.findViewById(R.id.text_hrs);
        text_min = view.findViewById(R.id.text_min);
        text_seg = view.findViewById(R.id.text_seg);

        // **** Temporizador
        numPickHrs = view.findViewById(R.id.numPickHrs);
        numPickMin = view.findViewById(R.id.numPickMin);
        numPickSeg = view.findViewById(R.id.numPickSeg);

        // Hours
        numPickHrs.setMinValue(0);
        numPickHrs.setMaxValue(23);

        // Hours
        numPickMin.setMinValue(0);
        numPickMin.setMaxValue(59);

        // Hours
        numPickSeg.setMinValue(0);
        numPickSeg.setMaxValue(59);

        // Variables
        //counterTemporizer = 0;

        // FIREBASE
        database = FirebaseDatabase.getInstance();
        DatabaseReference tempRef2 = database.getReference();

        tempRef2.child("Temporizador").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    tempRef2.child("Temporizador").child("hrsTemp").setValue(0);
                    tempRef2.child("Temporizador").child("countTemp").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        btn_start.setOnClickListener( vista -> {
            // ERROR, Por si alguna razon puede presionar el boton, saltará este mensaje.
            // if (timerRunning) Toast.makeText(MainActivity.this, "El temporizador ya esta en uso.", Toast.LENGTH_SHORT).show();

            // Cambio de valores en variables.
            timerRunning = true;

            // Calculo de hora, minutos, segundos a milisegundos
            durHour = TimeUnit.MILLISECONDS.convert(numPickHrs.getValue(), TimeUnit.HOURS);
            durMinute = TimeUnit.MILLISECONDS.convert(numPickMin.getValue(), TimeUnit.MINUTES);
            durSecond = TimeUnit.MILLISECONDS.convert(numPickSeg.getValue(), TimeUnit.SECONDS);
            durTotal = durHour + durMinute + durSecond;

            // Contador
            //counterTemporizer += 1;
            // counterBtnTemporizer.setText(String.format(Locale.getDefault(), "%d", counterTemporizer));

            // Inhabilitar cambio de tiempo y el boton.
            btn_start.setEnabled(false);
            numPickHrs.setEnabled(false);
            numPickMin.setEnabled(false);
            numPickSeg.setEnabled(false);

            // BUNDLE
            /*
            totalBundle += durTotal;
            Bundle tempBundle = new Bundle();
            tempBundle.putString("countTemp", String.valueOf(counterTemporizer));
            tempBundle. putString("hrsTemp", String.valueOf(totalBundle));
            getParentFragmentManager().setFragmentResult("Temporizador", tempBundle);*/


            // FIREBASE
            tempRef2.child("Temporizador").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String hrs = snapshot.child("hrsTemp").getValue().toString();
                        int valorhrs = Integer.parseInt(hrs);
                        valorhrs += durTotal;

                        String cont = snapshot.child("countTemp").getValue().toString();
                        int valorcont = Integer.parseInt(cont);
                        valorcont += 1;

                        if(verdad) {
                            tempRef2.child("Temporizador").child("hrsTemp").setValue(valorhrs);
                            tempRef2.child("Temporizador").child("countTemp").setValue(valorcont);
                        }
                        verdad = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
            verdad=true;


            // Contador.
            new CountDownTimer( durTotal, milisegundos) {
                @Override
                public void onTick(long millisUntilFinished) {
                    requireActivity().runOnUiThread(() -> ConteoHaciaAtras(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    // Reinicio de los valores de las variables.
                    durTotal = 0;
                    timerRunning = false;
                    ConteoHaciaAtras(0);

                    // Habilitar cambio de tiempo y el boton.
                    btn_start.setEnabled(true);
                    numPickHrs.setEnabled(true);
                    numPickMin.setEnabled(true);
                    numPickSeg.setEnabled(true);
                }
            }.start();
        });
    }
}