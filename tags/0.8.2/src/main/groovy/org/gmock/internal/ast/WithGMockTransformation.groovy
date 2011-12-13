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
