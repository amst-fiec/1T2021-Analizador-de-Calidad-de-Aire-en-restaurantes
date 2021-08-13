package com.analizadoraire.windKind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RestaurantesParaJefes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurantes_para_jefes);

        String name = "";
        String email = "";
        String uid = "";
        final String[] message = {"Valor inicial"};
        Uri photoUrl;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            name = user.getDisplayName();
            email = user.getEmail();
            photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            uid = user.getUid();
        }

        Log.println(Log.DEBUG, "UUID", user.getUid());

        updateData(name, email, uid, "okay jefes");
    }

    public void updateData(String name, String email, String uid, String message){
        TextView data = (TextView) findViewById(R.id.data);

        data.setText("Nombre: " + name + "\nEmail: " + email + "\nUUID: " + uid + "\nMessage: " + message);
    }

    public void logOut(View view){
        DBHelper.logOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}