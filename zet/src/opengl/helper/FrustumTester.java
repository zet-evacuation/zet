package opengl.helper;


// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.5BBEE2FB-C7C5-F861-5E63-5ABBEE1F955C]
// </editor-fold> 
public abstract class FrustumTester extends CullingTester {

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.6816EEB3-5A79-25C9-B0FA-588B51C96667]
    // </editor-fold> 
    private Frustum frustum;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.0378A435-8AF6-10E5-19C6-0D858BFDB12D]
    // </editor-fold> 
    public FrustumTester () {
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.DD241778-D134-D588-6D74-151AF0D7F62C]
    // </editor-fold> 
    public Frustum getFrustum () {
        return frustum;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.370645EC-585F-09C7-4823-0D1641F02689]
    // </editor-fold> 
    public void setFrustum (Frustum val) {
        this.frustum = val;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.FAA08F96-3845-F950-44A8-319DDB5BEB51]
    // </editor-fold> 
    public CullingLocation testCullingLocation (CullingShape object) {
        return null;
    }

}

