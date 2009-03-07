/*
 * RasterContainerCreator.java
 *
 */
package converter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import localization.Localization;

import util.DebugFlags;

import ds.Project;
import ds.PropertyContainer;
import ds.graph.NetworkFlowModel;
import ds.z.BuildingPlan;
import ds.z.DelayArea;
import ds.z.EvacuationArea;
import ds.z.Floor;
import ds.z.InaccessibleArea;
import ds.z.PlanPoint;
import ds.z.Room;
import ds.z.RoomEdge;
import ds.z.SaveArea;
import ds.z.DelayArea.DelayType;

public class RasterContainerCreator {
    
    private static RasterContainerCreator instance = null;
    
    protected RasterContainerCreator(){        
    }
    
    public static RasterContainerCreator getInstance(){
        if(instance == null){
            instance = new RasterContainerCreator();
        }
        
        return instance;
    }
    
    public ZToGraphRasterContainer ZToGraphRasterContainer(BuildingPlan buildingPlan){
    	ZToGraphRasterContainer container = new ZToGraphRasterContainer();
    	List<Floor> floors = buildingPlan.getFloors();
    	container.setFloors(floors);
    	for (Floor floor : floors){
    		List<Room> rooms = floor.getRooms();
    		for (Room room : rooms){
    		    ZToGraphRoomRaster rasterer = new ZToGraphRoomRaster(room);
    		    rasterer.rasterize();
    		    container.setRoomRaster(room,rasterer);
    		}
    	}
    	
    	for(Floor floor : floors){
    		List<Room> rooms = floor.getRooms();
    	    for (Room room : rooms){
    		    ZToGraphRoomRaster rasterer = container.getRasteredRoom(room);
    		    saveListOfDoors(rasterer, container);
    		    if (DebugFlags.RASTER){
    		    	System.out.println("The creator created the raster for a room:");
    		    	System.out.println(rasterer.superToString());
    		    }
    		}
    	}

    	return container;
    }
    
    public ZToCARasterContainer ZToCARasterContainer(BuildingPlan buildingPlan){
        ZToCARasterContainer container = new ZToCARasterContainer();
        container.setFloors(buildingPlan.getFloors());
        for(Floor floor : buildingPlan.getFloors()){
            for(Room room : floor.getRooms()){
                ZToCARoomRaster raster = new ZToCARoomRaster(room);
                raster.rasterize();
                container.setRoomRaster(room, raster);
            }
        }
        
        for(Floor floor : buildingPlan.getFloors()){
            for(Room room : floor.getRooms()){
                for(ds.z.Edge edge : room.getEdges()){
                    RoomEdge rEdge = (RoomEdge)edge;
                    ZToCARoomRaster raster = container.getRasteredRoom(room);
                    if((rEdge).isPassable()){
                        List<ZToCARasterSquare> squares = util.ConversionTools.getSquaresAlongEdge(rEdge, raster);
                        //RoomEdge partnerEdge;
                        Room partnerRoom = rEdge.getLinkTarget ().getRoom ();
                        
//                        if(partnerEdge == null){
//                            throw new RuntimeException("Inconsistency found: There is a passible edge that does not lie in two rooms!");
//                        }
                        
                        ZToCARoomRaster partnerRaster = container.getRasteredRoom(partnerRoom);
                        List<ZToCARasterSquare> partnerSquares = util.ConversionTools.getSquaresAlongEdge(rEdge.getLinkTarget(), partnerRaster);
                        
                        Iterator<ZToCARasterSquare> myIt = squares.iterator();
                        Iterator<ZToCARasterSquare> partnerIt = partnerSquares.iterator();
                        
                        while(myIt.hasNext() && partnerIt.hasNext()){
                            ZToCARasterSquare nextSquare = myIt.next();
                            if(nextSquare != null){
                                nextSquare.setIsDoor();
                                nextSquare.addPartner(partnerIt.next());
                            }
                        }
                    }
                }
            }
            
        }
        

        
        return container;
    }
    
    private void saveListOfDoors(ZToGraphRoomRaster roomRaster, ZToGraphRasterContainer container){
        ds.z.Room room = roomRaster.getRoom();
        for(ds.z.Edge edge : room.getEdges()){
            RoomEdge rEdge = (RoomEdge)edge;
            if((rEdge).isPassable()){
                List<ZToGraphRasterSquare> squares = util.ConversionTools.getSquaresAlongEdge(rEdge, roomRaster);
                RoomEdge partnerEdge = rEdge.getLinkTarget ();
                Room partnerRoom = rEdge.getLinkTarget ().getRoom ();
                               
                if(partnerEdge == null){
                    throw new RuntimeException(Localization.getInstance ().getString ("converter.InconsistencyException"));
                }
                
                ZToGraphRoomRaster partnerRaster = container.getRasteredRoom(partnerRoom);
                List<ZToGraphRasterSquare> partnerSquares = util.ConversionTools.getSquaresAlongEdge(partnerEdge, partnerRaster);
                
                Iterator<ZToGraphRasterSquare> myIt = squares.iterator();
                Iterator<ZToGraphRasterSquare> partnerIt = partnerSquares.iterator();
                                
                while(myIt.hasNext() && partnerIt.hasNext()){
                    ZToGraphRasterSquare nextSquare = myIt.next();
                    ZToGraphRasterSquare test = partnerIt.next();
                    container.addDoor(new ZToGraphRasteredDoor(nextSquare, test));
                    if (DebugFlags.GRAPH_DOORS){
                    	System.out.println(nextSquare+" "+test);
                    }
                }
            }
        }
    }
    
    public static Project makeComplexExample()
    {
    	Project project = new Project();
    	BuildingPlan bp = project.getPlan();
    	Floor mainFloor = new Floor(Localization.getInstance (
		).getString ("converter.groundFloor"));
    	bp.addFloor(mainFloor);
    	
    	ArrayList<PlanPoint> points;
    	
    	// Create Room 1
    	Room r01 = new Room(mainFloor, "Raum 1");
    	points = new ArrayList<PlanPoint>();
    	points.add(new PlanPoint(0.4d, 0.4d, true));
    	points.add(new PlanPoint(3.2d, 0.4d, true));
    	points.add(new PlanPoint(3.2d, 2.4d, true));
    	points.add(new PlanPoint(3.6d, 2.4d, true));
    	points.add(new PlanPoint(3.6d, 2.8d, true));
    	points.add(new PlanPoint(4d, 2.8d, true));
    	points.add(new PlanPoint(4d, 3.2d, true));
    	points.add(new PlanPoint(4.4d, 3.2d, true));
    	points.add(new PlanPoint(4.4d, 3.6d, true));
    	points.add(new PlanPoint(2d, 3.6d, true));
    	points.add(new PlanPoint(1.6d, 3.6d, true));
    	points.add(new PlanPoint(1.6d, 2.8d, true));
    	points.add(new PlanPoint(1.2d, 2.8d, true));
    	points.add(new PlanPoint(1.2d, 2d, true));
    	points.add(new PlanPoint(0.8d, 2d, true));
    	points.add(new PlanPoint(0.8d, 1.2d, true));
    	points.add(new PlanPoint(0.4d, 1.2d, true));
    	points.add(new PlanPoint(0.4d, 0.4d, true));
    	r01.add(points);
    	
    	Room r02 = new Room(mainFloor, "Raum 2");
    	points = new ArrayList<PlanPoint>();
    	points.add(new PlanPoint(2d, 3.6d, true));
    	points.add(new PlanPoint(4.4d, 3.6d, true));
    	points.add(new PlanPoint(4.4d, 4d, true));
    	points.add(new PlanPoint(2d, 4d, true));    	
    	r02.add(points);
    	
    	Room r03 = new Room(mainFloor, "Raum 3");
    	points = new ArrayList<PlanPoint>();
    	points.add(new PlanPoint(2d, 4d, true));
    	points.add(new PlanPoint(4.4d, 4d, true));
    	points.add(new PlanPoint(4.8d, 4d, true));
    	points.add(new PlanPoint(4.8d, 4.8d, true));
    	points.add(new PlanPoint(4.4d, 4.8d, true));
    	points.add(new PlanPoint(4d, 4.8d, true));
    	points.add(new PlanPoint(2.4d, 4.8d, true));
    	points.add(new PlanPoint(2.4d, 4.4d, true));
    	points.add(new PlanPoint(2d, 4.4d, true));
    	r03.add(points);
    	
    	Room r04 = new Room(mainFloor, "Raum 4");
    	points = new ArrayList<PlanPoint>();
    	points.add(new PlanPoint(4d, 4.8d, true));
    	points.add(new PlanPoint(4.4d, 4.8d, true));
    	points.add(new PlanPoint(4.4d, 6.4d, true));
    	points.add(new PlanPoint(4d, 6.4d, true));
    	points.add(new PlanPoint(4d, 4.8d, true));
    	r04.add(points);
    	
    	Room r05 = new Room(mainFloor, "Raum 5");
    	points = new ArrayList<PlanPoint>();
    	points.add(new PlanPoint(2d, 6.4d, true));
    	points.add(new PlanPoint(4d, 6.4d, true));
    	points.add(new PlanPoint(4.4d, 6.4d, true));
    	points.add(new PlanPoint(8d, 6.4d, true));
    	points.add(new PlanPoint(8d, 10d, true));
    	points.add(new PlanPoint(2d, 10d, true));
    	points.add(new PlanPoint(2d, 6.4d, true));
    	r05.add(points);
    	
    	// Connect Rooms of Floor 1
    	
    	r01.connectTo(r02, new PlanPoint(2, 3.6), new PlanPoint(4.4, 3.6));
    	r02.connectTo(r03, new PlanPoint(2, 4), new PlanPoint(4.4, 4));
    	//r03.connectTo(r04, new PlanPoint(4, 4.8), new PlanPoint(4.4, 4.8));
    	//r04.connectTo(r05, new PlanPoint(4, 6.4), new PlanPoint(4.4, 6.4));
    	
    	return project;
    }
    
    public static BuildingPlan makeAnotherExample(){
    	BuildingPlan bp = new BuildingPlan();
        Floor floor = new Floor("Erdgeschoss");
        bp.addFloor(floor);
        
        Room room = new Room( floor, "Größerer Raum" );
        
        ArrayList<PlanPoint> points;
        InaccessibleArea iA;
        DelayArea dA;
        EvacuationArea eA;
        SaveArea saveA;
        
        points = new ArrayList<PlanPoint>();        
        points.add( new PlanPoint(0.4, 0.4) );
        points.add( new PlanPoint(4.0, 0.4) );
        points.add( new PlanPoint(4.0, 4.0) );
        points.add( new PlanPoint(0.4, 4.0) );        
        room.add(points);
        
        points = new ArrayList<PlanPoint>();
        iA = new InaccessibleArea( room );
        points.add( new PlanPoint(0.8, 3.6));
        points.add( new PlanPoint(3.6, 3.6));
        points.add( new PlanPoint(3.6, 4.0));
        points.add( new PlanPoint(0.8, 4.0));
        iA.add(points);
        
        points = new ArrayList<PlanPoint>();
        dA = new DelayArea( room, DelayType.OBSTACLE );
        points.add( new PlanPoint(0.8, 2.4));
        points.add( new PlanPoint(1.6, 2.4));
        points.add( new PlanPoint(1.6, 3.2));
        points.add( new PlanPoint(0.8, 3.2));
        dA.add(points);
        
        points = new ArrayList<PlanPoint>();
        eA = new EvacuationArea( room );
        points.add( new PlanPoint(3.6, 2.4));
        points.add( new PlanPoint(4.0, 2.4));
        points.add( new PlanPoint(4.0, 3.2));
        points.add( new PlanPoint(3.6, 3.2));
        eA.add(points);
        
        points = new ArrayList<PlanPoint>();
        saveA = new SaveArea(room);
        points.add( new PlanPoint(3.2, 2.4));
        points.add( new PlanPoint(3.6, 2.4));
        points.add( new PlanPoint(3.6, 3.2));
        points.add( new PlanPoint(3.2, 3.2));
        saveA.add(points);
        return bp;
    }
    
    public static BuildingPlan makeBuildingPlanExample(){
    	BuildingPlan bp = new BuildingPlan();
        Floor floor = new Floor();
        bp.addFloor(floor);
        
        Room room = new Room( floor );
        
        ArrayList<PlanPoint> points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint( 0.4, 0.4 ) );
        points.add( new PlanPoint( 2.4, 0.4 ) );
        points.add( new PlanPoint( 2.4, 2.4 ) );
        points.add( new PlanPoint( 0.4, 2.4 ) );
        room.add( points );
        
        // To exception is allowed here!
        room.checkRasterized();
        
        // Inaccessible Areas
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint( 1.2, 0.4 ) );
        points.add( new PlanPoint( 1.6, 0.4 ) );
        points.add( new PlanPoint( 1.6, 0.8 ) );
        points.add( new PlanPoint( 1.2, 0.8 ) );
        InaccessibleArea iA = new InaccessibleArea( room );
        iA.add( points );
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint( 1.2, 1.2 ) );
        points.add( new PlanPoint( 1.6, 1.2 ) );
        points.add( new PlanPoint( 1.6, 1.6 ) );
        points.add( new PlanPoint( 2.0, 1.6 ) );
        points.add( new PlanPoint( 2.0, 2.0 ) );
        points.add( new PlanPoint( 1.2, 2.0 ) );
        iA = new InaccessibleArea( room );
        iA.add( points );
        
        // DelayArea
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint( 0.8, 0.8 ) );
        points.add( new PlanPoint( 2.0, 0.8 ) );
        points.add( new PlanPoint( 2.0, 1.2 ) );
        points.add( new PlanPoint( 0.8, 1.2 ) );
        DelayArea dA = new DelayArea( room, DelayArea.DelayType.OTHER, 1.0/3.0 );
        dA.add( points );

        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint( 1.6, 0.8 ) );
        points.add( new PlanPoint( 2.4, 0.8 ) );
        points.add( new PlanPoint( 2.4, 2.0 ) );
        points.add( new PlanPoint( 1.6, 2.0 ) );
        dA = new DelayArea( room, DelayArea.DelayType.OTHER, 0.5 );
        dA.add( points );

        Room pg_room = new Room( floor );
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(3.0,3.0) );
        points.add( new PlanPoint(8.6,3.0) );
        points.add( new PlanPoint(8.6,8.2) );
        points.add( new PlanPoint(3.0,8.2) );
        pg_room.add( points );
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(3.4,3.4) );
        points.add( new PlanPoint(3.8,3.4) );
        points.add( new PlanPoint(3.8,5.4) );
        points.add( new PlanPoint(3.4,5.4) );
        iA = new InaccessibleArea( pg_room );
        iA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(3.8,3.4));
        points.add( new PlanPoint(4.6,3.4));
        points.add( new PlanPoint(4.6,4.6));
        points.add( new PlanPoint(3.8,4.6));
        points.add( new PlanPoint(3.8,4.2));
        points.add( new PlanPoint(4.2,4.2));
        points.add( new PlanPoint(4.2,3.8));
        points.add( new PlanPoint(3.8,3.8));
        iA = new InaccessibleArea( pg_room );
        iA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(5.0,3.4));
        points.add( new PlanPoint(6.6,3.4));
        points.add( new PlanPoint(6.6,3.8));
        points.add( new PlanPoint(5.4,3.8));
        points.add( new PlanPoint(5.4,5.0));
        points.add( new PlanPoint(6.2,5.0));
        points.add( new PlanPoint(6.2,4.6));
        points.add( new PlanPoint(5.8,4.6));
        points.add( new PlanPoint(5.8,4.2));
        points.add( new PlanPoint(6.6,4.2));
        points.add( new PlanPoint(6.6,5.4));
        points.add( new PlanPoint(5.0,5.4));
        iA = new InaccessibleArea( pg_room );
        iA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(3.4,5.8) );
        points.add( new PlanPoint(4.6,5.8) );
        points.add( new PlanPoint(4.6,6.2) );
        points.add( new PlanPoint(3.8,6.2) );
        points.add( new PlanPoint(3.8,6.6) );
        points.add( new PlanPoint(4.6,6.6) );
        points.add( new PlanPoint(4.6,7.8) );
        points.add( new PlanPoint(3.4,7.8) );
        points.add( new PlanPoint(3.4,7.4) );
        points.add( new PlanPoint(4.2,7.4) );
        points.add( new PlanPoint(4.2,7.0) );
        points.add( new PlanPoint(3.4,7.0) );
        iA = new InaccessibleArea( pg_room );
        iA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(5.0,6.6) );
        points.add( new PlanPoint(5.4,6.6) );        
        points.add( new PlanPoint(5.4,7.0) );
        points.add( new PlanPoint(5.0,7.0) );
        iA = new InaccessibleArea( pg_room );
        iA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(5.4,6.2) );
        points.add( new PlanPoint(5.8,6.2) );        
        points.add( new PlanPoint(5.8,6.6) );
        points.add( new PlanPoint(5.4,6.6) );
        iA = new InaccessibleArea( pg_room );
        iA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(5.8,5.8) );
        points.add( new PlanPoint(6.2,5.8) );        
        points.add( new PlanPoint(6.2,7.8) );
        points.add( new PlanPoint(5.8,7.8) );
        iA = new InaccessibleArea( pg_room );
        iA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(6.6,5.8));
        points.add( new PlanPoint(8.2,5.8));
        points.add( new PlanPoint(8.2,6.2));
        points.add( new PlanPoint(7.8,6.2));
        points.add( new PlanPoint(7.8,6.6));
        points.add( new PlanPoint(7.4,6.6));
        points.add( new PlanPoint(7.4,6.2));
        points.add( new PlanPoint(6.6,6.2));
        iA = new InaccessibleArea( pg_room );
        iA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(6.6,6.6) );
        points.add( new PlanPoint(8.2,6.6) );
        points.add( new PlanPoint(8.2,7.0) );
        points.add( new PlanPoint(7.4,7.0) );
        points.add( new PlanPoint(7.4,7.4) );
        points.add( new PlanPoint(7.0,7.4) );
        points.add( new PlanPoint(7.0,7.0) );
        points.add( new PlanPoint(6.6,7.0) );
        iA = new InaccessibleArea( pg_room );
        iA.add(points);        
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(6.6,7.4));
        points.add( new PlanPoint(7.0,7.4));
        points.add( new PlanPoint(7.0,7.8));
        points.add( new PlanPoint(6.6,7.8));
        iA = new InaccessibleArea( pg_room );
        iA.add(points);
        
        return bp;
        
    }
    
	  public static BuildingPlan testraum() {
		    // 5x5-Raum mit rasterisierten Koordinaten erzeugen
		  	BuildingPlan bp = new BuildingPlan();
		    Floor floor = new Floor();
		    bp.addFloor(floor);
		    
		    Room room = new Room( floor, "Testraum" );		    
		    ArrayList<PlanPoint> points = new ArrayList<PlanPoint>();
		    points.add( new PlanPoint( 0.4, 0.4 ) );	    
		    points.add( new PlanPoint( 2.4, 0.4 ) );
		    points.add( new PlanPoint( 2.4, 2.4 ) );
		    points.add( new PlanPoint( 2.0, 2.4 ) );
		    points.add( new PlanPoint( 1.2, 2.4 ) );
		    points.add( new PlanPoint( 0.4, 2.4 ) );
		    room.add( points );

		    Room room2 = new Room( floor, "Anderer Raum");
		    points = new ArrayList<PlanPoint>();
		    points.add( new PlanPoint( 2.4, 2.4 ) );
		    points.add( new PlanPoint( 2.0, 2.4 ) );
		    points.add( new PlanPoint( 1.2, 2.4 ) );
		    points.add( new PlanPoint( 1.2, 3.2 ) );
		    points.add( new PlanPoint( 2.0, 3.2 ) );
		    points.add( new PlanPoint( 2.4, 3.2 ) );	    
		    room2.add( points );
		    room2.connectTo( room, new PlanPoint( 2.0, 2.4, true ), new PlanPoint( 1.2, 2.4, true ) );

		    
		    // To exception is allowed here!
		    room.checkRasterized();
		    
		    // Inaccessible Areas
		    points = new ArrayList<PlanPoint>();
		    points.add( new PlanPoint( 1.2, 0.4 ) );
		    points.add( new PlanPoint( 1.6, 0.4 ) );
		    points.add( new PlanPoint( 1.6, 0.8 ) );
		    points.add( new PlanPoint( 1.2, 0.8 ) );
		    InaccessibleArea iA = new InaccessibleArea( room );
		    iA.add( points );
		    
		    points = new ArrayList<PlanPoint>();
		    points.add( new PlanPoint( 1.2, 1.2 ) );
		    points.add( new PlanPoint( 1.6, 1.2 ) );
		    points.add( new PlanPoint( 1.6, 1.6 ) );
		    points.add( new PlanPoint( 2.0, 1.6 ) );
		    points.add( new PlanPoint( 2.0, 2.0 ) );
		    points.add( new PlanPoint( 1.2, 2.0 ) );
		    iA = new InaccessibleArea( room );
		    iA.add( points );
		    
		    // DelayArea
		    points = new ArrayList<PlanPoint>();
		    points.add( new PlanPoint( 0.8, 0.8 ) );
		    points.add( new PlanPoint( 2.0, 0.8 ) );
		    points.add( new PlanPoint( 2.0, 1.2 ) );
		    points.add( new PlanPoint( 0.8, 1.2 ) );
		    DelayArea dA = new DelayArea( room, DelayArea.DelayType.OTHER, 1.0/3.0 );
		    dA.add( points );

		    points = new ArrayList<PlanPoint>();
		    points.add( new PlanPoint( 1.6, 0.8 ) );
		    points.add( new PlanPoint( 2.4, 0.8 ) );
		    points.add( new PlanPoint( 2.4, 2.0 ) );
		    points.add( new PlanPoint( 1.6, 2.0 ) );
		    dA = new DelayArea( room, DelayArea.DelayType.OTHER, 0.5 );
		    dA.add( points );
		    
		    return bp;

		  }
    
    public static BuildingPlan makeBuildingPlanExampleWithDifferentAreas(){
        BuildingPlan bp = new BuildingPlan();
        Floor floor = new Floor();
        bp.addFloor(floor);
        
        Room room = new Room( floor );
        
        ArrayList<PlanPoint> points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint( 0.4, 0.4 ) );
        points.add( new PlanPoint( 2.4, 0.4 ) );
        points.add( new PlanPoint( 2.4, 2.4 ) );
        points.add( new PlanPoint( 0.4, 2.4 ) );
        room.add( points );
        
        // To exception is allowed here!
        room.checkRasterized();
        
        // Inaccessible Areas
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint( 1.2, 0.4 ) );
        points.add( new PlanPoint( 1.6, 0.4 ) );
        points.add( new PlanPoint( 1.6, 0.8 ) );
        points.add( new PlanPoint( 1.2, 0.8 ) );
        InaccessibleArea iA = new InaccessibleArea( room );
        iA.add( points );
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint( 1.2, 1.2 ) );
        points.add( new PlanPoint( 1.6, 1.2 ) );
        points.add( new PlanPoint( 1.6, 1.6 ) );
        points.add( new PlanPoint( 2.0, 1.6 ) );
        points.add( new PlanPoint( 2.0, 2.0 ) );
        points.add( new PlanPoint( 1.2, 2.0 ) );
        iA = new InaccessibleArea( room );
        iA.add( points );
        
        // DelayArea
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint( 0.8, 0.8 ) );
        points.add( new PlanPoint( 2.0, 0.8 ) );
        points.add( new PlanPoint( 2.0, 1.2 ) );
        points.add( new PlanPoint( 0.8, 1.2 ) );
        DelayArea dA = new DelayArea( room, DelayArea.DelayType.OTHER, 1.0/3.0 );
        dA.add( points );

        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint( 1.6, 0.8 ) );
        points.add( new PlanPoint( 2.4, 0.8 ) );
        points.add( new PlanPoint( 2.4, 2.0 ) );
        points.add( new PlanPoint( 1.6, 2.0 ) );
        dA = new DelayArea( room, DelayArea.DelayType.OTHER, 0.5 );
        dA.add( points );

        Room pg_room = new Room( floor );
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(3.0,3.0) );
        points.add( new PlanPoint(8.6,3.0) );
        points.add( new PlanPoint(8.6,8.2) );
        points.add( new PlanPoint(3.0,8.2) );
        pg_room.add( points );
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(3.4,3.4) );
        points.add( new PlanPoint(3.8,3.4) );
        points.add( new PlanPoint(3.8,5.4) );
        points.add( new PlanPoint(3.4,5.4) );
        iA = new InaccessibleArea( pg_room );
        iA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(3.8,3.4));
        points.add( new PlanPoint(4.6,3.4));
        points.add( new PlanPoint(4.6,4.6));
        points.add( new PlanPoint(3.8,4.6));
        points.add( new PlanPoint(3.8,4.2));
        points.add( new PlanPoint(4.2,4.2));
        points.add( new PlanPoint(4.2,3.8));
        points.add( new PlanPoint(3.8,3.8));
        iA = new InaccessibleArea( pg_room );
        iA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(5.0,3.4));
        points.add( new PlanPoint(6.6,3.4));
        points.add( new PlanPoint(6.6,3.8));
        points.add( new PlanPoint(5.4,3.8));
        points.add( new PlanPoint(5.4,5.0));
        points.add( new PlanPoint(6.2,5.0));
        points.add( new PlanPoint(6.2,4.6));
        points.add( new PlanPoint(5.8,4.6));
        points.add( new PlanPoint(5.8,4.2));
        points.add( new PlanPoint(6.6,4.2));
        points.add( new PlanPoint(6.6,5.4));
        points.add( new PlanPoint(5.0,5.4));
        iA = new InaccessibleArea( pg_room );
        iA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(3.4,5.8) );
        points.add( new PlanPoint(4.6,5.8) );
        points.add( new PlanPoint(4.6,6.2) );
        points.add( new PlanPoint(3.8,6.2) );
        points.add( new PlanPoint(3.8,6.6) );
        points.add( new PlanPoint(4.6,6.6) );
        points.add( new PlanPoint(4.6,7.8) );
        points.add( new PlanPoint(3.4,7.8) );
        points.add( new PlanPoint(3.4,7.4) );
        points.add( new PlanPoint(4.2,7.4) );
        points.add( new PlanPoint(4.2,7.0) );
        points.add( new PlanPoint(3.4,7.0) );
        dA = new DelayArea( pg_room, DelayType.OBSTACLE );
        dA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(5.0,6.6) );
        points.add( new PlanPoint(5.4,6.6) );        
        points.add( new PlanPoint(5.4,7.0) );
        points.add( new PlanPoint(5.0,7.0) );
        dA = new DelayArea( pg_room, DelayType.OBSTACLE );
        dA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(5.4,6.2) );
        points.add( new PlanPoint(5.8,6.2) );        
        points.add( new PlanPoint(5.8,6.6) );
        points.add( new PlanPoint(5.4,6.6) );
        SaveArea sA = new SaveArea( pg_room );
        sA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(5.8,5.8) );
        points.add( new PlanPoint(6.2,5.8) );        
        points.add( new PlanPoint(6.2,7.8) );
        points.add( new PlanPoint(5.8,7.8) );
        dA = new DelayArea( pg_room, DelayType.OBSTACLE );
        dA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(6.6,5.8));
        points.add( new PlanPoint(8.2,5.8));
        points.add( new PlanPoint(8.2,6.2));
        points.add( new PlanPoint(7.8,6.2));
        points.add( new PlanPoint(7.8,6.6));
        points.add( new PlanPoint(7.4,6.6));
        points.add( new PlanPoint(7.4,6.2));
        points.add( new PlanPoint(6.6,6.2));
        EvacuationArea eA = new EvacuationArea( pg_room );
        eA.add(points);
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(6.6,6.6) );
        points.add( new PlanPoint(8.2,6.6) );
        points.add( new PlanPoint(8.2,7.0) );
        points.add( new PlanPoint(7.4,7.0) );
        points.add( new PlanPoint(7.4,7.4) );
        points.add( new PlanPoint(7.0,7.4) );
        points.add( new PlanPoint(7.0,7.0) );
        points.add( new PlanPoint(6.6,7.0) );
        eA = new EvacuationArea( pg_room );
        eA.add(points);        
        
        points = new ArrayList<PlanPoint>();
        points.add( new PlanPoint(6.6,7.4));
        points.add( new PlanPoint(7.0,7.4));
        points.add( new PlanPoint(7.0,7.8));
        points.add( new PlanPoint(6.6,7.8));
        eA = new EvacuationArea( pg_room );
        eA.add(points);
        
        return bp;
        
    }
    
    public static void main(String args[]){
    	PropertyContainer propertyContainer = PropertyContainer.getInstance();
    	propertyContainer.define("converter.Imbalance", Integer.class, 1);
    	propertyContainer.define("converter.GraphPrecision", Integer.class, 2);
    	propertyContainer.define("converter.AccurateDelayAreaCreation", Boolean.class, false);
    	propertyContainer.set("converter.Imbalance", 1);
    	propertyContainer.set("converter.GraphPrecision",1);
    	propertyContainer.set("converter.AccurateDelayAreaCreation", true);
    	ZToGraphConverter.convertBuildingPlan(testraum(), new NetworkFlowModel());
    }
}
