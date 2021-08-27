package com.analizadoraire.windKind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class RegistrarResJefe extends AppCompatActivity {

    EditText name, dir, suc, tel, codSen, ubSen;
    Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_res_jefe);

        name = (EditText) findViewById(R.id.et_nombreRegJefe);
        dir = (EditText) findViewById(R.id.et_direccionRegJefe);
        suc = (EditText) findViewById(R.id.et_sucursalRegJefe);
        tel = (EditText) findViewById(R.id.et_telRegJefe);
        codSen = (EditText) findViewById(R.id.et_codSensor);
        ubSen = (EditText) findViewById(R.id.et_ubSensor);

        btn_save = (Button) findViewById(R.id.btn_guardarRegJefe);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean createFlag = createRestaurant();
            }
        });
    }

    private boolean createRestaurant() {
        final boolean[] ret = {false, false};
        // Add a new document with a generated id.
        Map<String, Object> data = new HashMap<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference df = FirebaseFirestore.getInstance().collection("usuarios").document(currentUser.getUid());
        data.put("jefe", df);
        Log.println(Log.DEBUG, "#######################", "Jefe all good");

        String sUS = ubSen.getText().toString();
        if (sUS.equals("")){
            Log.println(Log.DEBUG, "#######################", "US fail");
            Toast.makeText(RegistrarResJefe.this, "Ingrese todos los datos", Toast.LENGTH_LONG).show();
            return false;
        }

        String sCS = codSen.getText().toString();
        if (!sCS.equals("")){
            data.put("sensor", sCS);
            Log.println(Log.DEBUG, "#######################", "CS all good");
            FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
            Query sq = fbdb.getReference().orderByChild("regCode").equalTo(sCS);
            sq.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    int counter = 0;
                    for (DataSnapshot innerSnap: snapshot.getChildren()) {
                        Log.println(Log.DEBUG, "#######################", "Sensor existe");
                        boolean ir = (boolean) innerSnap.child("isRegistered").getValue();
                        if (!ir) {
                            Log.println(Log.DEBUG, "#######################", "Sensor no registrado");
                            innerSnap.getRef().child("isRegistered").setValue(true);
                            innerSnap.getRef().getRef().child("ubicacion").setValue(sUS);
                            counter++;
                        } else {
                            Log.println(Log.DEBUG, "#######################", "Sensor registrado");
                            ret[0] = false;
                            ret[1] = true;
                        }
                    }
                    if (counter < 1) {
                        Log.println(Log.DEBUG, "#######################", "Sensor no existe");
                        ret[0] = false;
                        ret[1] = true;
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(RegistrarResJefe.this, "Algo salió mal al registrar, transacción cancelada, intente otra vez", Toast.LENGTH_LONG).show();
                    ret[0] = false;
                    ret[1] = true;
                }
            });
        } else {
            Toast.makeText(RegistrarResJefe.this, "Ingrese todos los datos", Toast.LENGTH_LONG).show();
            return false;
        }

        String sDir = dir.getText().toString();
        if (!sDir.equals("")){
            Toast.makeText(RegistrarResJefe.this, "Ingrese todos los datos", Toast.LENGTH_LONG).show();
            Log.println(Log.DEBUG, "#######################", "Dir all good");
            data.put("direccion", sDir);
        } else {
            return false;
        }

        String sNom = name.getText().toString();
        if (!sNom.equals("")){
            Toast.makeText(RegistrarResJefe.this, "Ingrese todos los datos", Toast.LENGTH_LONG).show();
            Log.println(Log.DEBUG, "#######################", "Name all good");
            data.put("nombre", sNom);
        } else {
            return false;
        }

        String sSuc = suc.getText().toString();
        if (!sSuc.equals("")){
            Toast.makeText(RegistrarResJefe.this, "Ingrese todos los datos", Toast.LENGTH_LONG).show();
            Log.println(Log.DEBUG, "#######################", "Suc all good");
            int iSuc = (int) Integer.parseInt(sSuc);
            data.put("numSucursal", iSuc);
        } else {
            return false;
        }

        String sTel = tel.getText().toString();
        if (!sTel.equals("")){
            Toast.makeText(RegistrarResJefe.this, "Ingrese todos los datos", Toast.LENGTH_LONG).show();
            Log.println(Log.DEBUG, "#######################", "Tel all good");
            data.put("telefono", sTel);
        } else {
            return false;
        }

        if (ret[1]) {
            Toast.makeText(RegistrarResJefe.this, "Ingrese todos los datos", Toast.LENGTH_LONG).show();
            Log.println(Log.DEBUG, "#######################", "If ret[1]");
            return false;
        }

        FirebaseFirestore.setLoggingEnabled(true);
        FirebaseFirestore.getInstance().collection("restaurante")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.println(Log.DEBUG, "#######################", "Success creacion firestore" + documentReference.toString());
                        String fh = String.valueOf(Math.abs(documentReference.getId().hashCode()));
                        String th = fh.substring(0, Math.min(fh.length(), 8));
                        documentReference.update("regCode", th);
                        df.update("restaurantes", FieldValue.arrayUnion(documentReference));
                        ret[0] = true;
                        ret[1] = true;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(RegistrarResJefe.this, RestaurantesParaJefes.class);
                                startActivity(intent);
                            }
                        }, 1000);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrarResJefe.this, "Algo salió mal al registrar, intente otra vez", Toast.LENGTH_LONG).show();
                        Log.println(Log.DEBUG, "#######################", "Falla creacion firestore" + e.toString());
                        ret[0] = false;
                        ret[1] = true;
                    }
                });

        return ret[0];
    }
}