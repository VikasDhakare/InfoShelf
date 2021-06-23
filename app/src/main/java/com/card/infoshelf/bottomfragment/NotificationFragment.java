package com.card.infoshelf.bottomfragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.card.infoshelf.R;
import com.card.infoshelf.postDetailsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class NotificationFragment extends Fragment {

    private RecyclerView noti_recycler;
    private FirebaseRecyclerAdapter<noti_model, notification_viewHolder> adapter;
    private DatabaseReference NotiRef;
    private FirebaseAuth mAuth;
    private String CurrentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_notification, container, false);

        noti_recycler = root.findViewById(R.id.noti_recycler);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        NotiRef = FirebaseDatabase.getInstance().getReference("Users");

        noti_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadNotification();

        return root;
    }

    private void loadNotification() {
        FirebaseRecyclerOptions<noti_model> options = new FirebaseRecyclerOptions.Builder<noti_model>().setQuery(NotiRef.child(CurrentUserId).child("Notifications"), noti_model.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<noti_model, notification_viewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull notification_viewHolder holder, int position, @NonNull noti_model model) {
                String uId = model.getsUid();
                String noti_time = model.getTimestamp();
                String message = model.getNotification();
                String pid = model.getpId();
                String status = model.getStatus();

                String showPostTime = holder.getFormateDate(getActivity() , noti_time);

                holder.noti_time.setText(showPostTime);
                holder.reactText.setText(message);

                holder.getUserInfo(uId);
                holder.getPostInfo(pid, getActivity());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), postDetailsActivity.class);
                        intent.putExtra("pId", pid);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Delete");
                        builder.setMessage("Are you sure to delete this notification?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(CurrentUserId).child("Notifications").child(noti_time).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), "Notification Deleted..", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                        return false;
                    }
                });
            }

            @NonNull
            @Override
            public notification_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout, parent, false);
                return new notification_viewHolder(v);
            }
        };
        adapter.notifyDataSetChanged();
        adapter.startListening();
        noti_recycler.setAdapter(adapter);
    }
}