package com.analizadoraire.windKind;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Restaurantes extends ListActivity {

    ListView listaRestaurantes;
    private boolean backCounter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurantes);

        Context context = getApplicationContext();

        listaRestaurantes = (ListView) findViewById(android.R.id.list);
        ArrayList<String[]> datos = new ArrayList<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Only accesable when loggeed in so there's no need to check for a null user
        DocumentReference df = FirebaseFirestore.getInstance().collection("usuarios").document(currentUser.getUid());
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        if ((Boolean) ((Map) Objects.requireNonNull(doc.getData()).get("role")).get("jefe")){
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                        } else {
                            final int[] counter = {0};
                            ArrayList<String[]> datos = new ArrayList<>();

                            Log.println(Log.DEBUG, "restaurantes: ", String.valueOf(Objects.requireNonNull(doc.getData().get("restaurantes"))));
                            ArrayList<DocumentReference> referencias = ((ArrayList<DocumentReference>) Objects.requireNonNull(doc.getData().get("restaurantes")));

                            referencias.forEach((n) -> {
                                ((DocumentReference) n).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot res = task.getResult();
                                        if (res.exists()) {
                                            String[] resData = new String[3];
                                            resData[0] = String.valueOf(res.getData().get("nombre"));
                                            resData[1] = String.valueOf(res.getData().get("direccion"));
                                            resData[2] = String.valueOf(n);
                                            Log.println(Log.DEBUG, "restaurantes: ", "Completed " + String.valueOf(resData) + " and exists");
                                            datos.add(resData);
                                            counter[0] += 1;
                                            if (counter[0] == referencias.size()){
                                                setListAdapter(new RestaurantesAdapter(context, R.layout.restaurant_user_card, datos));
                                            }
                                        }
                                    }
                                });
                            });
                        }
                    }
                }
            }
        });

        setListAdapter(new RestaurantesAdapter(context, R.layout.restaurant_user_card, datos));
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (this.backCounter) {
            this.finishAffinity();
        }

        this.backCounter = true;
        Toast.makeText(this, "Presione otra vez para salir", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                backCounter=false;
            }
        }, 2000);

    }

    public void logOut(View view){
        DBHelper.logOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegistrarResUsuario.class);
        startActivity(intent);
    }
}