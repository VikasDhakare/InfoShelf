package com.card.infoshelf.bottomfragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.card.infoshelf.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
//
//import static android.content.Context.MODE_PRIVATE;
//import static com.card.infoshelf.bottomfragment.AddTimeline.BottomSheetDialog.SHARED_PREFS;
//import static com.card.infoshelf.bottomfragment.AddTimeline.BottomSheetDialog.TEXT;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.MyViewHolder>{
    private Context context;
    private List<TagModel> tagPersonList;
    private List<String> CheckBoxTags = new ArrayList<>();
    public static  final String SHARED_PREFS = "sharedPrefs";
    public static  final String TEXT = "text";
    private List<String> SharedList = new ArrayList<>();
    public List<String> getArrayTags(){
        return CheckBoxTags;
    }

    public TagAdapter(List<TagModel> tagPersonList) {
        this.context = context;
        this.tagPersonList = tagPersonList;
    }

    @NonNull
    @Override
    public TagAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagAdapter.MyViewHolder holder, int position) {
        TagModel tagModel = tagPersonList.get(position);
        holder.PersonName.setText(tagModel.getUserName());
        String imageUri = tagModel.getProfile_image();
        Picasso.get().load(imageUri).into(holder.personimg);
        if (tagModel.isChecker() == true){
            holder.checkBox.setChecked(true);
        }

        String TagiId = tagModel.getUserId();
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setSelected(tagModel.isHold());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tagModel.setHold(true);
                    CheckBoxTags.add(tagModel.getUserId());
                    tagModel.setChecker(true);
                }else {
                    CheckBoxTags.remove(tagModel.getUserId());
                    tagModel.setHold(false);
                    tagModel.setChecker(false);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return tagPersonList.size();
    }

    public void filterList(List<TagModel> newContacts) {
        tagPersonList = newContacts;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView PersonName;
        LinearLayout rootView;
        CheckBox checkBox;
        ImageView personimg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            PersonName = itemView.findViewById(R.id.personnameTag);
            rootView = itemView.findViewById(R.id.rootView);
            checkBox = itemView.findViewById(R.id.Tagcheckbox);

            personimg = itemView.findViewById(R.id.PersonImage);
        }


    }


//    public void updateList(List<TagModel> newContacts){
//        tagPersonList.clear();
//        tagPersonList.addAll(newContacts);
//        notifyDataSetChanged();
//    }
}
