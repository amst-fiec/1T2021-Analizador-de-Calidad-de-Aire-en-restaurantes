package com.analizadoraire.windKind;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RestaurantesAdapter extends ArrayAdapter<String> {

    private LayoutInflater mInflater;

    private ArrayList<String[]> mString;

    private int mViewResourceId;

    private Context context;

    public RestaurantesAdapter(@NonNull Context context, int viewResourceId, ArrayList<String[]> strings) {
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

        Button btn = (Button) convertView.findViewById(R.id.btn_verResUser);
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

        return convertView;
    }
}
