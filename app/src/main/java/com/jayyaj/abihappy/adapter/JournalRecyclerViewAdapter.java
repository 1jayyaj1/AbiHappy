package com.jayyaj.abihappy.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jayyaj.abihappy.R;
import com.jayyaj.abihappy.model.Journal;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class JournalRecyclerViewAdapter extends RecyclerView.Adapter<JournalRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Journal> journalList;
    private Bitmap shareImage;

    public JournalRecyclerViewAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.journal_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rowProgressBar.setVisibility(View.VISIBLE);
        Journal journal = journalList.get(position);

        Picasso.get().load(journal.getImageUrl()).into(target);

        holder.title.setText(journal.getTitle());
        holder.thought.setText(journal.getThought());
        holder.name.setText(journal.getUserName());
        String timeAgo = (String) DateUtils
                .getRelativeTimeSpanString(journal.getTimeAdded().getSeconds() * 1000);
        holder.dateAdded.setText(timeAgo);
        Picasso.get().load(journal.getImageUrl())
                .resize(2560, 1440)
                .centerCrop()
                .error(android.R.drawable.stat_notify_error)
                .into(holder.image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        holder.rowProgressBar.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onError(Exception e) {
                        holder.rowProgressBar.setVisibility(View.INVISIBLE);
                    }
                });

        holder.shareButton.setOnClickListener(v -> {
            Uri imageToShare = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), shareImage, "Share image", null));
            String subjectToShare = "I'm thankful for the fact that " + journal.getTitle().toLowerCase();
            String textToShare = "I wanted to share this memory with you \n\n From: " + journal.getUserName();

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.setType("*/*");
            share.putExtra(Intent.EXTRA_TEXT, textToShare);
            share.putExtra(Intent.EXTRA_SUBJECT, subjectToShare);
            share.putExtra(Intent.EXTRA_STREAM, imageToShare);
            context.startActivity(share);

        });
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            shareImage = bitmap;
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, thought, dateAdded, name;
        public ImageView image;
        public ProgressBar rowProgressBar;
        public ImageButton shareButton;
        public String userId;
        public String username;
        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            title = itemView.findViewById(R.id.journal_title_timeline);
            thought = itemView.findViewById(R.id.journal_thought_timeline);
            dateAdded = itemView.findViewById(R.id.journal_timestamp_timeline);
            image = itemView.findViewById(R.id.journal_image_timeline);
            rowProgressBar = itemView.findViewById(R.id.rowProgressBar);
            name = itemView.findViewById(R.id.journal_username_timeline);
            shareButton = itemView.findViewById(R.id.journal_share_timeline);
        }
    }
}
