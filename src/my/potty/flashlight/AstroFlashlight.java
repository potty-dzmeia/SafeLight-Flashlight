package my.potty.flashlight;



import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

public class AstroFlashlight implements Flashlight {
		
	public static final int MAX_BRIGHTNESS = 255;
	public static final int MIN_BRIGHTNESS = 0;
	

	/** false - is OFF; true  - is ON*/
	private boolean isAstroFlashlightEnabled;	
	/** The brightness of the flashlight: MIN_BRIGHTNESS - darkest; MAX_BRIGHTNESS - brightest */
	private int 	astroFlashlightBrightness;
	private int     scrollVerticalScale;
	private boolean isSreenTouched = false;     // True if the user is touching the screen at the moment
	

	

	public AstroFlashlight() 
	{
		super();	
		
		scrollVerticalScale       = 400; // Init the with some acceptable value (later we canvasHeigh will be used)
		isAstroFlashlightEnabled  = false; 
		astroFlashlightBrightness = MAX_BRIGHTNESS;
	}
	
	
	
	@Override
	public synchronized void draw(Canvas canvas)
	{	
	    scrollVerticalScale = canvas.getHeight();
	    
		if(isAstroFlashlightEnabled == false)
		{
			canvas.drawColor(Color.BLACK);
		}
		else
		{
			canvas.drawColor(Color.rgb(astroFlashlightBrightness, 0, 0));
		}
	}

	
	
	@Override
	/**
	 * // Switches ON/OFF the flashlight
	 */
	public synchronized boolean doubleTap() 
	{		
		isAstroFlashlightEnabled = !isAstroFlashlightEnabled;
		
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
	/**
	 *  Changes the brightness of the flashlight
	 */
	public synchronized boolean scroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
	{
		if(distanceY ==0)
			return false;
		if(isAstroFlashlightEnabled == false)
            return false;
		
		// The amount of scroll is scaled by the resolution of the display so that
		// when the user scroll from top to bottom we will have brightness change of MAX to MIN
		float brightnessDelta = distanceY * ((float)MAX_BRIGHTNESS/scrollVerticalScale);
				
		astroFlashlightBrightness = astroFlashlightBrightness + Math.round(brightnessDelta);
			
		// Make sure we don't get above the limit...
		if(astroFlashlightBrightness > MAX_BRIGHTNESS)
			astroFlashlightBrightness = MAX_BRIGHTNESS;
		if(astroFlashlightBrightness < MIN_BRIGHTNESS)
			astroFlashlightBrightness = MIN_BRIGHTNESS;		
				
		return true;
	}
				
	
	


    @Override
    public synchronized void saveUserPreferences(SharedPreferences preferences)
    {
        SharedPreferences.Editor editor = preferences.edit();
        // Save the settings to file
        editor.putBoolean("isAstroFlashlightEnabled", isAstroFlashlightEnabled);
        editor.putInt("astroFlashlightBrightness", astroFlashlightBrightness); 
        editor.commit();
    }



    @Override
    public synchronized void loadUserPreferences(SharedPreferences preferences)
    {
        // Restore user preferences     
        isAstroFlashlightEnabled  = preferences.getBoolean("isAstroFlashlightEnabled", false);  
        astroFlashlightBrightness = preferences.getInt("astroFlashlightBrightness", MAX_BRIGHTNESS);  
    }



    @Override
    public synchronized String getFeedbackInfo()
    { 
        if(isAstroFlashlightEnabled == false)
        {
            return "Astro mode\nDouble tap to switch ON!";
        }
        // IF the flashlight is ON
        else
        {
            // and the user is touching the screen we will display the current brightness (scaled to 100)
            if(isSreenTouched)
            {
                return "Brightness = " + (astroFlashlightBrightness-MIN_BRIGHTNESS)*100/(MAX_BRIGHTNESS-MIN_BRIGHTNESS);
            }
        }
        
        return "";
    }
	
}
