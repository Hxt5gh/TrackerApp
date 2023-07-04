package com.hxt5gh.android.tracker.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.transition.Hold;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hxt5gh.android.tracker.Activities.UpdateActivity;
import com.hxt5gh.android.tracker.MainActivity;
import com.hxt5gh.android.tracker.Models.ClientClass;
import com.hxt5gh.android.tracker.Models.UserClass;
import com.hxt5gh.android.tracker.R;

import java.util.ArrayList;

public class userAdapter  extends RecyclerView.Adapter<userAdapter.userViewHolder> {

    private ArrayList<ClientClass> list ;
    private Context context;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private MainActivity mainActivity;

    public userAdapter(ArrayList<ClientClass> list, Context context) {
        this.list = list;
        this.context = context;
    }



    @NonNull
    @Override
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent , false);
        return new userViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userViewHolder holder, int position) {

        int pos = position;

        holder.name.setText(list.get(pos).getName());
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(100, 100);
        Glide.with(context).load(list.get(pos).getImageUrl()).apply(requestOptions).placeholder(R.drawable.user).into(holder.profile);
        holder.mNumber.setText(list.get(pos).getmNumber());

        int priority = list.get(pos).getPriority();


        if (priority == 0) {
            holder.priority.setImageResource(R.drawable.red_priority);
        } else if (priority == 1) {
            holder.priority.setImageResource(R.drawable.green_priority);
        }

        // Set a default tag for the priority view
        holder.priority.setTag(R.drawable.red_priority);

        holder.priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if ((int) holder.priority.getTag() == R.drawable.red_priority) {
                    // Perform actions when the red_priority image is clicked
                    // Add your code here
                    holder.priority.setImageResource(R.drawable.green_priority);
                    holder.priority.setTag(R.drawable.green_priority);

                    mRef.child("Data/" +FirebaseAuth.getInstance().getUid() +"/" +list.get(pos).getPushId() +"/priority" ).setValue(1);



                } else if ((int) holder.priority.getTag() == R.drawable.green_priority) {
                    holder.priority.setImageResource(R.drawable.red_priority);
                    holder.priority.setTag(R.drawable.red_priority);

                    mRef.child("Data/" +FirebaseAuth.getInstance().getUid() +"/" +list.get(pos).getPushId() +"/priority" ).setValue(0);

                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , UpdateActivity.class);
                intent.putExtra("name" , list.get(pos).getName());
                intent.putExtra("mNo", list.get(pos).getmNumber());
                intent.putExtra("imageUri" , list.get(pos).getImageUrl());
                intent.putExtra("pushID" , list.get(pos).getPushId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return  list.size();
    }

    public class userViewHolder extends RecyclerView.ViewHolder {
        ImageView profile , priority;
        TextView name , mNumber;
        public userViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.idUserName);
            priority = itemView.findViewById(R.id.idpriority);
            mNumber = itemView.findViewById(R.id.mNumber);

        }
    }
}
