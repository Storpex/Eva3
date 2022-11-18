package com.example.project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class EstadisticaFragment extends Fragment {


    FirebaseDatabase database;

    TextView EstadTempVecesUsados, EstadCalVecesUsados, EstadOpenVecesUsados, Est_hrs, Est_min, Est_seg;

    public void ConteoHaciaAtras(long MilisegundosTotal) {
        String tiempo = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(MilisegundosTotal),
                TimeUnit.MILLISECONDS.toMinutes(MilisegundosTotal) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(MilisegundosTotal)),
                TimeUnit.MILLISECONDS.toSeconds(MilisegundosTotal) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(MilisegundosTotal))
        );
        final String[] hourMinSec = tiempo.split(":");
        Est_hrs.setText(hourMinSec[0]);
        Est_min.setText(hourMinSec[1]);
        Est_seg.setText(hourMinSec[2]);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Temporizador Contador
        /*getParentFragmentManager().setFragmentResultListener("Temporizador", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String countTemp = result.getString("countTemp");
                EstadTempVecesUsados.setText(String.format(Locale.getDefault(), "- Veces usado: %s", countTemp));

                String hrsTemp = result.getString("hrsTemp");
                ConteoHaciaAtras(Long.parseLong(hrsTemp));
            }
        });*/

        // Calendar Contador
        /*getParentFragmentManager().setFragmentResultListener("Calendar", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String countCal = result.getString("countCal");
                EstadCalVecesUsados.setText(String.format(Locale.getDefault(), "- Veces usado: %s", countCal));
            }
        });*/

        // Abrir Caja Contador
        /*getParentFragmentManager().setFragmentResultListener("OpenCase", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String countOpen = result.getString("countOpen");
                EstadOpenVecesUsados.setText(String.format(Locale.getDefault(), "- Veces usado: %s", countOpen));
            }
        });*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_estadistica, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EstadTempVecesUsados = view.findViewById(R.id.EstadTempVecesUsados);
        Est_hrs = view.findViewById(R.id.Est_hrs);
        Est_min= view.findViewById(R.id.Est_min);
        Est_seg= view.findViewById(R.id.Est_seg);

        EstadCalVecesUsados = view.findViewById(R.id.EstadCalVecesUsados);
        EstadOpenVecesUsados = view.findViewById(R.id.EstadOpenVecesUsados);


        // FIREBASE
        database = FirebaseDatabase.getInstance();
        DatabaseReference tempRef2 = database.getReference();

        tempRef2.child("Temporizador").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String hrs = snapshot.child("hrsTemp").getValue().toString();
                    long valorhrs = Integer.parseInt(hrs);
                    ConteoHaciaAtras(Long.parseLong(hrs));

                    String contTe = snapshot.child("countTemp").getValue().toString();
                    EstadTempVecesUsados.setText(String.format(Locale.getDefault(), "- Veces usado: %s", contTe));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        tempRef2.child("Calendario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String contCa = snapshot.child("countCal").getValue().toString();
                    EstadCalVecesUsados.setText(String.format(Locale.getDefault(), "- Veces usado: %s", contCa));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        tempRef2.child("Contrasenia").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String contOp = snapshot.child("countOpen").getValue().toString();
                    EstadOpenVecesUsados.setText(String.format(Locale.getDefault(), "- Veces usado: %s", contOp));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}