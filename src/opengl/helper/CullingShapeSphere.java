package opengl.helper;

import util.vectormath.Vector3;


// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.B689DE29-5907-83E0-1559-F54FCB39CA7F]
// </editor-fold> 
public class CullingShapeSphere extends CullingShape {

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.7B4B6778-0835-6B22-4647-05EA52197565]
    // </editor-fold> 
    private double radius;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.3783B6CD-D5FC-F086-AD4B-09052294ED12]
    // </editor-fold> 
    private Vector3 center;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.EEB9A688-6755-202C-3643-262DCCCA2D87]
    // </editor-fold> 
    public CullingShapeSphere () {
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.C214931C-AF34-7A70-868D-990017C686D3]
    // </editor-fold> 
    public Vector3 getCenter () {
        return center;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.34A1DB49-315C-FB94-E5D1-5206FC093367]
    // </editor-fold> 
    public void setCenter (Vector3 center) {
        this.center = center;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.580FE835-23F4-F7C2-8901-EF7D27048444]
    // </editor-fold> 
    public void setRadius (double radius) {
        this.radius = radius;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.5E5D9C5F-808B-B99B-DFB1-4D5AA40C7A48]
    // </editor-fold> 
    public double getRadius () {
        return radius;
    }

}

