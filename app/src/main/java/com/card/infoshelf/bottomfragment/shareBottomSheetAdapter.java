package com.card.infoshelf.bottomfragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.card.infoshelf.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class shareBottomSheetAdapter extends RecyclerView.Adapter<shareBottomSheetAdapter.MyviewHolder> {

    private Context context;
    private List<TagModel> tagPersonList;
    private List<String> CheckBoxTags = new ArrayList<>();
    private List<String> SharedList = new ArrayList<>();

    public shareBottomSheetAdapter(List<TagModel> tagPersonList) {
        this.tagPersonList = tagPersonList;
    }

    @NonNull
    @Override
    public shareBottomSheetAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_view,parent,false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull shareBottomSheetAdapter.MyviewHolder holder, int position) {

        TagModel tagModel = tagPersonList.get(position);
        holder.PersonName.setText(tagModel.getUserName());
        String imageUri = tagModel.getProfile_image();
        Picasso.get().load(imageUri).into(holder.personimg);


    }

    @Override
    public int getItemCount() {
        return tagPersonList.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {

        TextView PersonName;
        LinearLayout rootView;
        TextView sharepersonBtn;
        ImageView personimg;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            PersonName = itemView.findViewById(R.id.personnameTag);
            rootView = itemView.findViewById(R.id.rootView);
            sharepersonBtn = itemView.findViewById(R.id.sharepersonBtn);

            personimg = itemView.findViewById(R.id.PersonImage);

        }
    }
}
