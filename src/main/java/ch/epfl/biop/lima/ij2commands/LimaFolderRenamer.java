package ch.epfl.biop.lima.ij2commands;

import org.apache.commons.io.FileUtils;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Plugin(type = Command.class, menuPath = "Plugins>BIOP>Lima>Rename Folder For Lima Task")
public class LimaFolderRenamer implements Command {

    @Parameter(label ="Rename images infos. For each line, type 'OldName:NewName'", style = "text area", required = false)
    String rename_images_infos =
            "001 :LIPID_ID_1--\n"+
                    "001 :LIPID_ID_1--\n"+
                    "002 :LIPID_ID_2--\n"+
                    "003 :LIPID_ID_3--\n"+
                    "004 :LIPID_ID_4--\n"+
                    "005 :LIPID_ID_5--\n"+
                    "006 :LIPID_ID_6--\n"+
                    "007 :LIPID_ID_7--\n"+
                    "008 :LIPID_ID_8--\n"+
                    "009 :LIPID_ID_9--\n"+
                    "010 :LIPID_ID_10--\n"+
                    "011 :LIPID_ID_11--\n"+
                    "012 :LIPID_ID_12--\n"+
                    "013 :LIPID_ID_13--\n"+
                    "014 :LIPID_ID_14--\n"+
                    "015 :LIPID_ID_15--\n"+
                    "016 :LIPID_ID_16--\n"+
                    "017 :LIPID_ID_17--\n"+
                    "018 :LIPID_ID_18--\n"+
                    "019 :LIPID_ID_19--\n"+
                    "020 :LIPID_ID_20--\n"+
                    "021 :LIPID_ID_21--\n"+
                    "022 :LIPID_ID_22--\n"+
                    "023 :LIPID_ID_23--\n"+
                    "024 :LIPID_ID_24--\n"+
                    "025 :LIPID_ID_25--\n"+
                    "026 :LIPID_ID_26--\n"+
                    "027 :LIPID_ID_27--\n"+
                    "028 :LIPID_ID_28--\n"+
                    "029 :LIPID_ID_29--\n"+
                    "030 :LIPID_ID_30--"; // format: ATTO3:_atto3ms;_100ms;_1s

    @Parameter(label ="Rename images channels. For each line, type 'OldName:NewName'", style = "text area", required = false)
    String rename_images_channels = "CY5.:Cy5_3ms.\n" +
            "CY52.:Cy5_5ms.\n" +
            "FITC.:FITC_1ms.\n" +
            "FITC2.:FITC_5ms.\n" +
            "FITC3.:FITC_10ms.\n" +
            "FITC4.:FITC_30ms.\n" +
            "FITC5.:FITC_50ms.\n" +
            "FITC6.:FITC_75ms.\n" +
            "FITC7.:FITC_100ms.\n" +
            "FITC8.:FITC_200ms.\n" +
            "FITC9.:FITC_300ms."; // format: ATTO3:_atto3ms;_100ms;_1s

    @Parameter(type= ItemIO.INPUT, style = "directory", required = true)
    File folder_images;

    @Parameter(label = "Image name prefix", style = "text area")
    String imageNamePrefix="Exp1_PLCdelta--";

    @Override
    public void run() {
        renameFiles(imageNamePrefix, rename_images_channels);
        renameFiles("", rename_images_infos);
    }

    public void renameFiles(String prefix, String renameStringList) {
        // Loops through all files in the folder
        if (renameStringList!=null) {

            Map<String, String> fileNewNames = new HashMap<>();
            String[] renamePatterns = renameStringList.split("[\\r\\n]+");
            for (int i=0;i<renamePatterns.length;i++) {
                String[] renamePattern = renamePatterns[i].split(":");
                if (fileNewNames.containsKey(renamePattern[0])) {
                    System.err.println("Error, duplicate key found:"+renamePattern[0]);
                } else {
                    fileNewNames.put(renamePattern[0], renamePattern[1]);
                }
            }

            fileNewNames.forEach((k,v) -> {
                System.out.println(k+"--"+v);
            });

            File[] listofFiles = folder_images.listFiles();
            for (int i=0;i<listofFiles.length;i++) {
                File f = listofFiles[i];
                System.out.println(f.getName());

                fileNewNames.keySet().stream().filter(k -> f.getName().contains(k)).findFirst().ifPresent(k -> {
                    String newName = prefix + f.getName().replace(k, fileNewNames.get(k));
                    System.out.println(f.getName()+" contains "+k+" renamed to "+newName);
                    System.out.println(f.getAbsolutePath());
                    String newPath = f.getParent()+File.separator+newName;
                    System.out.println(newPath);

                    try {
                        FileUtils.moveFile(
                                FileUtils.getFile(f.getAbsolutePath()),
                                FileUtils.getFile(newPath)
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }
        }
    }

}
