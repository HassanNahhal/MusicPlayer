package com.conestogac.musicplayer.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.conestogac.musicplayer.R;

import java.io.File;

/*
    It is Util class to use Glide opensource which helps caching or decoding remote located images
    moreover it is east to use.

    http://google-opensource.blogspot.ca/2014/09/glide-30-media-management-library-for.html
*/
public class GlideUtil {
    public static void loadImageWithFilePath(File url, ImageView imageView) {
        Context context = imageView.getContext();
        Glide.with(context)
                .load(url)
                .crossFade()
                .override(ScreenUtil.getScreenSize(context).x/2,ScreenUtil.getScreenSize(context).x/2)
                .centerCrop()
                .into(imageView);
    }

    public static void loadImageWithUrl(String url, ImageView imageView) {
        Context context = imageView.getContext();
        Glide.with(context)
                .load(url)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }

    public static void loadProfileIcon(String url, ImageView imageView) {
        Context context = imageView.getContext();
        Glide.with(context)
                .load(Uri.parse(url))
                .placeholder(R.drawable.ic_library_music_white_48dp)
                .dontAnimate()
                .fitCenter()
                .into(imageView);
    }
}

