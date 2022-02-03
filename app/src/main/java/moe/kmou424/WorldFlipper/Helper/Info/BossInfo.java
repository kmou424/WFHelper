package moe.kmou424.WorldFlipper.Helper.Info;

public class BossInfo {
    public String mName, mTeam;
    public Boolean isEnabled;
    public String[] mLevels;

    public BossInfo(String mName, Boolean isEnabled, String[] mLevels, String mTeam) {
        this.mName = mName;
        this.isEnabled = isEnabled;
        this.mLevels = mLevels;
        this.mTeam = mTeam;
    }
}
