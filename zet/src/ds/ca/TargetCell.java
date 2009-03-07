package ds.ca;

public abstract class TargetCell extends Cell{

	public TargetCell(Individual individual, double speedFactor, int x, int y) {
		super(individual, speedFactor, x, y);
	}
	
	public TargetCell( Individual individual, double speedFactor, int x, int y, Room room ){
		super(individual, speedFactor, x, y, room);
	}
	
	public abstract String getName();
}
