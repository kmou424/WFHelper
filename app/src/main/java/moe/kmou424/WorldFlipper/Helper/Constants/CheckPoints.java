package moe.kmou424.WorldFlipper.Helper.Constants;

import moe.kmou424.WorldFlipper.Helper.Info.ColorInfo;
import moe.kmou424.WorldFlipper.Helper.Info.RgbInfo;

public class CheckPoints {
    public final static String LOADING_TEXT = "请耐心等待";

    // Boss levels
    public final static String BOSS_LEVEL_PRIMARY = "初级";
    public final static String BOSS_LEVEL_MIDDLE = "中级";
    public final static String BOSS_LEVEL_HIGH = "高级";
    public final static String BOSS_LEVEL_HIGH_PLUS = "高级+";
    public final static String BOSS_LEVEL_SUPER = "超级";

    // 编队界面队伍指示小点激活时颜色
    public final static ColorInfo EDIT_TEAM_INDICATOR_DOT_ACTIVE_COLOR = new ColorInfo(
            new RgbInfo(0xff, 0x9a, 0x16), new RgbInfo(0xff, 0xa9, 0x26)
    );
    public final static ColorInfo ROOM_PREPARE_READY_COLOR = new ColorInfo(
            new RgbInfo(0xff, 0x9a, 0x16), new RgbInfo(0xff, 0xa9, 0x26)
    );
    public final static String BELL_DIALOG_TITLE = "关卡信息";
    public final static ColorInfo PREPARE_AS_GUEST_CHECKBOX_INACTIVATE_COLOR = new ColorInfo(
            new RgbInfo(0xde, 0xde, 0xde), new RgbInfo(0xe9, 0xe9, 0xe9)
    );
    public final static String BATTLE_FAILED_RESURRECTION_BUTTON_TITLE = "续战";
    public final static String BOTTOM_CONTINUE_BUTTON_TITLE = "继续";
    public final static String BOTTOM_QUIT_ROOM_BUTTON_TITLE = "离开房间";
    public final static ColorInfo DIALOG_BUTTON_COLOR = new ColorInfo(
            new RgbInfo(0x29, 0xbd, 0xb2), new RgbInfo(0x31, 0xc9, 0xbe)
    );
    public final static String DIALOG_MESSAGE_UPDATE_TIME = "日期变了";
    public final static String DIALOG_MESSAGE_POP_OFF_LOGIN = "您的账号已在其它设备登录";

    // Bottom navigation
    public final static ColorInfo BOTTOM_NAV_HOME_INACTIVATE_COLOR = new ColorInfo(
            new RgbInfo(0xfe, 0xf9, 0xf9), new RgbInfo(0xff, 0xfe, 0xff)
    );
    public final static ColorInfo BOTTOM_NAV_HOME_ACTIVATE_COLOR = new ColorInfo(
            new RgbInfo(0xff, 0x9e, 0x18), new RgbInfo(0xff, 0xa2, 0x21)
    );
}
