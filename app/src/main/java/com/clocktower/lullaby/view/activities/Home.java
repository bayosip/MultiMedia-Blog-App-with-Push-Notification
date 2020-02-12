package com.clocktower.lullaby.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;

import com.clocktower.lullaby.interfaces.AlarmViewInterFace;
import com.clocktower.lullaby.R;
import com.clocktower.lullaby.model.SongInfo;
import com.clocktower.lullaby.model.utilities.Constants;
import com.clocktower.lullaby.model.utilities.GeneralUtil;
import com.clocktower.lullaby.present.AlarmPresenter;
import com.clocktower.lullaby.view.fragments.home.HomePageFragmentAdapter;
import com.clocktower.lullaby.view.fragments.home.AlarmSetterFragment;
import com.clocktower.lullaby.view.fragments.home.BaseFragment;
import com.clocktower.lullaby.view.fragments.home.BlogFragment;
import com.clocktower.lullaby.view.fragments.home.MusicSelectorDialog;
import com.clocktower.lullaby.view.fragments.home.TrackSetterFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.LinkedList;
import java.util.List;

public class Home extends AppCompatActivity implements AlarmViewInterFace {

    private static final String TAG = "Home";
    private ViewPager pager;
    private FloatingActionButton fab;
    private BottomNavigationView bottomNavigationView;
    private int flag;
    private HomePageFragmentAdapter adapter;
    private List<BaseFragment> fragmentList;
    private TextView title;
    private BlogFragment blogFrag;
    private TrackSetterFragment trackFrag;
    private AlarmSetterFragment alarmFrag;
    private MusicSelectorDialog musicSelectorDialog;
    private MediaController mediaController;
    private AlarmPresenter presenter;
    private Toolbar toolbar;
    private List<SongInfo> audioFiles;
    private SongInfo chosenSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialisePrequisites();
        setupActionBar();

        initialiseWidgets();
    }

    private void initialisePrequisites() {
        presenter = new AlarmPresenter(this);
        fragmentList = new LinkedList<>();
        blogFrag = BlogFragment.getInstance();
        alarmFrag = AlarmSetterFragment.getInstance();
        trackFrag = TrackSetterFragment.getInstance();
        musicSelectorDialog = MusicSelectorDialog.getInstance();
        mediaController = new MediaController(this);

        fragmentList.add(blogFrag);
        fragmentList.add(alarmFrag);
        fragmentList.add(trackFrag);

        adapter = new HomePageFragmentAdapter(getSupportFragmentManager(), fragmentList);
        //mp = MediaPlayer.
    }

    private void setupActionBar() {
        toolbar = findViewById(R.id.appbar_alarm);
        title = findViewById(R.id.alarm_toolbar_name);
        bottomNavigationView = findViewById(R.id.navigationView);
    }

    private void setupHomeActionBar() {

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();

        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        ab.setDisplayShowHomeEnabled(false); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(false);
    }


    private void setupOtherActionBar() {
        // Get the ActionBar here to configure the way it behaves.
        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();

        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

    }

    private void initialiseWidgets() {
        setupActionBar();
        pager = findViewById(R.id.page_container);
        fab = findViewById(R.id.buttonMusicContent);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audioFiles != null) {
                    musicSelectorDialog.show(getSupportFragmentManager());
                }

                else GeneralUtil.showAlertMessage(Home.this, "Error!",
                        "No Audio Files Found");
            }
        });

        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(pageChangeListener);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener  navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.navigation_home:
                            pager.setCurrentItem(0);
                            return true;
                        case R.id.navigation_alarm:
                            pager.setCurrentItem(1);
                            return true;
                        case R.id.navigation_music:
                            pager.setCurrentItem(2);
                            return true;

                    }
                    return false;
                }
            };

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (adapter.getPageTitle(position).equals(Constants.HOME)) {
                title.setText(Constants.HOME);
                setupHomeActionBar();
                fab.hide();
            }else if (adapter.getPageTitle(position).equals(Constants.ALARM_SETTER)){
                fab.hide();
                flag = Constants.SET_ALARM_TRACK_FLAG;
                title.setText(Constants.ALARM_SETTER);
                setupOtherActionBar();
            } else if (adapter.getPageTitle(position).equals(Constants.MUSIC_SELECTOR)) {
                fab.setImageResource(R.drawable.ic_queue_music_24dp);
                flag = Constants.TRACK_SELECTOR_FLAG;
                setupOtherActionBar();
                accessFilesFromPhone();
                title.setText(Constants.MUSIC_SELECTOR);
                musicPlayerThread(trackFrag.getHandler());
                fab.show();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    private void accessFilesFromPhone() {
        Dexter.withActivity(Home.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        audioFiles= presenter.loadSongs();
                        MusicSelectorDialog.setAudioFiles(audioFiles);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                   PermissionToken token) {
                        token.cancelPermissionRequest();
                    }
                }).check();
    }

    @Override
    public void onMusicTrackClick(int position) {
        chosenSong = audioFiles.get(position);
        trackFrag.selectMusic(chosenSong.getSongName());
        presenter.startNewMusic(chosenSong.getSongUrl());
        trackFrag.changePlayButtonRes(R.drawable.ic_pause_24dp);
        musicSelectorDialog.dismiss();
    }

    @Override
    public void setAlarm(int hour, int minute) {
        presenter.setAlarm(hour, minute);
    }

    @Override
    public void stopAlarm() {
        presenter.cancelAlarm();
    }

    @Override
    public void playOrPauseMusic(FragmentManager manager) {
        if (!presenter.musicIsPlaying()) {
            if (chosenSong!= null) {
                presenter.playMusic();
                trackFrag.changePlayButtonRes(R.drawable.ic_pause_24dp);
            }else {
                if (audioFiles != null) {
                    musicSelectorDialog.show(manager);
                }
            }
        } else {
            presenter.pauseMusic();
            trackFrag.changePlayButtonRes(R.drawable.ic_play_arrow_24dp);
        }
    }

    @Override
    public void stopMusic() {
        presenter.stopMusic();
        trackFrag.changePlayButtonRes(R.drawable.ic_play_arrow_24dp);
    }

    @Override
    public void musicPlayerThread(Handler handler) {
        presenter.musicPlayerThread(handler);
    }

    @Override
    public MediaController getVideoMediaController() {
        return mediaController;
    }

    @Override
    public void setAlarmMusic() {
        presenter.setAlarmTone(chosenSong.getSongUrl());
    }

    @Override
    public void seekMusicToPosition(int time) {
        presenter.seekMusic(time);
    }

    @Override
    public Home getListenerContext() {
        return Home.this;
    }

    @Override
    public void setTrackBarForMusic(int duration) {
        trackFrag.calibrateTrackBarForMusic(duration);
    }

    @Override
    public void goToMusicSetter() {
        pager.setCurrentItem(2);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public Fragment getCurrentFragmentInView() {
        return getSupportFragmentManager().findFragmentById(R.id.page_container );
    }



    @Override
    public void onBackPressed() {
        if ( pager.getCurrentItem() == 0) {
            GeneralUtil.exitApp(Home.this);
        }
        else {
            pager.setCurrentItem(0);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}