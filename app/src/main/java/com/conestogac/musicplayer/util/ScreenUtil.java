package com.conestogac.musicplayer.util;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * This class is for Util for Screen
 *
 * author Changho Choi
 */
public class ScreenUtil {
    private Context ctxt;
    static Point size = new Point();

    public static Point getScreenSize(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getSize(size);
        return size;
    }
}
