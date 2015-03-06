package local.sample.viewservice;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * Moving window handler interface.
 */
interface WindowHandler {

    public void setPosition(Point loc);

    public Point getPosition();

    public Rect getParentWindowRect();
}
