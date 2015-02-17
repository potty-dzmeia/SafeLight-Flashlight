package my.potty.flashlight;



import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

public class NormalFlashlight implements Flashlight 
{
	public static final int MAX_BRIGHTNESS = 255;
	public static final int MIN_BRIGHTNESS = 0;
	
	
	
	private boolean isNormalFlashlightEnabled;	
	private int     normalFlashlightBrightness; //The brightness of the flashlight: MIN_BRIGHTNESS - darkest; MAX_BRIGHTNESS - brightest
	private int     scrollVerticalScale;        // Value used for scaling vertical scrolls into brightness change
    private boolean isSreenTouched = false;     // True if the user is touching the screen at the moment
	

	public NormalFlashlight() 
	{
		super();	
		
		scrollVerticalScale        = 400; // Init the with some acceptable value (later we canvasHeigh will be used)
		isNormalFlashlightEnabled  = false;
		normalFlashlightBrightness = MAX_BRIGHTNESS;
	}
	
	
	
	@Override
	public synchronized void draw(Canvas canvas)
	{	
	    scrollVerticalScale = canvas.getHeight();
	    
		if(isNormalFlashlightEnabled == false)
			canvas.drawColor(Color.BLACK);
		else
			canvas.drawColor(Color.rgb(normalFlashlightBrightness, normalFlashlightBrightness, normalFlashlightBrightness));
	}

	
	
	@Override
	/**
	 * // Switches ON/OFF the flashlight
	 */
	public synchronized boolean doubleTap() 
	{		
		isNormalFlashlightEnabled = !isNormalFlashlightEnabled;
		
		return true;
	}

	
	
	@Override
	public synchronized boolean motionEvent(MotionEvent event) 
	{
	    if(event.getAction() == MotionEvent.ACTION_DOWN)
            isSreenTouched = true;
        else if(event.getAction() == MotionEvent.ACTION_UP)
            isSreenTouched = false;
        
		return false;
	}


	
	@Override
	public synchronized boolean scroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
	{
		if(distanceY == 0)
			return false;
		if(isNormalFlashlightEnabled == false)
            return false;
		
		// The amount of scroll is scaled by the resolution of the display so that
		// when the user scroll from top to bottom we will have brightness change of MAX to MIN
		float brightnessDelta = distanceY * ((float)(MAX_BRIGHTNESS-MIN_BRIGHTNESS)/scrollVerticalScale);
				
		normalFlashlightBrightness = normalFlashlightBrightness + Math.round(brightnessDelta);
			
		// Make sure we don't get above the limit...
		if(normalFlashlightBrightness > MAX_BRIGHTNESS)
			normalFlashlightBrightness = MAX_BRIGHTNESS;
		if(normalFlashlightBrightness < MIN_BRIGHTNESS)
			normalFlashlightBrightness = MIN_BRIGHTNESS;		
				
		return true;
	}
				


    @Override
    public synchronized void saveUserPreferences(SharedPreferences preferences)
    {
        SharedPreferences.Editor editor = preferences.edit();
        // Save the settings to file
        editor.putBoolean("isNormalFlashlightEnabled", isNormalFlashlightEnabled);
        editor.putInt("normalFlashlightBrightness", normalFlashlightBrightness); 
        editor.commit();
        
        
    }



    @Override
    public synchronized void loadUserPreferences(SharedPreferences preferences)
    {
        // Restore user preferences        
        isNormalFlashlightEnabled  = preferences.getBoolean("isNormalFlashlightEnabled", false);  
        normalFlashlightBrightness = preferences.getInt("normalFlashlightBrightness", MAX_BRIGHTNESS);  
    }



    @Override
    public String getFeedbackInfo()
    {
        if(isNormalFlashlightEnabled == false)
        {
            return "Normal mode\nDouble tap to switch ON!";
        }
        // IF the flashlight is ON
        else
        {
            // and the user is touching the screen we will display the current brightness (scaled to 100)
            if(isSreenTouched)
            {
                return "Brightness = " +  (normalFlashlightBrightness-MIN_BRIGHTNESS)*100/(MAX_BRIGHTNESS-MIN_BRIGHTNESS);
            }
        }
        
        return "";
    }
	
		
}
