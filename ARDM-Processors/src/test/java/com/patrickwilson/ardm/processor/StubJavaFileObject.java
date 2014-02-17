package com.patrickwilson.ardm.processor;

import org.springframework.context.annotation.Scope;
import sun.misc.IOUtils;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

/**
 * a wrapper for test stub java files.
 * User: pwilson
 */
public class StubJavaFileObject extends SimpleJavaFileObject {

    private String name;
    private String fqName;
    private URL content;
    public StubJavaFileObject(String name) throws URISyntaxException {
        super(URI.create("String:///com/patrickwilson/mock/sources/" + name + Kind.SOURCE.extension), Kind.SOURCE);
        this.content = ClassLoader.getSystemClassLoader().getResource("com/patrickwilson/mock/sources/" + name + ".java_source");
        this.name = name;
        this.fqName = "com.patrickwilson.mock.sources." + name + ".java";


    }

    public String getName() {
        return name;
    }

    public String getFqName() {
        return fqName;
    }

    /**
     * Return the content from the URI stream.
     */
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        InputStream bytesRead = content.openStream();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        StringBuilder source = new StringBuilder();
        Scanner scan = new Scanner(bytesRead);
        while (scan.hasNextLine()) {
            source.append(scan.nextLine()).append('\n');
        }

        return source.toString();
    }
}
