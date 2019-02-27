package ch.epfl.biop.lima.ij2commands;

import ch.epfl.biop.lima.cppipe.CPPipe;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;

@Plugin(type = Command.class, menuPath = "Plugins>BIOP>Lima>Save CPPipe")
public class SaveCPPipe implements Command {
    @Parameter
    File f;

    @Parameter(type= ItemIO.INPUT)
    CPPipe cppipe;

    @Parameter
    boolean saveAsReadableText=false;

    @Override
    public void run() {
        cppipe.save(f.getAbsolutePath(), saveAsReadableText);
    }
}
