package com.patrickwilson.ardm.processor;

import com.patrickwilson.ardm.api.annotation.Repository;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Iterator;
import java.util.Set;

/**
 * This is where the magic happens.  This annotation processor will wire together the repositories
 * automatically and provide any glue that is missing.
 * User: pwilson
 */
@SupportedAnnotationTypes({"com.patrickwilson.ardm.api.annotation.Repository"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RepositoryAnnotationProcessor extends AbstractProcessor {
    /**
     * handle all instances of the {@link com.patrickwilson.ardm.api.annotation.Repository }
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> clazzes = roundEnv.getElementsAnnotatedWith(Repository.class);
        Messager messager = processingEnv.getMessager();
        for (Element clazz: clazzes) {
             messager.printMessage(Diagnostic.Kind.WARNING, "Found Repository Annotation on element '" + clazz.getSimpleName() + "' of type: " + clazz.getKind().toString());
        }

        return true;
    }
}
