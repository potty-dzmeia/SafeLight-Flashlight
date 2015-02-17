package my.potty.flashlight;

public enum MorseCode 
{
	/**
	 * Relative length of the dot
	 */
	DOT(1),
	
	/**
	 * Relative length of the dash
	 */
	DASH(3),
	
	/**
	 * Relative length of the inter-element gap between the dots and dashes within a character 
	 */
	INTRA_GAP(1),
	
	/**
	 * Relative length of the gap between the characters
	 */
	CHAR_GAP(3),
	
	/**
	 * Relative length of the gap between the words
	 */
	WORD_GAP(7);
	
	
	private int value;
	
	private MorseCode(int value) 
	{
		this.value =value;
	}
	
	
	/** Returns the relative length of the information element.
	 *  For example for "DOT" this function will return 1; 
	 * 
	 * @return
	 */
	public int getValue()
	{
		return value;
	}

}
