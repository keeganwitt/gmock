package org.gmock;


import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD])
@GroovyASTTransformationClass(["org.gmock.internal.ast.WithGMockTransformation"])
public @interface WithGMock {

}
