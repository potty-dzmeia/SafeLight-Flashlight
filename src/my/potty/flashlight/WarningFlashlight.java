package my.potty.flashlight;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

public class WarningFlashlight implements Flashlight
{
	public static final int MAX_BLINKING_SPEED = 1000;
	public static final int MIN_BLINKING_SPEED = 0;
	
	
	private boolean isWarningFlashlightEnabled;	
	/** Used to figure out elapsed time between blinks */
    private long mLastTime;
    /** Variable indicating the current status of the warning light: true - is ON; false - is OFF */ 
	boolean bOn;
	/** Relative value of how fast is the warning light blinking:
	 *  From MAX_BLINKING_SPEED to MIN_BLINKING_SPEED
	 */
	private int 	blinkingSpeed;
	private Bitmap  offscreenBitmap_Black;
	private Bitmap  offscreenBitmap_Yellow;
	private int     scrollVerticalScale;
	private boolean isSreenTouched = false;     // True if the user is touching the screen at the moment
	
	
	public WarningFlashlight()
	{
	    scrollVerticalScale        = 400; // Init the with some acceptable value (later we canvasHeigh will be used)
		isWarningFlashlightEnabled = false; 
		blinkingSpeed              = (MAX_BLINKING_SPEED-MIN_BLINKING_SPEED)/2;
		
		mLastTime = System.currentTimeMillis();
		bOn = false;
	}	
	
	

	@Override
	public synchronized boolean doubleTap() 
	{
		isWarningFlashlightEnabled = !isWarningFlashlightEnabled;
		return false;
	}

	
	
	@Override
	/**
	 *  Draws alternating yellow and black screens
	 */
	public synchronized void draw(Canvas canvas) 
	{        
	    scrollVerticalScale = canvas.getHeight();
	    
		// Init the off-screen surfaces if not done
		initSurfaces(canvas);
		
		
		// Check if it is time to toggle the warning light?
		if(System.currentTimeMillis() > (getBlinkingPeriodInMs()+mLastTime) )
		{
			// Switch state
			bOn = !bOn;		
			
			// Store the last time we toggled the light
			mLastTime = System.currentTimeMillis();
		}
			
		
		if(isWarningFlashlightEnabled == false)
		{
		    canvas.drawBitmap(offscreenBitmap_Black, 0, 0, null);
		    return;
		}
		
		// Draw the new state
		if(bOn)
			canvas.drawBitmap(offscreenBitmap_Yellow, 0, 0, null);
		else
			canvas.drawBitmap(offscreenBitmap_Black, 0, 0, null);

	}
	
	

	@Override
	/**
	 *  Event is ignored
	 */
	public synchronized boolean motionEvent(MotionEvent event) 
	{
	    if(event.getAction() == MotionEvent.ACTION_DOWN)
            isSreenTouched = true;
        else if(event.getAction() == MotionEvent.ACTION_UP)
            isSreenTouched = false;
        
		return false;
	}
	
	

	@Override
	/** Vertical scroll changes the blinking speed of the yellow light
	 * 
	 */
	public synchronized boolean scroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) 
	{
		if(distanceY ==0)
			return false;
		if(isWarningFlashlightEnabled == false)
		    return false;
		
		// Scale the amount of scroll to MAX_BLINKING_SPEED
		float blinkingSpeedDelta = distanceY * ((float)(MAX_BLINKING_SPEED-MIN_BLINKING_SPEED)/scrollVerticalScale);
		blinkingSpeed += Math.round(blinkingSpeedDelta);
		
	    // Make sure we don't go outside limits...
		if(blinkingSpeed > MAX_BLINKING_SPEED)
			blinkingSpeed = MAX_BLINKING_SPEED;
		else if(blinkingSpeed < MIN_BLINKING_SPEED)
			blinkingSpeed = MIN_BLINKING_SPEED;
		
		return true;
	}

	
	

    @Override
    public synchronized void saveUserPreferences(SharedPreferences preferences)
    {
        SharedPreferences.Editor editor = preferences.edit();
        // Save the settings to file
        editor.putInt("blinkingSpeed", blinkingSpeed);
        editor.putBoolean("isWarningFlashlightEnabled", isWarningFlashlightEnabled);
        editor.commit();
    }



    @Override
    public synchronized void loadUserPreferences(SharedPreferences preferences)
    {
        isWarningFlashlightEnabled = preferences.getBoolean("isWarningFlashlightEnabled", false);  
        blinkingSpeed              = preferences.getInt("blinkingSpeed", (MAX_BLINKING_SPEED-MIN_BLINKING_SPEED)/2);
    }
	    
	    

    @Override
    public synchronized String getFeedbackInfo()
    {
        if(isWarningFlashlightEnabled == false)
        {
            return "Warning mode\nDouble tap to switch ON!";
        }
        // IF the flashlight is ON
        else
        {
            // and the user is touching the screen we will display the current blinking speed (scaled to 100)
            if(isSreenTouched)
            {
                return "Blinking period = " + (blinkingSpeed-MIN_BLINKING_SPEED)*100/(MAX_BLINKING_SPEED-MIN_BLINKING_SPEED);
            }
        }
        
        return "";
    }


	
	
	/** Inits the offscreenBitmap_Black and offscreenBitmap_Yellow
	 * 
	 * @param canvas
	 */
	private void initSurfaces(Canvas canvas)
	{
		// If the surfaces are not initiated - init for the first time
		if(offscreenBitmap_Black == null)
		{
			// Create the BLACK bitmap
			offscreenBitmap_Black = Bitmap.createBitmap(canvas.getWidth(), 
													  	canvas.getHeight(), 
													  	Bitmap.Config.ARGB_8888);
			Canvas tempCanvas = new Canvas(offscreenBitmap_Black);
			tempCanvas.drawColor(Color.BLACK);
			
			// Create the YELLOW bitmap
			offscreenBitmap_Yellow = Bitmap.createBitmap(canvas.getWidth(), 
														 canvas.getHeight(), 
														 Bitmap.Config.ARGB_8888);
			tempCanvas.setBitmap(offscreenBitmap_Yellow);
			tempCanvas.drawColor(Color.YELLOW);

		}
		// They are inited but size has changed - init again
		else if(offscreenBitmap_Black.getWidth() != canvas.getWidth())
		{
			// Create the BLACK bitmap
			offscreenBitmap_Black = Bitmap.createBitmap(canvas.getWidth(), 
													  	canvas.getHeight(), 
													  	Bitmap.Config.ARGB_8888);
			Canvas tempCanvas = new Canvas(offscreenBitmap_Black);
			tempCanvas.drawColor(Color.BLACK);
			
			// Create the YELLOW bitmap
			offscreenBitmap_Yellow = Bitmap.createBitmap(canvas.getWidth(), 
														 canvas.getHeight(), 
														 Bitmap.Config.ARGB_8888);
			tempCanvas.setBitmap(offscreenBitmap_Yellow);
			tempCanvas.drawColor(Color.YELLOW);			
		}	
	
		//else do nothing
		
		
	} //initSurfaces() 
	
	
	
	/** Converts blinkingSpeed to Blinking period of the warning lights in milliseconds.
	 * 
	 *  for x = 0 y = 0
	 * Assumptions: max_blinking_seed is 1000; warningLightsCounter 
	 *  		
	 * 
	 * @return - Min is 0ms Max is 1500ms; Step is 30ms;
	 */
	private int getBlinkingPeriodInMs()
	{
		double convertedValue;
		
		//2.1496e-10  -2.6758e-07   1.0078e-04   1.4886e-03   3.0667e-01
		
		convertedValue = 2.1496e-10 *Math.pow(blinkingSpeed,4) + //a4*x^4 +
						-2.6758e-07 *Math.pow(blinkingSpeed,3) + //a3*x^3 +
						 1.0078e-04 *Math.pow(blinkingSpeed,2) + //a2*x^2 + 
						 1.4886e-03 * blinkingSpeed 		   + //a1*x^1 +
						 2.1496e-10; 						     //a0
				
		if(convertedValue < 0)
			convertedValue = 0;
		else if(convertedValue > 50)
			convertedValue = 50;
		
		return (int) Math.round(convertedValue)*30;
	}
  
}

