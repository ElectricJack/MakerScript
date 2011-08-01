package makerscript;

import java.util.ArrayList;

import makerscript.geom.Vector3;

public class Machine {
  public String          name;
  public Vector3         size;
  
  public ArrayList<Tool> tools;      // The tools available for this machine
  public Tool            activeTool; // The active tool for any operations to be performed by this as an active machine
}

