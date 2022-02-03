package moe.kmou424.WorldFlipper.Helper.Constants;

import static moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import moe.kmou424.WorldFlipper.Helper.Info.BossInfoWrapper;
import moe.kmou424.WorldFlipper.Helper.Info.DateInfo;
import moe.kmou424.WorldFlipper.Helper.R;

public class Global {
    public final static String WORLD_FLIPPER_PACKAGE_NAME_LT_SERVER = "com.leiting.wf";
    public final static String SHARED_PREFERENCES_NAME = "wfhelper";

    public final static int MOVE_FLOATING_WINDOW_SENSITIVITY = 15;
    public final static int NO_TASK = 0;

    public final static HashMap<Integer, String> TASKS = new HashMap<>() {{
        put(NO_TASK, "无任务");
        put(GO_RE_LOGIN, "重新登录");
        put(GO_RE_LOGIN_DELAY, "延时重新登录");
        put(GO_BELL, "进入铃铛事件");
        put(GO_PREPARE_AS_GUEST, "战斗前准备中");
        put(GO_WAITING_FOR_FINISH, "等待战斗结束");
        put(GO_MAIN_PAGE, "前往主城");
    }};

    private final static String BOSS_LEVEL_PRIMARY = CheckPoints.BOSS_LEVEL_PRIMARY;
    private final static String BOSS_LEVEL_MIDDLE = CheckPoints.BOSS_LEVEL_MIDDLE;
    private final static String BOSS_LEVEL_HIGH = CheckPoints.BOSS_LEVEL_HIGH;
    private final static String BOSS_LEVEL_HIGH_PLUS = CheckPoints.BOSS_LEVEL_HIGH_PLUS;
    private final static String BOSS_LEVEL_SUPER = CheckPoints.BOSS_LEVEL_SUPER;

    public final static BossInfoWrapper[] BOSS_INFO_WRAPPER_LIST = {
            new BossInfoWrapper(null,
                    "wf_boss_1", "wf_boss_1_enabled",
                    "wf_boss_1_levels", "wf_boss_1_team", R.string.wf_boss_name_1,
                    new ArrayList<>(Arrays.asList(BOSS_LEVEL_PRIMARY))),
            new BossInfoWrapper(null,
                    "wf_boss_2", "wf_boss_2_enabled",
                    "wf_boss_2_levels", "wf_boss_2_team", R.string.wf_boss_name_2,
                    new ArrayList<>(Arrays.asList(BOSS_LEVEL_MIDDLE, BOSS_LEVEL_HIGH, BOSS_LEVEL_HIGH_PLUS, BOSS_LEVEL_SUPER))),
            new BossInfoWrapper(null,
                    "wf_boss_3", "wf_boss_3_enabled",
                    "wf_boss_3_levels", "wf_boss_3_team", R.string.wf_boss_name_3,
                    new ArrayList<>(Arrays.asList(BOSS_LEVEL_MIDDLE, BOSS_LEVEL_HIGH, BOSS_LEVEL_HIGH_PLUS, BOSS_LEVEL_SUPER))),
            new BossInfoWrapper(null,
                    "wf_boss_4", "wf_boss_4_enabled",
                    "wf_boss_4_levels", "wf_boss_4_team", R.string.wf_boss_name_4,
                    new ArrayList<>(Arrays.asList(BOSS_LEVEL_MIDDLE, BOSS_LEVEL_HIGH))),
            new BossInfoWrapper(null,
                    "wf_boss_5", "wf_boss_5_enabled",
                    "wf_boss_5_levels", "wf_boss_5_team", R.string.wf_boss_name_5,
                    new ArrayList<>(Arrays.asList(BOSS_LEVEL_MIDDLE, BOSS_LEVEL_HIGH))),
            new BossInfoWrapper(null,
                    "wf_boss_6", "wf_boss_6_enabled",
                    "wf_boss_6_levels", "wf_boss_6_team", R.string.wf_boss_name_6,
                    new ArrayList<>(Arrays.asList(BOSS_LEVEL_MIDDLE, BOSS_LEVEL_HIGH, BOSS_LEVEL_HIGH_PLUS, BOSS_LEVEL_SUPER))),
            new BossInfoWrapper(null,
                    "wf_boss_7", "wf_boss_7_enabled",
                    "wf_boss_7_levels", "wf_boss_7_team", R.string.wf_boss_name_7,
                    new ArrayList<>(Arrays.asList(BOSS_LEVEL_MIDDLE, BOSS_LEVEL_HIGH))),
            new BossInfoWrapper(null,
                    "wf_boss_8", "wf_boss_8_enabled",
                    "wf_boss_8_levels", "wf_boss_8_team", R.string.wf_boss_name_8,
                    new ArrayList<>(Arrays.asList(BOSS_LEVEL_MIDDLE, BOSS_LEVEL_HIGH))),
            new BossInfoWrapper(null,
                    "wf_boss_9", "wf_boss_9_enabled",
                    "wf_boss_9_levels", "wf_boss_9_team", R.string.wf_boss_name_9,
                    new ArrayList<>(Arrays.asList(BOSS_LEVEL_MIDDLE, BOSS_LEVEL_HIGH))),
            new BossInfoWrapper(null,
                    "wf_boss_10", "wf_boss_10_enabled",
                    "wf_boss_10_levels", "wf_boss_10_team", R.string.wf_boss_name_10,
                    new ArrayList<>(Arrays.asList(BOSS_LEVEL_HIGH))),
            new BossInfoWrapper(new DateInfo(2022, 2, 10, 11, 59, 0),
                    "wf_boss_11", "wf_boss_11_enabled",
                    "wf_boss_11_levels", "wf_boss_11_team", R.string.wf_boss_name_11,
                    new ArrayList<>(Arrays.asList(BOSS_LEVEL_HIGH, BOSS_LEVEL_HIGH_PLUS))),
            new BossInfoWrapper(new DateInfo(2022, 2, 10, 11, 59, 0),
                    "wf_boss_12", "wf_boss_12_enabled",
                    "wf_boss_12_levels", "wf_boss_12_team", R.string.wf_boss_name_12,
                    new ArrayList<>(Arrays.asList(BOSS_LEVEL_HIGH, BOSS_LEVEL_SUPER)))
    };


    public final static String[] AvailableTeam = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    public final static Boolean DEBUG = true;
}