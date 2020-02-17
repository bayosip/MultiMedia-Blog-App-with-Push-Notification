package com.clocktower.lullaby.view.list.blog_list;

import android.media.MediaPlayer;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.RecyclerView;

import com.clocktower.lullaby.R;
import com.clocktower.lullaby.interfaces.FragmentListener;
import com.clocktower.lullaby.model.CozaBlog;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.util.Date;
import java.util.List;

public class BlogVH extends RecyclerView.ViewHolder implements View.OnClickListener {


    VideoView video;
    TextView elaspedTime, postTitle;
    TextView likeCount, commentCount;
    ImageView like, comment, imgPost;
    ImageButton playVideoBtn;
    View mediaView;
    ContentLoadingProgressBar buffering;
    String url;
    String postId;
    private FragmentListener listener;


    public BlogVH(@NonNull View itemView) {
        super(itemView);
        initialiseWidgets(itemView);
    }

    private void initialiseWidgets(View v){

        video = v.findViewById(R.id.videoViewPost);
        video.setOnClickListener(this);
        imgPost = v.findViewById(R.id.imageViewPost);
        elaspedTime = v.findViewById(R.id.textElaspedTime);
        postTitle = v.findViewById(R.id.textPostTitle);
        likeCount = v.findViewById(R.id.text_blog_like_count);
        commentCount =v.findViewById(R.id.text_post_comment_count);
        like = v.findViewById(R.id.post_like_btn);
        like.setOnClickListener(this);
        comment = v.findViewById(R.id.post_comment_icon);
        comment.setOnClickListener(this);
        playVideoBtn = v.findViewById(R.id.buttonPlayVideo);
        playVideoBtn.setOnClickListener(this);
        buffering = v.findViewById(R.id.progress_video_loading);
        mediaView = v.findViewById(R.id.mediaView);

    }

    public void setMediaController(MediaController mediaController){
        video.setMediaController(mediaController);
        mediaController.setAnchorView(video);
    }

    public void setBlogItems(List<CozaBlog> posts){

        postId = posts.get(getAdapterPosition()).getPost().postId;
        listener.updateLikesCount(postId);
        listener.updateCommentCount(postId);
        likeCount.setText(listener.getListenerContext().getString(R.string.like_count,
                posts.get(getAdapterPosition()).getLikeCount()));
        commentCount.setText(listener.getListenerContext().getString(R.string.comment_count,
                posts.get(getAdapterPosition()).getLikeCount()));
        if (posts.get(getAdapterPosition()).isLiked()){
            like.setImageResource(R.drawable.ic_like_on_24dp);
        }else {
            like.setImageResource(R.drawable.ic_like_off_24dp);
        }
        postTitle.setText(posts.get(getAdapterPosition()).getPost().getTitle());

        mediaView.setVisibility(posts.get(getAdapterPosition()).getPost().getMediaType()==0?
                View.GONE: View.VISIBLE);
        video.setVisibility(posts.get(getAdapterPosition()).getPost().getMediaType()==2?
                View.VISIBLE: View.GONE);
        imgPost.setVisibility(posts.get(getAdapterPosition()).getPost().getMediaType()==1?
                View.VISIBLE: View.GONE);
        playVideoBtn.setVisibility(posts.get(getAdapterPosition()).getPost().getMediaType()==2?
                View.VISIBLE: View.GONE);
        url = posts.get(getAdapterPosition()).getPost().getUrl();

        if(posts.get(getAdapterPosition()).getPost().getMediaType()==1)
            UrlImageViewHelper.setUrlDrawable(imgPost, url);

        try {
            long millisecond = posts.get(getAdapterPosition()).getPost().getTimeStamp().getTime();
            String dateString = DateFormat.format("MMM dd yyyy, HH:mm:ss", new Date(millisecond)).toString();
            elaspedTime.setText(dateString);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonPlayVideo:
               if (!video.isPlaying()) {
                   playVideoBtn.setImageResource(R.drawable.ic_pause_video_24dp);
                   playVideoBtn.setVisibility(View.GONE);
                   buffering.show();
                   playSelectedVideoFrom(url);
               }else {
                   playVideoBtn.setImageResource(R.drawable.ic_play_video_24dp);
                   playVideoBtn.setVisibility(View.VISIBLE);
                   video.pause();
               }
               break;
            case R.id.videoViewPost:
                playVideoBtn.setVisibility(View.VISIBLE);
                break;
            case R.id.post_like_btn:
                listener.likeThisPost(postId);
                break;
            case R.id.post_comment_icon:
                listener.openCommentSectionOnPostWithId(postId);
                break;
        }
    }

    private void playSelectedVideoFrom(String url){

        try {
            Uri uri = Uri.parse(url);
            video.setVideoURI(uri);
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    buffering.hide();
                    mediaPlayer.setLooping(true);
                    video.start();
                }
            });
            video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playVideoBtn.setImageResource(R.drawable.ic_play_video_24dp);
                    playVideoBtn.setVisibility(View.VISIBLE);
                }
            });

        }catch (Exception ex){
            ex.printStackTrace();
        }
        video.requestFocus();

    }

    public void setListener(FragmentListener listener) {
        this.listener = listener;
    }
}
