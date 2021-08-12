package com.analizadoraire.windKind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final Context context = this;

    // Cuando se crea la instancia
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    // Cuando se vuelve visible
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            changeBasedOnRole();
        }
    }

    public void goToMailLogin(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Devuelve true si es que el usuario es jefe false si es que el usuario es usuario
     * @return
     */
    public void changeBasedOnRole(){
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            DocumentReference df = FirebaseFirestore.getInstance().collection("usuarios").document(currentUser.getUid());
            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()){
                            if ((Boolean) ((Map) doc.getData().get("role")).get("jefe")){
                                Intent intent = new Intent(context, RestaurantesParaJefes.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(context, RestaurantesParaJefes.class);
                                startActivity(intent);
                            }
                        }
                    }
                }
            });
        }
    }
}