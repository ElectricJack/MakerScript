/*
  ScriptableMill
  copyright (c) 2011, Jack W. Kern
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */
package makerscript;


import com.fieldfx.doc.ListDoc;
import com.fieldfx.lang.ScriptInstance;

import makerscript.commands.CmdAlign;
import makerscript.commands.CmdClean;
import makerscript.commands.CmdDrill;
import makerscript.commands.CmdExport;
import makerscript.commands.CmdFlip;
import makerscript.commands.CmdLayer;
import makerscript.commands.CmdLoad;
import makerscript.commands.CmdMill;
import makerscript.commands.CmdMirror;
import makerscript.commands.CmdOffset;
import makerscript.commands.CmdResize;
//import makerscript.commands.CmdSave;
import makerscript.commands.CmdSelect;
import makerscript.commands.CmdSet;
import makerscript.commands.CmdTarget;
import makerscript.commands.CmdView;

import com.fieldfx.math.Vector2;
import com.fieldfx.ui.UIWindow;
import com.fieldfx.ui.UIManager;
import com.fieldfx.ui.UIButton;



public class MakerScriptCommands extends ScriptInstance {
  
  Vector2 buttonPos = new Vector2(5,5);
  
  private void addCommandButton( String name, UIManager mgr, UIWindow cmdbox ) {
    //UIButton         cmdButton = mgr.getUIFactory().newButton();
    //                 cmdButton.setPos( buttonPos );
    //                 cmdButton.setSize( 150, 35 );
    //                 cmdButton.setCaption( name );
    //cmdbox.addChild( cmdButton );
    
    buttonPos.y += 40;
  }
  
  // ----------------------------------------------------------------- //
  public MakerScriptCommands( ListDoc<String> codeState, UIManager mgr, UIWindow cmdbox ) {
    super(codeState);
    
    new CmdAlign   ( compiler );   addCommandButton("Align",mgr,cmdbox);
    //new CmdBridge  ( commands );
    
    new CmdClean   ( compiler );   addCommandButton("Clean",mgr,cmdbox);
    new CmdDrill   ( compiler );   addCommandButton("Drill",mgr,cmdbox);
    new CmdExport  ( compiler );   addCommandButton("Export",mgr,cmdbox);
    new CmdFlip    ( compiler );   addCommandButton("Flip",mgr,cmdbox);
    new CmdLayer   ( compiler );   addCommandButton("Layer",mgr,cmdbox);
    new CmdLoad    ( compiler );
    new CmdMill    ( compiler );
    new CmdMirror  ( compiler );
    new CmdOffset  ( compiler );
    new CmdResize  ( compiler );
    
    //new CmdSave    ( commands );
    new CmdSelect  ( compiler );
    new CmdSet     ( compiler );
    new CmdTarget  ( compiler );
    new CmdView    ( compiler );
  }
}
