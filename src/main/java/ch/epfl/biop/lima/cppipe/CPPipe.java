package ch.epfl.biop.lima.cppipe;

import java.io.*;
import java.util.ArrayList;
import org.apache.commons.io.FilenameUtils;

public class CPPipe implements Cloneable {
    public ArrayList<CPParam> header = new ArrayList<>();
    public ArrayList<CPModule> modules = new ArrayList<>();

    public String name;

    public static CPPipe load(File f, boolean isReadable) {

        CPPipe cppipe = new CPPipe();
        try {
            BufferedReader file = new BufferedReader(new FileReader(f));
            String line;

            line = file.readLine();
            String[] currentParam;

            // HEADER
            while (!(line.trim().equals(""))) {
                currentParam = readParamLine(line);
                cppipe.header.add(new CPParam(currentParam[0],currentParam[1]));
                line = file.readLine();
            }

            // MODULES
            while (line!=null) { // While not end of file
                // NEW MODULE
                CPModule m = new CPModule();
                cppipe.modules.add(m);
                line = file.readLine();
                while ((line!=null)&&!(line.trim().equals(""))) {
                    currentParam = readParamLine(line);
                    m.params.add(new CPParam(currentParam[0],currentParam[1], isReadable));
                    line = file.readLine();
                }
            }
            cppipe.name = FilenameUtils.removeExtension(f.getName());
            return cppipe;
        } catch (IOException e) {
            return null;
        }
    }

    public static CPPipe load(File f) {
        return load(f,false);
    }

    public void save(String path) {
        this.save(path,false);
    }

    public void save(String path, boolean convert) {
        BufferedWriter writer = null;
        try {
            File temp =  new File(path);
            writer = new BufferedWriter(new FileWriter(temp));
            if (convert) {
                writer.write(this.toReadableString());
            } else {
                writer.write(this.toUnreadableString());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    public String toUnreadableString() {
        String str = "";

        for (CPParam p : header) {
            String k = p.getName();
            String v = p.getValue();
            str += k + ":" + v + "\r\n";
        }

        for (CPModule m: modules) {
            str+="\n"; // New module
            for (CPParam p : m.params) {
                String k = p.getName();
                String v = p.getValue();
                str += k + ":" + v + "\r\n";
            }
        }

        return str;

    }

    static public int iPipe=0;

    public String toString() {
        if (name==null) {
            name = "CPPIPE_"+iPipe;
            iPipe++;
        }
        return name;
    }

    public String toReadableString() {
        String str = "";

        for (CPParam p : header) {
            String k = p.getName();
            String v = p.getValue();
            str += k + ":" + v + "\r\n";
        }

        for (CPModule m: modules) {
            str+="\n"; // New module
            for (CPParam p : m.params) {
                String k = p.getName();
                String v = p.getReadableValue();
                str += k + ":" + v + "\r\n";
            }
        }

        return str;
    }

    public void testDecodeEncode() {
        for (CPModule m: modules) {
            for (CPParam p : m.params) {

                String old_v = p.getValue();
                String readable_v = p.getReadableValue();
                String reencode_v = p.getReEncodedValue(readable_v);

                if (!(old_v.equals(reencode_v))) {
                    System.out.println("Cell Profiler .cppipe file String conversion error:");
                    System.err.println("old="+old_v);
                    System.err.println("new="+reencode_v);
                    System.err.println("readable="+readable_v);
                }
            }
        }
    }

    static String[] readParamLine(String line) {
        return line.split(":",2);
    }

    static int indexModule=1;

    public CPPipe generatePipe(String originalString, ArrayList<String> arrayString) {
            CPPipe genPipe = new CPPipe();
            genPipe.header = (ArrayList<CPParam>) this.header.clone();
            indexModule=1;
            this.modules.forEach(m -> {
                if (m.containsString(originalString)) {
                    if (m.params.get(0).name.equals("NamesAndTypes")) {
                        CPModule m_str = m.clone();
                        m_str.replaceAndAddImages(originalString, arrayString);
                        genPipe.modules.add(m_str);
                        genPipe.modules.get(genPipe.modules.size()-1).setModuleNumber(indexModule);
                        indexModule++;
                    } else if (m.params.get(0).name.equals("MeasureImageIntensity")) {
                        CPModule m_str = m.clone();
                        m_str.replaceAndAddImageMeasures(originalString, arrayString);
                        genPipe.modules.add(m_str);
                        genPipe.modules.get(genPipe.modules.size()-1).setModuleNumber(indexModule);
                        indexModule++;
                    } else if (m.params.get(0).name.equals("ExportToSpreadsheet")) {
                        CPModule m_str = m.clone();
                        m_str.replaceAndAddMeasures(originalString, arrayString);
                        genPipe.modules.add(m_str);
                        genPipe.modules.get(genPipe.modules.size()-1).setModuleNumber(indexModule);
                        indexModule++;
                    } else {
                        for (String str : arrayString) {
                            CPModule m_str = m.clone();
                            m_str.replaceString(originalString, str);
                            genPipe.modules.add(m_str);
                            genPipe.modules.get(genPipe.modules.size()-1).setModuleNumber(indexModule);
                            indexModule++;
                        }
                    }
                } else {
                    genPipe.modules.add(m.clone());
                    genPipe.modules.get(genPipe.modules.size()-1).setModuleNumber(indexModule);
                    indexModule++;
                }

            });

            genPipe.header.forEach(p -> {
                if (p.name.equals("ModuleCount")) {
                    p.setValue(Integer.toString(genPipe.modules.size()));
                }
            });
            return genPipe;
    }

}
