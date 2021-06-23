package com.card.infoshelf.profileFragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.card.infoshelf.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.theartofdev.edmodo.cropper.CropImageView.RequestSizeOptions;

import java.util.List;

public class videoAdapter extends RecyclerView.Adapter<videoAdapter.MyViewHolder> {
    private Context context;
    public List<GridModel> videoArray;

    public videoAdapter(Context context, List<GridModel> videoArray) {
        this.context = context;
        this.videoArray = videoArray;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_for_video,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GridModel gridModel = videoArray.get(position);
        String videoUrl = gridModel.getPostURL();
        String extensionType = gridModel.getFileType();
        if (extensionType.equals("video")){
            holder.Post_image.setVisibility(View.VISIBLE);
            long interval = holder.getPosition()*1000;
            RequestOptions options = new RequestOptions().frame(interval);
//            Glide.with(context).asBitmap().load(videoUrl).apply(options).into(holder.Post_image);
            Glide.with(context)
                    .load(videoUrl)
                    .apply(options)
                    .error(R.drawable.ic_baseline_video_library_24)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(holder.Post_image);
        }

    }

    @Override
    public int getItemCount() {
        return videoArray.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView Post_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Post_image = itemView.findViewById(R.id.gridImageview);

        }
    }
}
