package org.gmock.internal.ast

import org.gmock.WithGMock;
import org.codehaus.groovy.transform.*
import org.codehaus.groovy.control.*
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.ast.expr.*
import static org.objectweb.asm.Opcodes.*
import org.gmock.GMockController;
import java.lang.reflect.Array
import org.gmock.GMock

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class WithGMockTransformation implements ASTTransformation {

  public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {

      AnnotatedNode parent = (AnnotatedNode) nodes[1];

      if (!parent instanceof ClassNode){
          throw new RuntimeException("WithGMock need to be applied on a class level");
      }
      ClassNode cNode = (ClassNode) parent;

      Expression newGmockController = new ConstructorCallExpression(new ClassNode(GMockController), MethodCallExpression.NO_ARGUMENTS);
      cNode.addField("\$gmockController", ACC_PROTECTED, new ClassNode(GMockController),  newGmockController);

      def mockArgs = ["args": new ClassNode(Object).makeArray()]
      delegateToController(cNode, "mock", mockArgs)

      def playArgs = ["closure": new ClassNode(Closure)]
      delegateToController(cNode, "play", playArgs)

      def withArgs = ["mock": new ClassNode(Object), "closure": new ClassNode(Closure)]
      delegateToController(cNode, "with", withArgs)

      def orderedArgs = ["closure": new ClassNode(Closure)]
      delegateToController(cNode, "ordered", orderedArgs)

      def unorderedArgs = ["closure": new ClassNode(Closure)]
      delegateToController(cNode, "unordered", unorderedArgs)

      def matchArgs = ["closure": new ClassNode(Closure)]
      delegateToGmock(cNode, "match", matchArgs)

      def constructorArgs = ["args": new ClassNode(Object).makeArray()]
      delegateToGmock(cNode, "constructor", constructorArgs)

      def invokeConstructorArgs = ["args": new ClassNode(Object).makeArray()]
      delegateToGmock(cNode, "invokeConstructor", invokeConstructorArgs)

      def nameArgs = ["name": new ClassNode(String)]
      delegateToGmock(cNode, "name", nameArgs)

  }

  private delegateToGmock(ClassNode cNode, String methodName, Map args){
      final BlockStatement body = new BlockStatement();

      def arguments = args.keySet().collect { new VariableExpression(it) }
      def mockExpression = new StaticMethodCallExpression(
              new ClassNode(GMock),
              methodName,
              new ArgumentListExpression( arguments as VariableExpression[] ))
      body.addStatement(new ExpressionStatement(mockExpression))

      def params = args.entrySet().collect { new Parameter(it.value, it.key)}
      cNode.addMethod(new MethodNode(methodName, ACC_PROTECTED, ClassHelper.OBJECT_TYPE, params as Parameter[], ClassNode.EMPTY_ARRAY, body))
  }



  private delegateToController(ClassNode cNode, String methodName, Map args){
      final BlockStatement body = new BlockStatement();

      def arguments = args.keySet().collect { new VariableExpression(it) }

      def mockExpression = new MethodCallExpression(
              new VariableExpression("\$gmockController"),
              new ConstantExpression(methodName),
              new ArgumentListExpression( arguments as VariableExpression[] )
          )

      body.addStatement(new ExpressionStatement(mockExpression))

      def params = args.entrySet().collect { new Parameter(it.value, it.key)}
      cNode.addMethod(new MethodNode(methodName, ACC_PROTECTED, ClassHelper.OBJECT_TYPE, params as Parameter[], ClassNode.EMPTY_ARRAY, body))
  }



}
