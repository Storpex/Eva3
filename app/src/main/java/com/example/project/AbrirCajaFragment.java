package com.example.project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Objects;

public class AbrirCajaFragment extends Fragment {

    FirebaseDatabase database;
    boolean verdad = false;

    private int counterPassword;

    TextView PassAleatoria;
    EditText passIngresada;
    AppCompatButton btn_abrir_password;


    public String generarCadena(int longitud){
        String res = "";
        for (int cont=1; cont<=longitud; cont++){
            int num = (int) ((Math.random() * (('Z'-'A')+1))+'A');
            char letra = (char) num;
            res = res + letra;
        }
        return res;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_abrir_caja, container, false);

        String StringAleatorio = generarCadena(6);

        PassAleatoria = root.getRootView().findViewById(R.id.PassAleatoria);
        btn_abrir_password = root.findViewById(R.id.btn_abrir_password);
        passIngresada = root.findViewById(R.id.passIngresada);

        PassAleatoria.setText(StringAleatorio);

        counterPassword = 0;

        // FIREBASE
        database = FirebaseDatabase.getInstance();
        DatabaseReference tempRef2 = database.getReference();

        tempRef2.child("Contrasenia").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    tempRef2.child("Contrasenia").child("countOpen").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        btn_abrir_password.setOnClickListener(view -> {
            if (Objects.equals(StringAleatorio, passIngresada.getText().toString())) {
                Toast.makeText(root.getContext(),"Contraseña ingresada correctamente.", Toast.LENGTH_SHORT).show();

                // BUNDLE
                /*counterPassword += 1;
                Bundle openBundle = new Bundle();
                openBundle.putString("countOpen", String.valueOf(counterPassword));
                getParentFragmentManager().setFragmentResult("OpenCase", openBundle);*/

                // FIREBASE
                tempRef2.child("Contrasenia").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String cont = snapshot.child("countOpen").getValue().toString();
                            int valorcont = Integer.parseInt(cont);
                            valorcont += 1;

                            if(verdad) {
                                tempRef2.child("Contrasenia").child("countOpen").setValue(valorcont);
                            }
                            verdad = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
                verdad=true;

            } else {
                Toast.makeText(root.getContext(), "Contraseña ingresada incorrectamente.", Toast.LENGTH_SHORT).show();
            }
        });


        return root;
    }
}