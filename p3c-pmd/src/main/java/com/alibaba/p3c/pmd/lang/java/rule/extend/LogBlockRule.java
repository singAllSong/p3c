package com.alibaba.p3c.pmd.lang.java.rule.extend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;

import org.jaxen.JaxenException;

import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;

/**
 * 敏感信息日志检测
 * 
 * @author Administrator
 * @date 2020年5月16日
 */
public class LogBlockRule extends AbstractAliRule {
    private static Set<String> SensitiveStrings = new HashSet<String>();
    private List<ASTName> astNamewithLog = (List<ASTName>)new ArrayList<ASTName>();
    private List<ASTName> sastNames = (List<ASTName>)new ArrayList<ASTName>();
    private List<ASTVariableDeclaratorId> sensitiveVariables =
        (List<ASTVariableDeclaratorId>)new ArrayList<ASTVariableDeclaratorId>();
    private List<String> booleanStrings = new ArrayList<String>();

    static {
        SensitiveStrings.add("classname");
        SensitiveStrings.add("pid");
        SensitiveStrings.add("uid");
        SensitiveStrings.add("imei");
        SensitiveStrings.add("getLocalClassName");
        SensitiveStrings.add("getPackageCodePath");
        SensitiveStrings.add("getPackagePath");
        SensitiveStrings.add("phone");
        SensitiveStrings.add("cardNo");
        SensitiveStrings.add("password");
        SensitiveStrings.add("certNo");
        SensitiveStrings.add("idCard");
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        checkLogRule(node, data);
        return super.visit(node, data);
    }

    @SuppressWarnings("unchecked")
    private void checkLogRule(Node node, Object data) {
        
        String xpathBoolean =
            ".//FieldDeclaration/VariableDeclarator/VariableInitializer/Expression/PrimaryExpression"
                + "/PrimaryPrefix/Literal/BooleanLiteral[@True='true']";
        // 遍历找出源代码中的log.*代码
        pickUpLogMethods(node);
        if (!this.astNamewithLog.isEmpty()) {
            try {
                monitorFirstLayer(node, data, xpathBoolean);

                monitorSecondLayer(node, data);
            } catch (JaxenException e) {
                e.printStackTrace();
            } finally {
                this.astNamewithLog.clear();
                this.sastNames.clear();
                this.booleanStrings.clear();
                this.sensitiveVariables.clear();
            }
        }
    }

    /**
     * @param node
     * @param data
     */
    private void monitorSecondLayer(Node node, Object data) {
        // 第二层敏感信息监测
        List<ASTVariableDeclaratorId> variableDeclaratorIds =
            node.findDescendantsOfType(ASTVariableDeclaratorId.class);
        // 找出定义的所有变量
        if (variableDeclaratorIds.size() > 0) {
            for (ASTVariableDeclaratorId variableDeclaratorId : variableDeclaratorIds) {
                ASTType type = variableDeclaratorId.getTypeNode();
                if (!(type.jjtGetParent() instanceof ASTFormalParameter)) {
                    ASTName astName =
                        variableDeclaratorId.getFirstParentOfType(ASTVariableDeclarator.class)
                            .getFirstDescendantOfType(ASTName.class);
                    if (astName != null) {
                        if (checkIsSensitiveString(astName.getImage())) {
                            this.sensitiveVariables.add(variableDeclaratorId);
                        }
                    }
                }
            }

            if (this.sensitiveVariables.size() > 0) {
                for (ASTVariableDeclaratorId sensitiveVariable : this.sensitiveVariables) {
                    for (ASTName secondastName : this.sastNames) {
                        String astNameimage = secondastName.getImage();
                        if (!(hasNullInitializer(sensitiveVariable)) && astNameimage != null
                            && sensitiveVariable.getImage().equalsIgnoreCase(astNameimage)) {
                            // 被if判断包围
                            ASTIfStatement ifStatement =
                                secondastName.getFirstParentOfType(ASTIfStatement.class);
                            if (ifStatement != null) {
                                ASTExpression astExpression =
                                    ifStatement.getFirstDescendantOfType(ASTExpression.class);
                                ASTName astName3 = astExpression.getFirstDescendantOfType(ASTName.class);
                                if (astName3 != null) {
                                    String astNameString = astName3.getImage();
                                    // 判断条件的值为true
                                    if (this.booleanStrings.size() > 0
                                        && this.booleanStrings.contains(astNameString)) {
                                        addViolationWithMessage(data, secondastName,
                                            "java.extend.logsensitive.rule.msg");
                                    }
                                }
                                // 没有被if判断包围，触发规则
                            } else {
                                addViolationWithMessage(data, secondastName,
                                    "java.extend.logsensitive.rule.msg");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param node
     * @param data
     * @param xpathBoolean
     * @throws JaxenException
     */
    private void monitorFirstLayer(Node node, Object data, String xpathBoolean) throws JaxenException {
        List<ASTBooleanLiteral> xpathBooleanStringNames =
            (List<ASTBooleanLiteral>)node.findChildNodesWithXPath(xpathBoolean);
        if (xpathBooleanStringNames.size() > 0) {
            for (ASTBooleanLiteral booleanLiteral : xpathBooleanStringNames) {
                ASTVariableDeclarator variableDeclarator =
                    booleanLiteral.getFirstParentOfType(ASTVariableDeclarator.class);
                ASTVariableDeclaratorId variableDeclaratorId =
                    variableDeclarator.getFirstChildOfType(ASTVariableDeclaratorId.class);
                this.booleanStrings.add(variableDeclaratorId.getImage());
            }
        }
        List<ASTName> xpathLogNames = this.astNamewithLog;
        if (xpathLogNames.size() > 0) {
            for (ASTName name : xpathLogNames) {
                String imageString = name.getImage();
                boolean bool = imageString.contains("log.") || imageString.contains("logger.");
                if (imageString != null && bool) {
                    ASTIfStatement ifStatement = name.getFirstParentOfType(ASTIfStatement.class);
                    ASTBlockStatement blockStatement = name.getFirstParentOfType(ASTBlockStatement.class);
                    List<ASTName> names2 = (List<ASTName>)blockStatement.findDescendantsOfType(ASTName.class);

                    if (names2.size() > 0) {
                        for (ASTName name2 : names2) {
                            if (name2 != null) {
                                String imageString2 = name2.getImage();

                                boolean sflag = checkIsSensitiveString(imageString2);
                                // 当前没发现包含敏感信息，把该ASTName节点存储后续解析
                                if (!sflag) {
                                    this.sastNames.add(name2);
                                }
                                // 当前发现包含敏感信息，确认是否被if包围
                                if (sflag) {
                                    // 被if判断包围，确认判断条件是否为true
                                    if (ifStatement != null) {
                                        ASTExpression astExpression =
                                            ifStatement.getFirstDescendantOfType(ASTExpression.class);
                                        ASTName astName = astExpression.getFirstDescendantOfType(ASTName.class);
                                        if (astName != null) {
                                            String astNameString = astName.getImage();
                                            // 判断条件的值为true
                                            if (this.booleanStrings.size() > 0
                                                && this.booleanStrings.contains(astNameString)) {
                                                addViolationWithMessage(data, name2,
                                                    "java.extend.logsensitive.rule.msg");
                                            }
                                        }
                                    } else {
                                        // 没有被if判断包围，触发规则
                                        addViolationWithMessage(data, name2,
                                            "java.extend.logsensitive.rule.msg");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private boolean checkIsSensitiveString(String imageString2) {
        for (String sensitiveString : SensitiveStrings) {
            if (imageString2.equalsIgnoreCase(sensitiveString)) {
                return true;
            }
            if (imageString2 != null && imageString2.contains(".")) {
                String[] partStrings = imageString2.split("\\.");
                int lastIndex = partStrings.length - 1;
                if ("length".equals(partStrings[lastIndex])|| "size".equals(partStrings[lastIndex])) {
                    return false;
                } else {
                    for (int i = 0; i < partStrings.length; i++) {
                        String partString = partStrings[i];
                        if (partString.equalsIgnoreCase(sensitiveString)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean hasNullInitializer(ASTVariableDeclaratorId var) {
        ASTVariableInitializer init = var.getFirstDescendantOfType(ASTVariableInitializer.class);
        if (init != null) {
            try {
                List<?> nulls =
                    init.findChildNodesWithXPath("Expression/PrimaryExpression/PrimaryPrefix/Literal/NullLiteral");
                return !nulls.isEmpty();
            } catch (JaxenException e) {
                return false;
            }
        }
        return false;
    }

    private void pickUpLogMethods(Node node) {
        // 遍历找出源代码中的log.*代码
        List<ASTStatementExpression> pexs = node.findDescendantsOfType(ASTStatementExpression.class);

        for (ASTStatementExpression ast : pexs) {
            ASTPrimaryPrefix primaryPrefix = ast.jjtGetChild(0).getFirstDescendantOfType(ASTPrimaryPrefix.class);
            if (primaryPrefix != null) {
                ASTName name = primaryPrefix.getFirstChildOfType(ASTName.class);
                if (name != null) {
                    String imageString = name.getImage();
                    if (imageString.startsWith("log.") || imageString.startsWith("logger.")) {
                        astNamewithLog.add(name);
                    }
                }
            }
        }
    }
}