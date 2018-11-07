package com.taher.qatifedu.utility;

import java.util.ArrayList;

import com.taher.qatifedu.entity.Ads_Entity;
import com.taher.qatifedu.fragments.Ads_Details;
import com.taher.qatifedu.fragments.Ads_Details_WithID;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;

public class WebView_AdsDetails_WithID extends WebView {
    private boolean flinged;
    private static final int SWIPE_MIN_DISTANCE = 320;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    Ads_Details_WithID Fragment;
    public WebView_AdsDetails_WithID(Context context, AttributeSet attrs) {
        super(context, attrs);
        gd = new GestureDetector(context, sogl);
    }
    
    public void setFragment(Ads_Details_WithID  Fragment){
    	this.Fragment = Fragment;
    } 
  
    


    GestureDetector gd;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
         gd.onTouchEvent(event);
         if (flinged) {
             flinged = false;
             return true;
         } else {
             return super.onTouchEvent(event);
         }
    }

    GestureDetector.SimpleOnGestureListener sogl = new GestureDetector.SimpleOnGestureListener() {
    // your fling code here
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            if (event1.getX() < 1200 && event1.getX() > 80) {
                return false;
            }
            if (Math.abs(event1.getY() - event1.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            if(event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            	Fragment.onSwipeRight() ;
                flinged = true;
            } else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            	Fragment.onSwipeLeft() ;
                flinged = true;
            }
            return true;
        }
    };

}
