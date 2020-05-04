package com.alibaba.p3c.pmd.lang.java.rule.extend;

import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;

import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;

/**
 * @author Administrator
 * @date 2020年5月3日
 */
public final class MethodParamsNumRule extends AbstractAliRule {

    private static final int PARAMSNUM = 5;

    @Override
    public Object visit(ASTFormalParameters node, Object data) {
        if (node.jjtGetNumChildren() >= PARAMSNUM) {
            addViolationWithMessage(data, node, "java.extend.MethodParamsNumRule.rule.msg");
        }
        return super.visit(node, data);
    }

}
