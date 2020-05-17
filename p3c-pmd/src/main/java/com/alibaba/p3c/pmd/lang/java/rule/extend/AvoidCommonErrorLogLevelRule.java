package com.alibaba.p3c.pmd.lang.java.rule.extend;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;

import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;

/**
 * @author Administrator
 * @date 2020年5月4日
 */
public class AvoidCommonErrorLogLevelRule extends AbstractAliRule {

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        
        try {
            List<Node> nodes = node.findChildNodesWithXPath("//Block//PrimaryExpression/PrimaryPrefix//Name[ends-with(@Image,'.error')]");
            if(null!=nodes&&nodes.size()>0){
                for (Node node2 : nodes) {
                    List<ASTTryStatement> parentList = node2.getParentsOfType(ASTTryStatement.class);
                    if((parentList.size()==0)&&node2.getImage().endsWith(".error")){
                        addViolationWithMessage(data, node, "java.extend.LogLevelRule.rule.msg");
                    }
                }
            }
        } catch (Exception e) {
            return super.visit(node, data);
        }
        return super.visit(node, data);
    }
}
