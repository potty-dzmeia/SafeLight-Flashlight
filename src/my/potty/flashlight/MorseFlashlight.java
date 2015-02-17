package my.potty.flashlight;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

public class MorseFlashlight implements Flashlight 
{
	/**
	 * True if the user is touching the screen at the moment
	 */
	private boolean isSreenTouched = false;


	
	@Override
	/**
	 *  Event is ignored
	 */
	public synchronized boolean doubleTap() 
	{
		return false;
	}

	
	
	@Override
	public synchronized void draw(Canvas canvas) 
	{
		if(isSreenTouched)
			canvas.drawColor(Color.WHITE);
		else
			canvas.drawColor(Color.BLACK);

	}

	
	
	@Override
	public synchronized boolean motionEvent(MotionEvent event) 
	{
	
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			isSreenTouched = true;
			return true;
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			isSreenTouched = false;
			return true;
		}
		
		return false;
	}

	
	
	@Override
	/**
	 *  Event is ignored
	 */
	public synchronized boolean scroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) 
	{
		return false;
	}
	



    @Override
    public synchronized void saveUserPreferences(SharedPreferences preferences)
    {
       // nothing to save
    }



    @Override
    public synchronized void loadUserPreferences(SharedPreferences preferences)
    {
        // nothing to load
    }



    @Override
    public String getFeedbackInfo()
    {
        return "";
    }

}

