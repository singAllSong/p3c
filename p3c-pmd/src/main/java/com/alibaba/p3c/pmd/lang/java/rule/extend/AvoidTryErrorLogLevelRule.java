package com.alibaba.p3c.pmd.lang.java.rule.extend;

import net.sourceforge.pmd.lang.ast.Node;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.AbstractXpathRule;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;

/**
 * @author Administrator
 * @date 2020年5月4日
 */
public class AvoidTryErrorLogLevelRule extends AbstractXpathRule {

    private static final String XPATH = "//TryStatement/Block//PrimaryPrefix/Name[ends-with(@Image,'.error')]";

    public AvoidTryErrorLogLevelRule() {
        setXPath(XPATH);
    }

    @Override
    public void addViolation(Object data, Node node, String arg) {
        ViolationUtils.addViolationWithPrecisePosition(this, node, data,
            I18nResources.getMessage("java.extend.LogLevelRule.rule.msg"));
    }
}
