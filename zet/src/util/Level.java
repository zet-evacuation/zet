package util;

public enum Level {

	HIGHER(),
	EQUAL(),
	LOWER(HIGHER);
	
    private Level inverseLevel;
	
    private Level(){
    	this.inverseLevel = this;
    }
	
	private Level(Level inverseLevel){
        this.inverseLevel = inverseLevel;
        inverseLevel.setInverse(this);
    }
	
	private void setInverse(Level inverseLevel){
		this.inverseLevel = inverseLevel;
	}
	
	public Level getInverse(){
		return inverseLevel;
	}
	
}
