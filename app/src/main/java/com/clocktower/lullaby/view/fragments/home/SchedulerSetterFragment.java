package com.clocktower.lullaby.view.fragments.home;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.clocktower.lullaby.R;

public class SchedulerSetterFragment extends BaseFragment {
    private Button stopAlarm, setAlarm;
    private TimePicker timePicker;

    public static SchedulerSetterFragment getInstance(){
        SchedulerSetterFragment fragment = new SchedulerSetterFragment();
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_scheduler, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialiseWidgets(view);
    }

    private void initialiseWidgets(View view){
        stopAlarm = view.findViewById(R.id.buttonStopAlarm);
        setAlarm = view.findViewById(R.id.buttonSetAlarm);
        timePicker =view.findViewById(R.id.timeAlarmSetter);

        setAlarm.setOnClickListener(onClickListener);
        stopAlarm.setOnClickListener(onClickListener);
    }

    private Button.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.buttonStopAlarm:
                    listener.stopAlarm();
                    break;
                case R.id.buttonSetAlarm:
                    if (Build.VERSION.SDK_INT >=23) {
                        listener.setAlarm(timePicker.getHour(), timePicker.getMinute());
                    }else {
                        listener.setAlarm(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                    }
                    break;
            }
        }
    };
}
