package my.potty.flashlight;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

public class SosFlashlight implements Flashlight
{
	private static final int MAX_DOT_DURATION_IN_MS = 1000;
	private static final int MIN_DOT_DURATION_IN_MS = 100;
	
	private boolean	isSosFlashlightEnabled;
	private int 	dotDuration;	  // The speed with which we are transmitting: from MAX_DOT_DURATION_IN_MS to MIN_DOT_DURATION_IN_MS
	private int	 	elementBeingSend; // The MorseCode element being transmitted from the sos[] array
	private long	mLastTime; 		  // The time at which we started transmitting the elementBeingSend
	private int     scrollVerticalScale; // For scaling the speed
	private boolean isSreenTouched = false;     // True if the user is touching the screen at the moment
	
	private MorseCode[] sos = 
	{/*S*/MorseCode.DOT, MorseCode.INTRA_GAP , MorseCode.DOT, MorseCode.INTRA_GAP, MorseCode.DOT, MorseCode.INTRA_GAP,
		  MorseCode.CHAR_GAP,
	 /*O*/MorseCode.DASH, MorseCode.INTRA_GAP, MorseCode.DASH, MorseCode.INTRA_GAP, MorseCode.DASH, MorseCode.INTRA_GAP,
	      MorseCode.CHAR_GAP,
	 /*S*/MorseCode.DOT, MorseCode.INTRA_GAP , MorseCode.DOT, MorseCode.INTRA_GAP, MorseCode.DOT, MorseCode.INTRA_GAP,
	 	  MorseCode.WORD_GAP};
	
	
	

	public SosFlashlight() 
	{
	    scrollVerticalScale    = 400; // Init the with some acceptable value (later we canvasHeigh will be used)
	    dotDuration 		   = (MAX_DOT_DURATION_IN_MS+MIN_DOT_DURATION_IN_MS)/2; 
		isSosFlashlightEnabled = false; 
		
		startSendingFromBeginning();
	}
	
	
	
	@Override
	public synchronized boolean doubleTap() 
	{
		isSosFlashlightEnabled = !isSosFlashlightEnabled;
		startSendingFromBeginning();
		
		return true;
	}
	
	

	@Override
	public synchronized void draw(Canvas canvas) 
	{
	    scrollVerticalScale = canvas.getHeight();
	    
	    
		// If we have sent the current information element - we should move to the next
		if(System.currentTimeMillis() > (mLastTime+getElementLengthInMs(sos[elementBeingSend])) )
		{
			// move to next element
			elementBeingSend++; 					
			
			// Start from beginning when all the "SOS" is sent
			if(elementBeingSend >= sos.length)
				elementBeingSend = 0;
			
			// mark the start of  the information element
			mLastTime = System.currentTimeMillis(); 
		}
		
		
		
		// If the flashlight is ON...
		if(isSosFlashlightEnabled)
			// draw the current element
			canvas.drawColor(getElementColor(sos[elementBeingSend]));
		else
			canvas.drawColor(Color.BLACK);

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
		if(isSosFlashlightEnabled == false)
            return false;
		
		// Scale the amount of scroll to MAX_BLINKING_SPEED
		float delta = distanceY * ((float)(MAX_DOT_DURATION_IN_MS-MIN_DOT_DURATION_IN_MS)/scrollVerticalScale);
		
		dotDuration += Math.round(delta);
		
	
		  // Make sure we don't go outside limits...
		if(dotDuration > MAX_DOT_DURATION_IN_MS)
			dotDuration = MAX_DOT_DURATION_IN_MS;
		else if(dotDuration < MIN_DOT_DURATION_IN_MS)
			dotDuration = MIN_DOT_DURATION_IN_MS;
		
		
		return true;
	}
	


    @Override
    public synchronized void saveUserPreferences(SharedPreferences preferences)
    {
        // Save the settings to file
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("sosSpeed", dotDuration);
        editor.putBoolean("isSosFlashlightEnabled", isSosFlashlightEnabled);
        editor.commit();
    }



    @Override
    public synchronized void loadUserPreferences(SharedPreferences preferences)
    {
        dotDuration            = preferences.getInt("sosSpeed", (MAX_DOT_DURATION_IN_MS + MIN_DOT_DURATION_IN_MS) / 2);
        isSosFlashlightEnabled = preferences.getBoolean("isSosFlashlightEnabled", false);
    }
	
	
	
	/** Returns the transmission duration of the specified MorseCode element
	 * 
	 * @param element - the element that we are interested in
	 * @return - length in [msec]
	 */
	private int getElementLengthInMs(MorseCode element)
	{
		return element.getValue()*dotDuration;
	}
	
	
	
	/** Return the color that is used to visualize a given MorseCode element.
	 *  For example for DOT the function will return white and for INTRA_GAP 
	 *  the function will return black.
	 * 
	 * @param element - the element that we are interested in
	 * @return - the color that should be used when transmitting the given element
	 */
	private int getElementColor(MorseCode element)
	{
		switch(element)
		{
		case DOT:	
		case DASH:
			return Color.WHITE;
			
		default:
			return Color.BLACK;
		}					
	}
	
	
	private void startSendingFromBeginning()
	{
		elementBeingSend = 0;
		mLastTime		 = System.currentTimeMillis();
	}



    @Override
    public synchronized String getFeedbackInfo()
    {
        if(isSosFlashlightEnabled == false)
        {
            return "SOS mode\nDouble tap to switch ON!";
        }
        // IF the flashlight is ON
        else
        {
            // and the user is touching the screen we will display the current speed (scaled to 100)
            if(isSreenTouched)
            {
                return "Dot length = " + (dotDuration-MIN_DOT_DURATION_IN_MS)*100/(MAX_DOT_DURATION_IN_MS-MIN_DOT_DURATION_IN_MS);
            }
        }
        
        
        return "";
    }
}
	
	
	
	
	
	
	
	
	
