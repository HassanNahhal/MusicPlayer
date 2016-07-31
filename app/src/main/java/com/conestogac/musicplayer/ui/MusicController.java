package com.conestogac.musicplayer.ui;

import android.content.Context;
import android.widget.MediaController;

/**
 * presents a standard widget with play/pause, rewind, fast-forward,
 * and skip (previous/next) buttons in it.
 * The widget also contains a seek bar, which updates as the song plays
 * and contains text indicating the duration of the song and the player's current position
 * @author Changho Choi
 */
public class MusicController extends MediaController {

    public MusicController(Context ctxt){
        super(ctxt);
    }

    public void hide(){}

}
