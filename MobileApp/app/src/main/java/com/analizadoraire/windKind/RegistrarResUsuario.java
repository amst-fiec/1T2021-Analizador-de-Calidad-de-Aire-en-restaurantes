package com.analizadoraire.windKind;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RegistrarResUsuario extends AppCompatActivity {

    private EditText codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_res_usuario);

        codigo = (EditText) findViewById(R.id.et_codResUsu);
    }


    public void regResUser(View view) {
        String regCode = codigo.getText().toString();

        FirebaseFirestore.getInstance()
                .collection("restaurante")
                .whereEqualTo("regCode", regCode)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        List<DocumentSnapshot> snapshotsList = documentSnapshots.getDocuments();
                        if (snapshotsList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Restaurante no existe.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        for (DocumentSnapshot res : snapshotsList) {
                            if (res.exists()) {
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                DocumentReference rr = res.getReference();
                                DocumentReference df = FirebaseFirestore.getInstance().collection("usuarios").document(currentUser.getUid());
                                df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            DocumentSnapshot doc = task.getResult();
                                            if (doc.exists()){
                                                if ((Boolean) ((Map) Objects.requireNonNull(doc.getData()).get("role")).get("jefe")){
                                                    Intent intent = new Intent(RegistrarResUsuario.this, MainActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    ArrayList<DocumentReference> referencias = ((ArrayList<DocumentReference>) Objects.requireNonNull(doc.getData().get("restaurantes")));
                                                    if (referencias.contains(rr)){
                                                        Toast.makeText(getApplicationContext(), "Restaurante ya está registrado.",
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        referencias.add(rr);

                                                        Map<String, Object> update = new HashMap<>();
                                                        update.put("restaurantes", referencias);

                                                        df.set(update, SetOptions.merge());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Restaurante no existe.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
            }
        });
    }
}