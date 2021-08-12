package com.analizadoraire.windKind;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class DBHelper extends AppCompatActivity {

    public static void logOut(){
        FirebaseAuth.getInstance().signOut();
    }
}
