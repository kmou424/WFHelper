package moe.kmou424.WorldFlipper.Helper.Info;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class DateInfo {
    public final String time;
    public boolean isAvailable;

    @SuppressLint({"DefaultLocale", "SimpleDateFormat"})
    public DateInfo(int mYear, int mMonth, int mDay, int mHour, int mMin, int mSec) {
        time = String.format("%d-%d-%d %02d:%02d:%02d", mYear, mMonth, mDay, mHour, mMin, mSec);
        long date2mill = 0;
        try {
            date2mill = Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time)).getTime();
        }catch (ParseException e){
            e.printStackTrace();
        }
        isAvailable = date2mill > System.currentTimeMillis();
    }
}
