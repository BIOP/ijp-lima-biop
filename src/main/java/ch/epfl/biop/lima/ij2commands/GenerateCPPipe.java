package ch.epfl.biop.lima.ij2commands;

import ch.epfl.biop.lima.cppipe.CPPipe;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.ArrayList;

@Plugin(type = Command.class, menuPath = "Plugins>BIOP>Lima>Generate Pipe")
public class GenerateCPPipe implements Command {
    @Parameter(label = "Duplicate modules - format:'CHANNEL:10ms;100ms;1s'", required = false, style = "text area")
    String generate_modules_based_on_keyword; // format: @CHANNEL@:_10ms;_100ms;_1s

    @Parameter(type= ItemIO.BOTH)
    CPPipe pipe_in;

    @Parameter(type=ItemIO.OUTPUT)
    CPPipe pipe_out;

    @Parameter
    String pipe_suffix;

    @Override
    public void run() {
        // Generate pipeline if necessary (if module generate is filled)
        if ((generate_modules_based_on_keyword==null)||(generate_modules_based_on_keyword.equals(""))) {
            System.err.println("Error in module generation definition. Check format 'key:name1;name2;'");
        } else {
            String[] parts = generate_modules_based_on_keyword.split(":",2);
            if (parts.length==2) {
                ArrayList<String> channelsToGenerate = new ArrayList<>();
                String[] channelNames = parts[1].split(";");
                for (int i=0;i<channelNames.length;i++) {
                    channelsToGenerate.add(channelNames[i]);
                }
                pipe_out = pipe_in.generatePipe(parts[0], channelsToGenerate);
                pipe_out.name=pipe_in.toString()+pipe_suffix;
            } else {
                System.err.println("Error in module generation definition. Check format 'key:name1;name2;'");
            }
        }
    }
}
