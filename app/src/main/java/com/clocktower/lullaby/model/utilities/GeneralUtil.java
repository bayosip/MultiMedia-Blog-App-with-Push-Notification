package com.clocktower.lullaby.model.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Toast;

import com.clocktower.lullaby.App;
import com.clocktower.lullaby.view.activities.AppFinish;

import java.net.InetAddress;

/**
 * Created by BABY v2.0 on 10/11/2016.
 */

public class GeneralUtil {
    private Context context;
    private Toast toast;

    private static Handler uiHandler;

    static {
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public static void message(String message) {
        Toast.makeText(App.context, message, Toast.LENGTH_SHORT).show();
    }

    public static Handler getHandler() {
        return uiHandler;
    }


    public static void exitApp(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) activity.finishAndRemoveTask();
        else activity.finish();
        Intent intent = new Intent(activity, AppFinish.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        activity.startActivity(intent);
    }

    public static Drawable setADrawable(Activity activity, int drawableID) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= 21) {
            drawable = activity.getResources().getDrawable(drawableID,
                    activity.getApplicationContext().getTheme());
        } else {
            drawable = activity.getResources().getDrawable(drawableID);
        }
        return drawable;
    }

    public static void transitionActivity(Activity oldActivity, Class newActivity) {
        Activity activity = oldActivity;
        if (Build.VERSION.SDK_INT >= 21) activity.finishAndRemoveTask();
        else activity.finish();

        //oldActivity.overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
        oldActivity.startActivity(new Intent(oldActivity, newActivity));
    }

    public static void transitionActivity(Activity oldActivity, Intent intent) {
        if (Build.VERSION.SDK_INT >= 21) oldActivity.finishAndRemoveTask();
        else oldActivity.finish();
        oldActivity.startActivity(intent);
    }

    public static boolean isNetworkConnected(Context activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork !=null && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI||
                activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)) {
            return activeNetwork.isConnected();
        }
        return false;
    }


    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(),
                View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static class ResizeAnimation extends Animation {
        private View mView;
        private float mToHeight;
        private float mFromHeight;

        private float mToWidth;
        private float mFromWidth;

        public ResizeAnimation(View v, float fromWidth, float fromHeight, float toWidth, float toHeight) {
            mToHeight = toHeight;
            mToWidth = toWidth;
            mFromHeight = fromHeight;
            mFromWidth = fromWidth;
            mView = v;
            float targetH = Math.max(mFromHeight, mToHeight);
            setDuration((int)(targetH / v.getContext().getResources().getDisplayMetrics().density));
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float height =
                    (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
            float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
            ViewGroup.LayoutParams p = mView.getLayoutParams();
            p.height = (int) height;
            p.width = (int) width;
            mView.requestLayout();
        }
    }
}
