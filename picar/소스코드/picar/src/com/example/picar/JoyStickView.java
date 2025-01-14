package com.example.picar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class JoyStickView {
    public static final int STICK_NONE = 0;
    public static final int STICK_UP = 1;
    public static final int STICK_UPRIGHT = 2;
    public static final int STICK_RIGHT = 3;
    public static final int STICK_RIGHTDOWN = 4;
    public static final int STICK_DOWN = 5;
    public static final int STICK_DOWNLEFT = 6;
    public static final int STICK_LEFT = 7;
    public static final int STICK_LEFTUP = 8; 
    
    private int STICK_ALPHA = 200;
    private int LAYOUT_ALPHA = 200;
    private int OFFSET = 0;
    
    private Context mContext;
    private ViewGroup mLayout;
    private LayoutParams params;
    private int stick_width, stick_height;
    
    private int position_x = 0, position_y = 0, min_distance = 0, max_distance = 0;
    private float distance = 0, angle = 0;
    
    private DrawCanvas draw;
    private Paint paint;
    private Bitmap stick;
    
    private boolean touch_state = false;
    
    public JoyStickView (Context context, ViewGroup layout, int stick_res_id) {
        mContext = context;

        stick = BitmapFactory.decodeResource(mContext.getResources(), stick_res_id);
        
        stick_width = stick.getWidth();
        stick_height = stick.getHeight();
        
        draw = new DrawCanvas(mContext);
        paint = new Paint();
        mLayout = layout;
        params = mLayout.getLayoutParams();
    }
    
    public void drawStick() {
    	max_distance = params.width / 2 - OFFSET;
    	draw.position(params.width/2, params.height/2);
    	draw();
    }
    
    public void drawStick(final MotionEvent arg1) {
    	draw.clearAnimation();
        position_x = (int) (arg1.getX() - (params.width / 2));
        position_y = (int) (arg1.getY() - (params.height / 2));
        distance = (float) Math.sqrt(Math.pow(position_x, 2) + Math.pow(position_y, 2));
        angle = (float) cal_angle(position_x, position_y);
        
        if(arg1.getAction() == MotionEvent.ACTION_DOWN) {
            if(distance <= max_distance) {
                draw.position(arg1.getX(), arg1.getY());
                draw();
            } else if(distance > max_distance){
                float x = (float) (Math.cos(Math.toRadians(cal_angle(position_x, position_y))) 
                        * (max_distance));
                float y = (float) (Math.sin(Math.toRadians(cal_angle(position_x, position_y))) 
                        * (max_distance));
                x += (params.width / 2);
                y += (params.height / 2);
                draw.position(x, y);
                draw();
            }
            touch_state = true;
        } else if(arg1.getAction() == MotionEvent.ACTION_MOVE && touch_state) {
            if(distance <= max_distance) {
                draw.position(arg1.getX(), arg1.getY());
                draw();
            } else if(distance > max_distance){
                float x = (float) (Math.cos(Math.toRadians(cal_angle(position_x, position_y))) 
                        * (max_distance));
                float y = (float) (Math.sin(Math.toRadians(cal_angle(position_x, position_y))) 
                        * (max_distance));
                x += (params.width / 2);
                y += (params.height / 2);
                draw.position(x, y);
                draw();
            } else {
            	mLayout.removeView(draw);
            }
        } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
    		touch_state = false;
    		
    		// 애니메이션
    		Animation animation = null;
    		if(distance <= max_distance) {
    			animation = new TranslateAnimation(0, -position_x, 0, -position_y);
    		} else if(distance > max_distance){
    			float x = (float) (Math.cos(Math.toRadians(cal_angle(position_x, position_y))) * (max_distance));
    			float y = (float) (Math.sin(Math.toRadians(cal_angle(position_x, position_y))) * (max_distance));
    			animation = new TranslateAnimation(0, -x, 0, -y);
    		}
    		animation.setDuration(500);
    		animation.setFillAfter(true);
    		draw.startAnimation(animation);
        }
    }
    
    public int[] getPosition() {
        if(distance > min_distance && touch_state) {
            return new int[] { position_x, position_y };
        }
        return new int[] { 0, 0 };
    }
    
    public int getX() {
        if(distance > min_distance && touch_state) {
        	if (position_x > max_distance) {
        		return max_distance;
        	} else if (position_x < -(max_distance)) {
        		return -(max_distance);
        	}
    		return position_x;
        }
        return 0;
    }
    
    public int getY() {
        if(distance > min_distance && touch_state) {
        	if (position_y > max_distance) {
        		return max_distance;
        	} else if (position_y < -(max_distance)) {
        		return -(max_distance);
        	}
            return position_y;
        }
        return 0;
    }
    
    public float getAngle() {
        if(distance > min_distance && touch_state) {
            return angle;
        }
        return 0;
    }
    
    public float getDistance() {
        if(distance > min_distance && touch_state) {
        	if (distance > max_distance) {
        		return max_distance;
        	}
        	return distance;
        }
        return 0;
    }
    
    public int getDistancePercentage() {
        if(distance > min_distance && touch_state) {
        	if (distance > max_distance) {
        		return 100;
        	}
        	return Math.round(distance / (max_distance) * 100);
        }
    	return 0;
    }
    
    public void setMinimumDistance(int minDistance) {
        min_distance = minDistance;
    }
    
    public int getMinimumDistance() {
        return min_distance;
    }
    
    public int get8Direction() {
        if(distance > min_distance && touch_state) {
            if(angle >= 247.5 && angle < 292.5 ) {
                return STICK_UP;
            } else if(angle >= 292.5 && angle < 337.5 ) {
                return STICK_UPRIGHT;
            } else if(angle >= 337.5 || angle < 22.5 ) {
                return STICK_RIGHT;
            } else if(angle >= 22.5 && angle < 67.5 ) {
                return STICK_RIGHTDOWN;
            } else if(angle >= 67.5 && angle < 112.5 ) {
                return STICK_DOWN;
            } else if(angle >= 112.5 && angle < 157.5 ) {
                return STICK_DOWNLEFT;
            } else if(angle >= 157.5 && angle < 202.5 ) {
                return STICK_LEFT;
            } else if(angle >= 202.5 && angle < 247.5 ) {
                return STICK_LEFTUP;
            }
        } else if(distance <= min_distance && touch_state) {
            return STICK_NONE;
        }
        return 0;
    }
    
    public int get4Direction() {
        if(distance > min_distance && touch_state) {
            if(angle >= 225 && angle < 315 ) {
                return STICK_UP;
            } else if(angle >= 315 || angle < 45 ) {
                return STICK_RIGHT;
            } else if(angle >= 45 && angle < 135 ) {
                return STICK_DOWN;
            } else if(angle >= 135 && angle < 225 ) {
                return STICK_LEFT;
            }
        } else if(distance <= min_distance && touch_state) {
            return STICK_NONE;
        }
        return 0;
    }
    
    public void setOffset(int offset) {
        OFFSET = offset;
    }
    
    public int getOffset() {
        return OFFSET;
    }
    
    public void setStickAlpha(int alpha) {
        STICK_ALPHA = alpha;
        paint.setAlpha(alpha);
    }
    
    public int getStickAlpha() {
        return STICK_ALPHA;
    }
    
    public void setLayoutAlpha(int alpha) {
        LAYOUT_ALPHA = alpha;
        mLayout.getBackground().setAlpha(alpha);
    }
    
    public int getLayoutAlpha() {
        return LAYOUT_ALPHA;
    }
    
    public void setStickSize(int width, int height) {
        stick = Bitmap.createScaledBitmap(stick, width, height, false);
        stick_width = stick.getWidth();
        stick_height = stick.getHeight();
    }
    
    public void setStickWidth(int width) {
        stick = Bitmap.createScaledBitmap(stick, width, stick_height, false);
        stick_width = stick.getWidth();
    }
    
    public void setStickHeight(int height) {
        stick = Bitmap.createScaledBitmap(stick, stick_width, height, false);
        stick_height = stick.getHeight();
    }
    
    public int getStickWidth() {
        return stick_width;
    }
    
    public int getStickHeight() {
        return stick_height;
    }
    
    public void setLayoutSize(int width, int height) {
        params.width = width;
        params.height = height;
    }

    public int getLayoutWidth() {
        return params.width;
    }

    public int getLayoutHeight() {
        return params.height;
    }
    
    private double cal_angle(float x, float y) {
        if(x >= 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x));
        else if(x < 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if(x < 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if(x >= 0 && y < 0) 
            return Math.toDegrees(Math.atan(y / x)) + 360;
        return 0;
    }
     
    private void draw() {
        try {
            mLayout.removeView(draw);
        } catch (Exception e) { }
        mLayout.addView(draw);
    }
     
    private class DrawCanvas extends View{
         float x, y;
         
         private DrawCanvas(Context mContext) {
             super(mContext);
         }
         
         public void onDraw(Canvas canvas) {
             canvas.drawBitmap(stick, x, y, paint);
         }
         
         private void position(float pos_x, float pos_y) {
             x = pos_x - (stick_width / 2);
             y = pos_y - (stick_height / 2);
         }
     }
}