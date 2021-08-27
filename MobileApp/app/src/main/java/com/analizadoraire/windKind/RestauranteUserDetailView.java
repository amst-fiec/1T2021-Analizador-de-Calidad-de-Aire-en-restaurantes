package com.analizadoraire.windKind;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

import static android.content.ContentValues.TAG;

public class RestauranteUserDetailView extends AppCompatActivity {

    FirebaseDatabase fbdb;

    Bundle extras;
    String resId;

    TextView name, sucursal, direccion, telefono, ubicacion, humedad, temperatura, calidad, alerta;
    Button btn_wa, btn_eliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante_user_detail_view);

        name = (TextView) findViewById(R.id.tv_name);
        sucursal = (TextView) findViewById(R.id.tv_sucursal);
        direccion = (TextView) findViewById(R.id.tv_direccion);
        telefono = (TextView) findViewById(R.id.tv_telefono);
        ubicacion = (TextView) findViewById(R.id.tv_ubicacion);
        humedad = (TextView) findViewById(R.id.tv_humedad);
        temperatura = (TextView) findViewById(R.id.tv_temperatura);
        calidad = (TextView) findViewById(R.id.tv_calidad);
        alerta = (TextView) findViewById(R.id.tv_alerta);
        alerta.setText("");
        alerta.setVisibility(View.INVISIBLE);

        btn_wa = (Button) findViewById(R.id.btn_wa);
        btn_eliminar = (Button) findViewById(R.id.btn_eliminarResUser);

        if (savedInstanceState == null){
            /*fetching extra data passed with intents in a Bundle type variable*/
            extras = getIntent().getExtras();
            if(extras == null) {
                resId = null;
            } else {
                resId= extras.getString("RES_ID");
                DocumentReference df = FirebaseFirestore.getInstance().collection("restaurante").document(resId);
                df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                final int[] counter = {0};
                                // Aqui es donde va el código de Realtime DB
                                Map<String, Object> resData = doc.getData();

                                name.setText((String) Objects.requireNonNull(Objects.requireNonNull(resData).get("nombre")));
                                sucursal.setText(String.valueOf((int) Math.toIntExact((Long) Objects.requireNonNull(Objects.requireNonNull(resData).get("numSucursal")))));
                                direccion.setText((String) Objects.requireNonNull(Objects.requireNonNull(resData).get("direccion")));
                                telefono.setText((String) Objects.requireNonNull(Objects.requireNonNull(resData).get("telefono")));

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String number;
                                if (user != null) {
                                    number = user.getPhoneNumber();
                                } else {
                                    number = "+000000000000"; // Formato para mandar el número
                                }

                                btn_wa.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String url = "https://api.whatsapp.com/send?phone=" + number;
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(url));
                                        startActivity(i);
                                    }
                                });

                                btn_eliminar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DocumentReference ur = FirebaseFirestore.getInstance().collection("usuarios").document(user.getUid());
                                        ur.update("restaurantes", FieldValue.arrayRemove(df));


                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(RestauranteUserDetailView.this, Restaurantes.class);
                                                startActivity(intent);
                                            }
                                        }, 300);
                                    }
                                });

                                fbdb = FirebaseDatabase.getInstance();
                                DatabaseReference sr = fbdb.getReference((String) Objects.requireNonNull(Objects.requireNonNull(resData).get("sensor")));
                                sr.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // This method is called once with the initial value and again
                                        // whenever data at this location is updated.
                                        boolean setAlerta = false;
                                        String regCode = (String) dataSnapshot.child("regCode").getValue(String.class);
                                        ubicacion.setText((String) dataSnapshot.child("ubicacion").getValue(String.class));

                                        int valHum = (int) Math.toIntExact((Long) dataSnapshot.child("humedad").getValue());
                                        if (valHum < 33){
                                            humedad.setText("Humedad: Baja (" + String.valueOf(valHum) + "%)");
                                        } else if (valHum < 40){
                                            humedad.setText("Humedad: Comfort (" + String.valueOf(valHum) + "%)");
                                        } else {
                                            humedad.setText("Humedad: Posible estática (" + String.valueOf(valHum) + "%)");
                                        }

                                        int valTemp = (int) Math.toIntExact((Long) dataSnapshot.child("temperatura").getValue());
                                        if (valTemp < 22){
                                            setAlerta = true;
                                            temperatura.setText("Temperatura: Muy frío (" + String.valueOf(valTemp) + "°C)");
                                        } else if (valTemp < 24){
                                            setAlerta = setAlerta || false;
                                            temperatura.setText("Temperatura: Frío (" + String.valueOf(valTemp) + "°C)");
                                        } else if (valTemp < 25){
                                            setAlerta = setAlerta || false;
                                            temperatura.setText("Temperatura: Comfort (" + String.valueOf(valTemp) + "°C)");
                                        } else if (valTemp < 27){
                                            setAlerta = true;
                                            temperatura.setText("Temperatura: Caliente (" + String.valueOf(valTemp) + "°C)");
                                        } else {
                                            setAlerta = true;
                                            temperatura.setText("Temperatura: Sofocante (" + String.valueOf(valTemp) + "°C)");
                                        }

                                        int valCalidad = (int) Math.toIntExact((Long) dataSnapshot.child("calidad").getValue());
                                        if (valCalidad < 181){
                                            setAlerta = setAlerta || false;
                                            calidad.setText("Calidad: Bueno (" + String.valueOf(valCalidad) + "ppm)");
                                        } else if (valCalidad < 225){
                                            setAlerta = setAlerta || false;
                                            calidad.setText("Calidad: Pobre (" + String.valueOf(valCalidad) + "ppm)");
                                        } else if (valCalidad < 300){
                                            setAlerta = true;
                                            calidad.setText("Calidad: Muy mala (" + String.valueOf(valCalidad) + "ppm)");
                                        } else if (valCalidad < 350){
                                            setAlerta = true;
                                            calidad.setText("Calidad: Tóxico (" + String.valueOf(valCalidad) + "ppm)");
                                        } else {
                                            setAlerta = true;
                                            calidad.setText("Calidad: Mortal (" + String.valueOf(valCalidad) + "ppm)");
                                        }

                                        if (setAlerta) {
                                            alerta.setText("ALERTA!");
                                        }

                                        Log.println(Log.DEBUG, "Sensor", "Completed and exists: " + regCode);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        // Failed to read value
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }

        DocumentReference df = FirebaseFirestore.getInstance().collection("usuarios").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        if ((Boolean) ((Map) doc.getData().get("role")).get("jefe")){
                            btn_wa.setVisibility(View.INVISIBLE);
                            btn_eliminar.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });
    }
}