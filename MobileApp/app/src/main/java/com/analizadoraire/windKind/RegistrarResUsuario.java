package com.analizadoraire.windKind;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RegistrarResUsuario extends ListActivity {
    private ListView list_v1;
    private String nombre_dispositivo[]={};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_res_usuario);
        list_v1 = findViewById(R.id.list_v1);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.list_view_devices,nombre_dispositivo);
        list_v1.setAdapter(adapter);
    }


}