package com.analizadoraire.windKind;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RestaurantesJefeAdapter extends ArrayAdapter<String> {

    private LayoutInflater mInflater;

    private ArrayList<String[]> mString;

    private int mViewResourceId;

    private Context context;

    public RestaurantesJefeAdapter(@NonNull Context context, int viewResourceId, ArrayList<String[]> strings) {
        super(context, viewResourceId);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mString = strings;

        mViewResourceId = viewResourceId;

        this.context = context;
    }

    @Override
    public int getCount() {
        return mString.size();
    }

    @Override
    public String getItem(int position){
        return mString.get(position).toString();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        convertView = mInflater.inflate(mViewResourceId, null);

        TextView nom = (TextView) convertView.findViewById(R.id.nomResUser);
        nom.setText(mString.get(position)[0]);

        TextView dir = (TextView) convertView.findViewById(R.id.dirResUser);
        dir.setText(mString.get(position)[1]);

        Button btn = (Button) convertView.findViewById(R.id.btn_verResJefe);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RestauranteUserDetailView.class);
                intent.putExtra("RES_ID", mString.get(position)[2]);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                // Toast.makeText(context, "Dentro del click " + mString.get(position)[2], Toast.LENGTH_SHORT).show();
            }
        });

        Button btn_del = (Button) convertView.findViewById(R.id.btn_delResJefe);
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference df = FirebaseFirestore.getInstance().collection("restaurante").document(mString.get(position)[2]);
                // Remove sensor from restaurant
                // Get restaurant
                df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @RequiresApi(api = Build.VERSION_CODES.N)
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if (task.isSuccessful()) {
                           DocumentSnapshot doc = task.getResult();
                           if (doc.exists()) {
                               // Get sensor
                               FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
                               DatabaseReference sr = fbdb.getReference((String) Objects.requireNonNull(Objects.requireNonNull(doc.getData()).get("sensor")));
                               // Remove sensor
                               df.update("sensor", "");
                               // Update sensor isRegistered value
                               sr.child("isRegistered").setValue(false);
                               // Remove restaurant from users
                               FirebaseFirestore.getInstance().collection("usuarios").get()
                                   .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                       @Override
                                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                           if (task.isSuccessful()) {
                                               for (QueryDocumentSnapshot document : task.getResult()) {

                                                   document.getReference().update("restaurantes", FieldValue.arrayRemove(df));
                                               }
                                           } else {
                                               Log.println(Log.DEBUG, "Delete res jefe", "Error loading all users ");
                                           }
                                       }
                                   });
                               df.delete();
                           } else {
                               Toast.makeText(context, "El restaurante que intenta eliminar ya no existe, si esto es un error consulte a soporte técnico", Toast.LENGTH_LONG).show();
                           }
                       } else {
                           Toast.makeText(context, "Algo salió mal al eliminar el restaurante, si los problemas persisten consulte a soporte técnico", Toast.LENGTH_LONG).show();
                       }
                   }
                });

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, RestaurantesParaJefes.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }, 300);
            }
        });

        return convertView;
    }
}