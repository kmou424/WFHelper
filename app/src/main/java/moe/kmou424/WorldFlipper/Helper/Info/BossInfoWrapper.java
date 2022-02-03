package moe.kmou424.WorldFlipper.Helper.Info;

import java.util.ArrayList;

import moe.kmou424.WorldFlipper.Helper.MainActivity;

public class BossInfoWrapper {
    public DateInfo mEndDateInfo;
    public String BossTag;
    public String BossEnabledConf, BossLevelsConf, BossTeamConf;
    public int BossNameResId;
    public ArrayList<String> mBossAvailableLevels;

    public BossInfoWrapper(DateInfo mEndDateInfo, String BossTag, String BossEnabledConf, String BossLevelsConf, String BossTeamConf, int BossNameResId, ArrayList<String> mBossAvailableLevels) {
        this.mEndDateInfo = mEndDateInfo;
        this.BossTag = BossTag;
        this.BossEnabledConf = BossEnabledConf;
        this.BossLevelsConf = BossLevelsConf;
        this.BossTeamConf = BossTeamConf;
        this.BossNameResId = BossNameResId;
        this.mBossAvailableLevels = mBossAvailableLevels;
    }

    public static String[] getBossLevels(String mBossLevelConf) {
        String mBossLevel = MainActivity.mSharedPreferences.getString(mBossLevelConf, "");
        return mBossLevel.split(":");
    }
}
