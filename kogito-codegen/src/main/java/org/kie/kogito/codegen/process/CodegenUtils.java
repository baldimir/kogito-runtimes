package org.kie.kogito.codegen.process;

import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public class CodegenUtils {

    
    public static void interpolateArguments(MethodDeclaration md, String dataType) {
        md.getParameters().forEach(p -> p.setType(dataType));
    }
    
    public static void interpolateTypes(ClassOrInterfaceType t, String dataClazzName) {
        SimpleName returnType = t.asClassOrInterfaceType().getName();
        interpolateTypes(returnType, dataClazzName);
        t.getTypeArguments().ifPresent(ta -> interpolateTypeArguments(ta, dataClazzName));
    }

    public static void interpolateTypes(SimpleName returnType, String dataClazzName) {
        String identifier = returnType.getIdentifier();
        returnType.setIdentifier(identifier.replace("$Type$", dataClazzName));
    }

    public static void interpolateTypeArguments(NodeList<Type> ta, String dataClazzName) {
        ta.stream().map(Type::asClassOrInterfaceType)
                .forEach(t -> interpolateTypes(t, dataClazzName));
    }
    
    public static boolean isProcessField(FieldDeclaration fd) {
        return fd.getElementType().asClassOrInterfaceType().getNameAsString().equals("Process");
    }
    
    public static boolean isApplicationField(FieldDeclaration fd) {
        return fd.getElementType().asClassOrInterfaceType().getNameAsString().equals("Application");
    }
    
    public static MethodDeclaration extractOptionalInjection(String type, String fieldName, String defaultMethod, DependencyInjectionAnnotator annotator) {
        BlockStmt body = new BlockStmt();
        MethodDeclaration extractMethod = new MethodDeclaration()
                .addModifier(Keyword.PROTECTED)
                .setName("extract_" + fieldName)
                .setType(type)
                .setBody(body);
        Expression condition = annotator.optionalInstanceExists(fieldName);
        IfStmt valueExists = new IfStmt(condition, new ReturnStmt(new MethodCallExpr(new NameExpr(fieldName), "get")), new ReturnStmt(new NameExpr(defaultMethod)));
        body.addStatement(valueExists);
        return extractMethod;
    }
}
