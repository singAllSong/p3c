package com.alibaba.p3c.pmd.lang.java.rule.extend;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class AliCodeExtendRuleTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-ali-extend";

    @Override
    public void setUp() {
        //addRule(RULESET, "MethodParamsNumRule");
        //addRule(RULESET, "NoMapParamTypeRule");
        //addRule(RULESET, "AvoidTryErrorLogLevelRule");
        //addRule(RULESET, "AvoidCatchInfoLogLevelRule");
        //addRule(RULESET, "AvoidCommonErrorLogLevelRule");
        
        //addRule(RULESET, "AvoidSystemdoOutRule");
        //addRule(RULESET, "AvoidEdoGetMessageRule");
        addRule(RULESET, "RequestMappingRule");
        
        
        
    }
}
