package moe.kmou424.WorldFlipper.Helper.Constants;

import android.graphics.Point;

import moe.kmou424.WorldFlipper.Helper.Info.BitmapInfo;

public class CoordinatePoints {
    // Pixel Dot
    // 准备界面: 左框位置
    public final static Point ROOM_PREPARE_STATUS_LEFT = new Point(55, 298);
    // 准备界面: 中框位置
    public final static Point ROOM_PREPARE_STATUS_CENTER = new Point(282, 285);
    // 准备界面: 右框位置
    public final static Point ROOM_PREPARE_STATUS_RIGHT = new Point(512, 298);
    // 主页: 铃铛位置
    public final static Point BELL_POINT_OUT = new Point(31, 35);
    public final static Point BELL_POINT_IN = new Point(55, 32);
    // 铃铛对话框: 参加按钮位置
    public final static Point BELL_JOIN = new Point(520, 1073);
    // 铃铛对话框: 不参加按钮位置
    public final static Point BELL_QUIT = new Point(200, 1073);
    // 准备界面: 准备完毕按钮位置
    public final static Point PREPARE_AS_GUEST_CHECKBOX = new Point(255, 923);
    // 结算界面: 继续按钮位置
    public final static Point BOTTOM_CONTINUE = new Point(360, 1200);
    // 结算界面: 离开房间按钮位置
    public final static Point BOTTOM_QUIT_ROOM = new Point(215, 1200);
    // 任意界面(对话框): 进入房间异常或队伍已解散时 确认按钮位置
    public final static Point ENTER_ROOM_FAILED = new Point(363, 802);
    // Bottom navigation
    // 底部导航栏: 主城按钮位置
    public final static Point BOTTOM_NAV_HOME_COLOR = new Point(220, 1260);

    // Bitmap Area
    public final static BitmapInfo LOADING_TEXT = new BitmapInfo(511, 1195, 208, 47);

    public final static BitmapInfo BELL_DIALOG_TITLE = new BitmapInfo(295, 51, 126, 38);
    public final static BitmapInfo BATTLE_FAILED_RESURRECTION_BUTTON_TITLE = new BitmapInfo(261, 1034, 194, 60);
    public final static BitmapInfo BOTTOM_CONTINUE_BUTTON_TITLE = new BitmapInfo(300, 1175, 127, 57);
    public final static BitmapInfo BOTTOM_QUIT_ROOM_BUTTON_TITLE = new BitmapInfo(137, 1175, 155, 57);
}
