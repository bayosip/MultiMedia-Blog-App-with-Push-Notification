package com.clocktower.lullaby.interfaces;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.widget.MediaController;

import androidx.fragment.app.FragmentManager;

import com.clocktower.lullaby.model.Post;

public interface FragmentListener extends ListItemClickListener {

    void setAlarm(int hour, int minute);
    void stopAlarm();
    void playOrPauseMusic(FragmentManager manager);
    void stopMusic();
    void setAlarmMusic();
    void seekMusicToPosition(int time);
    void musicPlayerThread(Handler handler);
    MediaController getVideoMediaController();
    void startLoadingPostsFromFirebase();

    void loadMorePost();

    void clearList();

    //Post upload Interface Method

    //Blog interface functions
    void updateLikesCount(String postID);
    void updateCommentCount(String postId);
    void openCommentSectionOnPostWithId(String postID, String title);
    void retrieveAllComments(String postId);
    void postACommentOnPostWithId(String postId, String msg);
    void restoreViewsAfterLeavingCommentSection();
    void removeToolbars();

    void saveNewPostInDB(Post post, long type);

}
