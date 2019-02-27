package ch.epfl.biop.lima.cppipe;

public class CPParam implements Cloneable {
    String name;
    String value;

    public String toString() {
        return name+":"+getReadableValue();
    }

    public CPParam clone() {
        return new CPParam(name,value);
    }

    public CPParam(String name, String value) {
        this.name=name;
        this.value=value;
    }

    public CPParam(String name, String value, boolean isReadable) {
        if (isReadable) {
            this.name = name;
            this.value = this.getReEncodedValue(value);
        } else {
            this.name = name;
            this.value = value;
        }
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getReadableValue() {
            // Terrible hacky stuff
            String convertedValue = value;
            if (name.startsWith(" ")) {
                if (convertedValue.length()>=8) {
                    convertedValue = convertedValue.substring(8); // removes \xff\xfe
                    if (convertedValue.startsWith("\\x5B")) {
                        convertedValue = convertedValue.substring(4);
                        convertedValue = "[" + convertedValue;
                    }
                    convertedValue = convertedValue.replaceAll("\\\\x00\\\\x5D", "]");
                    convertedValue = convertedValue.replaceAll("\\\\x00\\\\x5B", "[");
                    convertedValue = convertedValue.replaceAll("\\\\x00\\\\x3A", ":");
                    convertedValue = convertedValue.replaceAll("\\\\x00\\\\x7C", "|");
                    convertedValue = convertedValue.replaceAll("\\\\x00", "");
                }
            } else {
                convertedValue = convertedValue.replaceAll("\\\\x5D", "]");
                convertedValue = convertedValue.replaceAll("\\\\x5B", "[");
            }

            return convertedValue;
    }

    public String getReEncodedValue(String value) {
        String newValue = value;
        // Reencode into terrible format
        if (name.startsWith(" ")) {
            if (value.length()==0) {
                newValue = "\\xff\\xfe";
            }
            if (value.length()>=1) {
                String firstChar = newValue.substring(0,1);
                newValue = newValue.substring(1).replaceAll("(\\w)","\\\\x00$1");
                newValue = newValue.replaceAll("(\\(|\\)|\"|,|\\.|\\,| |-|}|\\{|&|<|>|/|\\?|\\*|$)","\\\\x00$1");
                newValue = newValue.replaceAll("\\$","\\\\x00\\$");
                newValue = newValue.replaceAll("\\\\\\\\","\\\\x00\\\\\\\\");
                newValue = newValue.replaceAll("\\\\'","\\\\x00\\\\'");
                newValue = newValue.replaceAll("\\[","\\\\x00\\\\x5B");
                newValue = newValue.replaceAll("\\]","\\\\x00\\\\x5D");
                newValue = newValue.replaceAll("\\|","\\\\x00\\\\x7C");
                newValue = newValue.replaceAll(":","\\\\x00\\\\x3A");
                if (firstChar.equals("[")) {
                    firstChar = "\\x5B";
                }
                newValue = "\\xff\\xfe"+firstChar+newValue;
            }
        } else {
            newValue = newValue.substring(1, newValue.length()-1);
            newValue = newValue.replaceAll( "\\]", "\\\\x5D");
            newValue = newValue.replaceAll( "\\[", "\\\\x5B");
            newValue = "["+newValue+"]";
        }

        this.value = newValue;
        return newValue;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setReadableValue(String value) {
        this.value = this.getReEncodedValue(value);
    }

}
