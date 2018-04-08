package com.example.hubbler_sudesh.dynamicform;

/**
 * Created by hubbler-sudesh on 22/03/2018 AD.
 */

public class FormObject {

    String fieldName;
    String value;
    String type;
    boolean required;
    FieldOptions options;

    private class FieldOptions{
        String defaultOption;

    }

    public FormObject(String fieldName,String value ,String type, boolean required) {
        this.fieldName = fieldName;
        this.value = value;
        this.type = type;
        this.required = required;
    }


}
