package moe.kmou424.WorldFlipper.Helper.Constants;

import static moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage.*;

import java.util.HashMap;

public class Global {
    public final static String WORLD_FLIPPER_PACKAGE_NAME_LT_SERVER = "com.leiting.wf";
    public final static String SHARED_PREFERENCES_NAME = "wfhelper";

    public final static int MOVE_FLOATING_WINDOW_SENSITIVITY = 15;
    public final static int NO_TASK = 0;

    public final static HashMap<Integer, String> TASKS = new HashMap<>() {{
        put(NO_TASK, "无任务");
        put(GO_BELL, "进入铃铛事件");
        put(GO_PREPARE_AS_GUEST, "战斗前准备中");
        put(GO_WAITING_FOR_FINISH, "等待战斗结束");
        put(GO_MAIN_PAGE, "前往主城");
    }};

    public final static Boolean DEBUG = true;
}