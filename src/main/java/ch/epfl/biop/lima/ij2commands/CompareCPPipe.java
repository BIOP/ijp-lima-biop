package ch.epfl.biop.lima.ij2commands;

import ch.epfl.biop.lima.cppipe.CPModule;
import ch.epfl.biop.lima.cppipe.CPParam;
import ch.epfl.biop.lima.cppipe.CPPipe;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, menuPath = "Plugins>BIOP>Lima>Compare Pipes")
public class CompareCPPipe implements Command {
    @Parameter
    CPPipe pipe1;

    @Parameter
    CPPipe pipe2;

    @Override
    public void run() {

        for (int indexModule=0;indexModule<pipe1.modules.size();indexModule++) {
            CPModule m1 = pipe1.modules.get(indexModule);
            if (pipe2.modules.size()>indexModule) {
                CPModule m2 = pipe2.modules.get(indexModule);
                for (int indexParam=0;indexParam<m2.params.size();indexParam++) {
                    CPParam p1 = m1.params.get(indexParam);
                    if (m2.params.size()>indexParam) {
                        CPParam p2 = m2.params.get(indexParam);
                        if (p1.getName().equals(p2.getName())) {
                            if (p1.getValue().equals(p2.getValue())) {
                            } else {
                                System.out.println("Module "+m1.params.get(0)+" param "+indexParam+" diff:");
                                System.out.println("m1 = "+p1);
                                System.out.println("m2 = "+p2);
                            }
                        } else {
                            System.out.println("Module "+m1.params.get(0)+" param "+indexParam+" diff:");
                            System.out.println("m1 = "+p1);
                            System.out.println("m2 = "+p2);
                        }
                    } else {
                        System.out.println("Parameter "+indexParam+" does not exist in pipe2.");
                    }
                }
            } else {
                System.out.println("Module "+indexModule+" does not exist in pipe2.");
            }

        }

    }
}
