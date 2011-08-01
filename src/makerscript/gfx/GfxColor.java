package makerscript.gfx;

public class GfxColor
{
  public float fRed   = 1.0f;
  public float fGreen = 1.0f;
  public float fBlue  = 1.0f;
  public float fAlpha = 1.0f;
  
  public GfxColor() {}
  public GfxColor( float fRed,  float fGreen, float fBlue, float fAlpha ) { set( fRed, fGreen, fBlue, fAlpha ); }
  public GfxColor( float fRed,  float fGreen, float fBlue )               { set( fRed, fGreen, fBlue, 255.0f ); }
  public GfxColor( float fGray, float fAlpha )                            { set( fGray, fAlpha ); }
  public GfxColor( float fGray )                                          { set( fGray ); }
 
  public GfxColor set( float fGray )                                          { return set( fGray, 255.0f ); }
  public GfxColor set( float fGray, float fAlpha )                            { return set( fGray,  fGray, fGray,  255.0f ); }
  public GfxColor set( float fRed, float fGreen, float fBlue )                { return set(  fRed, fGreen, fBlue,  255.0f ); }
  public GfxColor set( float fRed, float fGreen, float fBlue, float fAlpha )  { this.fRed = fRed/255.0f; this.fGreen = fGreen/255.0f; this.fBlue = fBlue/255.0f; this.fAlpha = fAlpha/255.0f;  return this; }
  public GfxColor set( GfxColor from )
  {
    fRed    = from.fRed;
    fGreen  = from.fGreen;
    fBlue   = from.fBlue;
    fAlpha  = from.fAlpha;
    
    return this;
  }
  
  public float[] get() {  return new float[] { fRed, fGreen, fBlue, fAlpha }; }
}