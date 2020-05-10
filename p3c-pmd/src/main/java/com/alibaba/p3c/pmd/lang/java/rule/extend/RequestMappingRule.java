package com.alibaba.p3c.pmd.lang.java.rule.extend;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTNormalAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTSingleMemberAnnotation;

import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;

/**
 * @author Administrator
 * @date 2020年5月10日
 */
public class RequestMappingRule extends AbstractAliRule {
    private static final String IMPORT_XPATH =
        "//ImportDeclaration[@ImportedName='org.springframework.web.bind.annotation.RequestMapping']";
    private static final String REQUESTMAPPING_XPATH =
        "//ClassOrInterfaceBody//Annotation[@AnnotationName='RequestMapping']";
    private static final String METHOD_XPATH = "MemberValuePairs/MemberValuePair[@MemberName='method']";

    /**
     * controller层requestmethod方法的规则集
     * 
     * @see net.sourceforge.pmd.lang.java.rule.AbstractJavaRule#visit(net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression,
     *      java.lang.Object)
     */
    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        try {
            List<Node> importNodes = node.findChildNodesWithXPath(IMPORT_XPATH);
            if (null != importNodes && importNodes.size() > 0) {
                List<Node> resquestMappingNodes = node.findChildNodesWithXPath(REQUESTMAPPING_XPATH);
                Node annotation = null;
                for (Node resquestMappingNode : resquestMappingNodes) {
                    annotation = resquestMappingNode.jjtGetChild(0);
                    if (annotation instanceof ASTSingleMemberAnnotation) {
                        addViolationWithMessage(data, resquestMappingNode, "java.extend.RequestMappingRule.rule.msg",
                            new Object[] {});
                    } else if (annotation instanceof ASTNormalAnnotation) {
                        if (!annotation.hasDescendantMatchingXPath(METHOD_XPATH)) {
                            addViolationWithMessage(data, resquestMappingNode, "java.extend.RequestMappingRule.rule.msg",
                                new Object[] {});

                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return super.visit(node, data);
    }
}
