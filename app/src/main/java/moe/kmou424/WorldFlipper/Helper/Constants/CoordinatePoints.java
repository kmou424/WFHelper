package moe.kmou424.WorldFlipper.Helper.Constants;

import android.graphics.Point;

import moe.kmou424.WorldFlipper.Helper.Info.BitmapInfo;

public class CoordinatePoints {
    // Pixel Dot
    // 主页: 关闭公告按钮位置
    public final static Point NOTICE_CLOSE_BUTTON = new Point(356, 1177);
    // [通用]编队界面: 队伍小圆点位置
    public final static Point[] EDIT_TEAM_INDICATOR_DOTS = {
            new Point(263, 85), //队伍A
            new Point(285, 85), //队伍B
            new Point(307, 85), //队伍C
            new Point(328, 85), //队伍D
            new Point(349, 85), //队伍E
            new Point(370, 85), //队伍F
            new Point(392, 85), //队伍G
            new Point(413, 85), //队伍H
            new Point(435, 85), //队伍I
            new Point(456, 85), //队伍J
    };
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
    // 准备界面: 准备界面编队按钮位置
    public final static Point PREPARE_EDIT_TEAM_BUTTON = new Point(652, 140);
    // 准备界面: 编队界面确认按钮位置
    public final static Point PREPARE_EDIT_TEAM_OK = new Point(52, 1230);
    // 结算界面: 继续按钮位置
    public final static Point BOTTOM_CONTINUE = new Point(360, 1200);
    // 结算界面: 离开房间按钮位置
    public final static Point BOTTOM_QUIT_ROOM = new Point(215, 1200);
    // 任意界面(对话框): 进入房间异常或队伍已解散时 确认按钮位置
    public final static Point DIALOG_BUTTON = new Point(363, 802);
    // Bottom navigation
    // 底部导航栏: 主城按钮位置
    public final static Point BOTTOM_NAV_HOME_COLOR = new Point(220, 1260);

    // Bitmap Area
    public final static BitmapInfo LOADING_TEXT = new BitmapInfo(511, 1195, 208, 47);
    // 对话框内容
    public final static BitmapInfo DIALOG_MESSAGE = new BitmapInfo(95, 436, 530, 285);

    // 铃铛对话框标题
    public final static BitmapInfo BELL_DIALOG_TITLE = new BitmapInfo(295, 51, 126, 38);
    // 铃铛对话框Boss信息
    public final static BitmapInfo BELL_DIALOG_BOSS_INFO = new BitmapInfo(226, 334, 304, 49);
    // 战斗中时，战败后显示的复活按钮
    public final static BitmapInfo BATTLE_FAILED_RESURRECTION_BUTTON_TITLE = new BitmapInfo(261, 1034, 194, 60);
    // 结算时的继续按钮
    public final static BitmapInfo BOTTOM_CONTINUE_BUTTON_TITLE = new BitmapInfo(300, 1175, 127, 57);
    // 结算时的退出房间按钮
    public final static BitmapInfo BOTTOM_QUIT_ROOM_BUTTON_TITLE = new BitmapInfo(137, 1175, 155, 57);

    public final static BitmapInfo BOSS_LIST_FIRST = new BitmapInfo(203, 438, 179, 34);
}
