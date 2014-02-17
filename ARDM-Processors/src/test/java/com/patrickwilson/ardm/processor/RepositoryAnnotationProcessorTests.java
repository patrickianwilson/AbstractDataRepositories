package com.patrickwilson.ardm.processor;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.patrickwilson.ardm.api.annotation.Repository;
import org.junit.Assert;
import org.junit.Test;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Unit tests for the repository annotation processor.
 * User: pwilson
 */
public class RepositoryAnnotationProcessorTests {

    @Test
    public void testDidItProcess() throws URISyntaxException {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();


        StubJavaFileObject source = new StubJavaFileObject("StubSourceRepository");

        ArrayList<StubJavaFileObject> toCompile = Lists.newArrayList(source);
        StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, Locale.getDefault(), null);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        List<String> names = Lists.transform(toCompile, new Function<StubJavaFileObject, String>() {
            @Override
            public String apply(com.patrickwilson.ardm.processor.StubJavaFileObject input) {
                if (input != null) {
                    return input.getFqName();
                }

                return null;
            }
        });

        ArrayList<String> annotationProcessors = Lists.newArrayList("com.patrickwilson.ardm.processor.RepositoryAnnotationProcessor");


        JavaCompiler.CompilationTask compilerTask = compiler.getTask(null,stdFileManager, diagnostics, null, null , toCompile);
        compilerTask.setProcessors(Lists.newArrayList(new RepositoryAnnotationProcessor()));



        List<Diagnostic<? extends JavaFileObject>> unitReports = diagnostics.getDiagnostics();

        boolean success = compilerTask.call();


        for(Diagnostic<? extends JavaFileObject> unit: unitReports) {
            System.out.println(unit.toString());
        }

       Assert.assertTrue(success);


    }
}
