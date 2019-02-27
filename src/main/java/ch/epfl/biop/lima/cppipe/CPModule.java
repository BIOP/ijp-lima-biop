package ch.epfl.biop.lima.cppipe;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CPModule implements Cloneable {
    
    public ArrayList<CPParam> params = new ArrayList<>();

    public boolean containsString(String key) {
        return params.stream().anyMatch(p -> p.getReadableValue().contains(key));
    }

    public CPModule clone() {
        CPModule cloned = new CPModule();
        params.forEach(p -> cloned.params.add(p.clone()));
        return cloned;
    }

    public void replaceString(String o, String n) {
        params.forEach(p -> {
            p.setReadableValue(
                    p.getReadableValue().replaceAll(o,n)
            );
        });
    }

    public void replaceAndAddImages(String key, ArrayList<String> arrayString) {
        // Adds images in NamesAndTypes module
        assert this.params.get(4).name.equals("Match metadata");
        String iniMatchMetadataValue = this.params.get(4).getReadableValue();
        //---- Updating NamesAndTypes module - Match metadata module

        System.out.println("iniMatchMetadataValue"+iniMatchMetadataValue);
        if (iniMatchMetadataValue.contains(key)) {

            Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
            Matcher matcher = pattern.matcher(iniMatchMetadataValue);

            String newValue="";
            while (matcher.find()) {
                String toModify = matcher.group(1);
                String[] parts = toModify.split(",");

                String out = "";

                for (int i=0;i<parts.length;i++) {
                    if (parts[i].contains(key)) {
                        for (int j=0;j<arrayString.size();j++) {
                            String str = arrayString.get(arrayString.size()-j-1);
                            out += parts[i].replaceAll(key, str) + ",";
                        }
                    } else {
                        out+=parts[i]+",";
                    }
                }

                out = out.substring(0,out.length()-1);
                newValue = newValue+"{"+out+"}, ";
            }
            newValue = newValue.substring(0,newValue.length()-2);
            newValue = "["+newValue+"]";

            System.out.println("newValue"+newValue);
            this.params.get(4).setReadableValue(newValue);
        }

        //---- Updating NamesAndTypes module - Adds
        // Assuming this model of data:
        //
        /*Select the rule criteria:and (file does contain "gfp_100ms.tif")
        Name to assign these images:protein_raw_100ms
        Name to assign these objects:Object4
        Select the image type:Grayscale image
        Set intensity range from:Image bit-depth
        Maximum intensity:255.0*/
        //ArrayList<CPParam> paramsForImageSet = new ArrayList<>();
        boolean found = false;
        int paramIndex = 5;
        while ((paramIndex<params.size())&&(found==false)) {
            found = params.get(paramIndex).name.equals("    Select the rule criteria");
            if (found) {
                found = ((found) && (params.get(paramIndex).getReadableValue().contains(key)));
            }
            paramIndex++;
        }

        if (found) {
            // We found the index
            paramIndex--;
            ArrayList<CPParam> paramsForImageSet = new ArrayList<>();
            paramsForImageSet.add(params.get(paramIndex).clone());
            paramsForImageSet.add(params.get(paramIndex+1).clone());
            paramsForImageSet.add(params.get(paramIndex+2).clone());
            paramsForImageSet.add(params.get(paramIndex+3).clone());
            paramsForImageSet.add(params.get(paramIndex+4).clone());
            paramsForImageSet.add(params.get(paramIndex+5).clone());

            params.remove(paramIndex);
            params.remove(paramIndex);
            params.remove(paramIndex);
            params.remove(paramIndex);
            params.remove(paramIndex);
            params.remove(paramIndex);

            ArrayList<CPParam> fullParams = new ArrayList<>();
            for (String str:arrayString) {
                for (CPParam p : paramsForImageSet) {
                    CPParam pmodif = p.clone();
                    pmodif.setReadableValue(
                            p.getReadableValue().replaceAll(key, str)
                    );
                    fullParams.add(pmodif);
                }
            }
            params.addAll(paramIndex,fullParams);

            // Now counts all images
            int numberOfAssignements = 0;
            for (CPParam p:params) {
                if (p.name.equals("    Select the rule criteria")) {
                    numberOfAssignements++;
                }
            }
            for (CPParam p:params) {
                if (p.name.equals("    Assignments count")) {
                    p.setReadableValue(Integer.toString(numberOfAssignements));
                }
            }

        }
    }

    public void replaceAndAddMeasures(String key, ArrayList<String> arrayString) {
        for (CPParam p: this.params) {
            if (p.name.equals("    Press button to select measurements")) {
                // Need to add the new measurements
                String[] measures_in = p.getReadableValue().split("\\|");
                String newReadableValue = "";

                for (int i=0;i<measures_in.length;i++) {
                    String m = measures_in[i];
                    if (m.contains(key)) {
                        for (String nm:arrayString) {
                            newReadableValue+=m.replaceAll(key, nm)+"|";
                        }
                    } else {
                        newReadableValue+=m+"|";
                    }
                }
                newReadableValue = newReadableValue.substring(0,newReadableValue.length()-1);
                p.setReadableValue(newReadableValue);
            }
        }
    }

    public void replaceAndAddImageMeasures(String key, ArrayList<String> arrayString) {
        ArrayList<CPParam> newparams = new ArrayList<>();
        for (int ip =0; ip<params.size();ip++) {
            CPParam p = this.params.get(ip);
                if ((p.name.equals("    Select the image to measure"))&&(p.getReadableValue().contains(key))) {
                    // Need to replace and duplicate the next two lines, then skip the next two
                    for (String s:arrayString) {
                        CPParam np = p.clone();
                        np.setReadableValue(
                                p.getReadableValue().replaceAll(key,s)
                        );
                        // Copy then skips next two lines
                        newparams.add(np);
                        newparams.add(this.params.get(ip+1).clone());
                        newparams.add(this.params.get(ip+2).clone());
                    }
                    ip+=2;

                } else {
                    newparams.add(p.clone());
                }
        }
        this.params = newparams;
    }

    public void setModuleNumber(int i) {
        String value = this.params.get(0).getValue();
        value = value.replaceAll("(\\[module_num:)(\\d+)(.*)","$1"+Integer.toString(i)+"$3");
        this.params.get(0).setValue(value);
    }

}
