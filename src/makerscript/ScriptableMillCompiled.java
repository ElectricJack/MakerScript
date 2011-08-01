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
import makerscript.lang.LSCompiled;

public class ScriptableMillCompiled extends LSCompiled {

  // ----------------------------------------------------------------- //
  public ScriptableMillCompiled()
  {
    super();
    
    new CmdAlign   ( commands );
    //new CmdBridge  ( commands );
    new CmdClean   ( commands );
    new CmdDrill   ( commands );
    new CmdExport  ( commands );
    new CmdFlip    ( commands );
    new CmdLayer   ( commands );
    new CmdLoad    ( commands );
    new CmdMill    ( commands );
    new CmdMirror  ( commands );
    new CmdOffset  ( commands );
    new CmdResize  ( commands );
    //new CmdSave    ( commands );
    new CmdSelect  ( commands );
    new CmdSet     ( commands );
    new CmdTarget  ( commands );
    new CmdView    ( commands );
  }
}
