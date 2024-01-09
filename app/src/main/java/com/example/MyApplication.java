package com.example;

import org.apache.commons.lang3.StringUtils;

public class MyApplication {
    public static void main(String[] args) {
        StringUtils.capitalize("");
        new PrintService().print(new MessageModel("Hi !:)"));
    }
}
