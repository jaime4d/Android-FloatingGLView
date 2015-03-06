package local.sample.viewservice;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * Moving window handler interface.
 */
interface WindowHandler {
    /*
    public enum WindowStateEnum {
        unknown,
        invisible,
        maximized,
        minimized,
    }

    public void setWindowState(WindowStateEnum state);

    public WindowStateEnum getWindowState();
    */
    public void setPosition(Point loc);

    public Point getPosition();

    public Rect getParentWindowRect();
}
