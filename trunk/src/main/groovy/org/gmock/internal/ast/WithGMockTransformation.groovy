/*
 * Copyright 2008-2009 Julien Gagnet, Johnny Jian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gmock.internal.ast

import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.gmock.GMock
import org.gmock.GMockController
import org.codehaus.groovy.ast.*
import static org.codehaus.groovy.ast.ClassHelper.*
import static org.codehaus.groovy.ast.ClassNode.EMPTY_ARRAY
import org.codehaus.groovy.ast.expr.*
import static org.codehaus.groovy.ast.expr.MethodCallExpression.NO_ARGUMENTS
import static org.objectweb.asm.Opcodes.*

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class WithGMockTransformation implements ASTTransformation {

    private static final ClassNode GMOCK_CONTROLLER_TYPE = new ClassNode(GMockController)
    private static final ClassNode GMOCK_TYPE = new ClassNode(GMock)
    private static final ClassNode OBJECT_ARRAY_TYPE = OBJECT_TYPE.makeArray()
    private static final ClassNode CLOSURE_TYPE = new ClassNode(Closure)

    private static final ClassNode TESTNG_BEFORE_METHOD_TYPE
    
    static {
        try {
            def testNGBeforeMethodClass = Class.forName('org.testng.annotations.BeforeMethod')
            if (testNGBeforeMethodClass) {
                TESTNG_BEFORE_METHOD_TYPE = new ClassNode(testNGBeforeMethodClass)
            }
        } catch (e) {
            TESTNG_BEFORE_METHOD_TYPE = null
        }
    }

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        AnnotatedNode parent = (AnnotatedNode) nodes[1]

        if (!(parent instanceof ClassNode)) {
            throw new RuntimeException("@WithGMock need to be applied on a class level.")
        }
        ClassNode cNode = (ClassNode) parent

        if (cNode.isInterface()) {
            throw new RuntimeException('@WithGMock cannot be applied on an interface.')
        }

        Expression newGmockController = new ConstructorCallExpression(GMOCK_CONTROLLER_TYPE, NO_ARGUMENTS)
        cNode.addField('$gmockController', ACC_PRIVATE, GMOCK_CONTROLLER_TYPE, newGmockController)

        delegateToController(cNode, "mock", [args: OBJECT_ARRAY_TYPE])
        delegateToController(cNode, "play", [closure: CLOSURE_TYPE])
        delegateToController(cNode, "with", [mock: OBJECT_TYPE, closure: CLOSURE_TYPE])
        delegateToController(cNode, "ordered", [closure: CLOSURE_TYPE])
        delegateToController(cNode, "unordered", [closure: CLOSURE_TYPE])

        delegateToGmock(cNode, "match", [closure: CLOSURE_TYPE])
        delegateToGmock(cNode, "constructor", [args: OBJECT_ARRAY_TYPE])
        delegateToGmock(cNode, "invokeConstructor", [args: OBJECT_ARRAY_TYPE])
        delegateToGmock(cNode, "name", [name: STRING_TYPE])

        if (isTestNGTest(cNode)) {
            addDependencyToBeforeMethods(cNode)
            addTestNGBeforeMethod(cNode)
        }
    }

    private addDependencyToBeforeMethods(ClassNode cNode) {
        cNode.methods.each { MethodNode method ->
            method.annotations.each { AnnotationNode annotation ->
                if (annotation.classNode.name == 'org.testng.annotations.BeforeMethod') {
                    def createGMockController = new ConstantExpression('$createGMockController')
                    def dependsOnMethods = annotation.getMember('dependsOnMethods')
                    if (dependsOnMethods == null) {
                        dependsOnMethods = new ListExpression([createGMockController])
                    } else if (dependsOnMethods instanceof ListExpression) {
                        dependsOnMethods.addExpression(createGMockController)
                    } else if (dependsOnMethods instanceof ConstantExpression) {
                        dependsOnMethods = new ListExpression([dependsOnMethods, createGMockController])
                    }
                    annotation.setMember('dependsOnMethods', dependsOnMethods)
                }
            }
        }
    }

    private addTestNGBeforeMethod(ClassNode cNode) {
        def gmockController = new VariableExpression('$gmockController')
        def assign = new Token(Types.ASSIGN, "=", -1, -1)
        def constructorCall = new ConstructorCallExpression(GMOCK_CONTROLLER_TYPE, NO_ARGUMENTS)

        def body = new BlockStatement()
        body.addStatement(new ExpressionStatement(new BinaryExpression(gmockController, assign, constructorCall)))

        def method = new MethodNode('$createGMockController', ACC_PUBLIC, VOID_TYPE, new Parameter[0], EMPTY_ARRAY, body)
        def annotation = new AnnotationNode(TESTNG_BEFORE_METHOD_TYPE)
        annotation.setMember('alwaysRun', ConstantExpression.TRUE)
        method.addAnnotation(annotation)

        cNode.addMethod(method)
    }

    private boolean isTestNGTest(ClassNode cNode) {
        return hasTestNGTestAnnotation(cNode) && isTestNGBeforeMethodAnnotationAvailable()
    }

    private boolean hasTestNGTestAnnotation(ClassNode cNode) {
        cNode.methods.any { MethodNode method ->
            method.annotations.any { AnnotationNode annotation ->
                annotation.classNode.name == 'org.testng.annotations.Test'
            }
        }
    }

    private boolean isTestNGBeforeMethodAnnotationAvailable() {
        TESTNG_BEFORE_METHOD_TYPE != null
    }

    private delegateToGmock(ClassNode cNode, String methodName, Map args) {
        addMethod(cNode, methodName, args, StaticMethodCallExpression, GMOCK_TYPE)
    }

    private delegateToController(ClassNode cNode, String methodName, Map args) {
        addMethod(cNode, methodName, args, MethodCallExpression, new VariableExpression('$gmockController'))
    }

    private addMethod(ClassNode cNode, String methodName, Map args, Class expressionClass, Object object) {
        def params = args.entrySet().collect { new Parameter(it.value, it.key) } as Parameter[]
        if (!cNode.getMethod(methodName, params)) { // add the method only if it is not exists 
            def arguments = args.keySet().collect { new VariableExpression(it) } as VariableExpression[]
            def mockExpression = expressionClass.newInstance(object, methodName, new ArgumentListExpression(arguments))

            final BlockStatement body = new BlockStatement()
            body.addStatement(new ExpressionStatement(mockExpression))

            cNode.addMethod(new MethodNode(methodName, ACC_PROTECTED, OBJECT_TYPE, params, EMPTY_ARRAY, body))
        }
    }

}
