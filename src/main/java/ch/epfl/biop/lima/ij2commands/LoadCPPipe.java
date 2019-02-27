package ch.epfl.biop.lima.ij2commands;

import ch.epfl.biop.lima.cppipe.CPPipe;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;

@Plugin(type = Command.class, menuPath = "Plugins>BIOP>Lima>Load CPPipe")
public class LoadCPPipe implements Command {

    @Parameter
    File f;

    @Parameter(type= ItemIO.OUTPUT)
    CPPipe cppipe;

    @Override
    public void run() {
        cppipe = CPPipe.load(f);
    }
}
