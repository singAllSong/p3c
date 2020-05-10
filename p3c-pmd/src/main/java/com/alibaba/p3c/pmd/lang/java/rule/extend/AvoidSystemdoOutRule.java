package com.alibaba.p3c.pmd.lang.java.rule.extend;

import net.sourceforge.pmd.lang.ast.Node;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.AbstractXpathRule;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;

/**
 * @author Administrator
 * @date 2020年5月4日
 */
public class AvoidSystemdoOutRule extends AbstractXpathRule {

    private static final String XPATH = "//Name[starts-with(@Image,'System.out.print')]";

    public AvoidSystemdoOutRule() {
        setXPath(XPATH);
    }

    @Override
    public void addViolation(Object data, Node node, String arg) {
        ViolationUtils.addViolationWithPrecisePosition(this, node, data,
            I18nResources.getMessage("java.extend.ConsolePrint.rule.msg"));
    }
}
