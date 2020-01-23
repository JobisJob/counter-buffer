package com.jobisjob.counterbuffer.impl;

public class TextIntKey implements Comparable<TextIntKey> {

	String text;
	int intValue;
	
	public TextIntKey(String text, int intValue) {
	    this.text = text;
	    this.intValue = intValue;
    }
	
	
	public int compareTo( TextIntKey o ) {
	    int v = text.compareTo( o.text );
	    if (v == 0){
	    	return intValue < o.intValue ? -1 : (intValue == o.intValue ? 0 : 1);
	    }else{
	    	return v;
	    }
	}
	
	@Override
	public boolean equals( Object obj ) {
	    return compareTo( (TextIntKey)obj ) ==  0;
	}
	

	@Override
	public int hashCode() {
	    return 100 * text.hashCode() + intValue;
	}


	public String getText() {
		return text;
	}


	public int getIntValue() {
		return intValue;
	}
	
    @Override
    public String toString() {
        return "[" + text + "-" + intValue + "]";
    }
	
}
