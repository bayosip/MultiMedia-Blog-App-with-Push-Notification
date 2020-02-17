package com.clocktower.lullaby.view.list.comment_list;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.clocktower.lullaby.R;
import com.clocktower.lullaby.model.Comments;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;


import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListViewHolder extends RecyclerView.ViewHolder {

    private TextView userName, comment, amountWon, position;
    private CircleImageView profilePic;
    private String tag;

    public CommentListViewHolder(View itemView) {
        super(itemView);
        setUpCommentsWidgets(itemView);
    }

    public String getTag() {
        return tag;
    }

    private void setUpCommentsWidgets(View v){
        userName = v.findViewById(R.id.textViewUser);
        comment = v.findViewById(R.id.textViewComment);
        profilePic = v.findViewById(R.id.imageUser);
    }

    public void getUsernameColor() {
        Random r = new Random();
        int red=r.nextInt(255 - 0 + 1)+0;
        int green=r.nextInt(255 - 0 + 1)+0;
        int blue=r.nextInt(255 - 0 + 1)+0;

        int color = Color.argb(255, red, green, blue);
        userName.setTextColor(color);
    }

    public void setComments(List<Comments> comments) {
        userName.setText(comments.get(getAdapterPosition()).getUsername());
        getUsernameColor();
        comment.setText(comments.get(getAdapterPosition()).getComment());
        UrlImageViewHelper.setUrlDrawable(profilePic, comments.get(getAdapterPosition()).getUrl());
    }
}
