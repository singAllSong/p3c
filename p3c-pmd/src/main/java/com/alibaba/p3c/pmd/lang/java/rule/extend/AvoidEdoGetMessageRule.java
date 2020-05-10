package com.alibaba.p3c.pmd.lang.java.rule.extend;

import net.sourceforge.pmd.lang.ast.Node;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.AbstractXpathRule;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;

/**
 * @author Administrator
 * @date 2020年5月4日
 */
public class AvoidEdoGetMessageRule extends AbstractXpathRule {

    private static final String XPATH = "//CatchStatement/Block//PrimaryPrefix/Name[starts-with(@Image,'e.getMessage')]";

    public AvoidEdoGetMessageRule() {
        setXPath(XPATH);
    }

    @Override
    public void addViolation(Object data, Node node, String arg) {
        ViolationUtils.addViolationWithPrecisePosition(this, node, data,
            I18nResources.getMessage("java.extend.edogetMessage.rule.msg"));
    }
}
