package com.analizadoraire.windKind;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Restaurantes extends ListActivity {

    ListView listaRestaurantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurantes);

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
        Context context = getApplicationContext();

        listaRestaurantes = (ListView) findViewById(android.R.id.list);

        ArrayList<String[]> datos = new ArrayList<>();

        setListAdapter(new RestaurantesAdapter(context, R.layout.restaurant_user_card, datos));
    }

    public void logOut(View view){
        DBHelper.logOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToRegister(View view) {

    }
}