package net.imagej.ui.swing.viewer.cppipe;

import ch.epfl.biop.lima.cppipe.CPPipe;
import org.scijava.display.AbstractDisplay;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;

@Plugin(type = Display.class)
public class CPPipeDisplay extends AbstractDisplay<CPPipe> {
    public CPPipeDisplay() {
        super(CPPipe.class);
    }
}
