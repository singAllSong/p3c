package com.alibaba.p3c.pmd.lang.java.rule.extend;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jaxen.JaxenException;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;

import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * @author Administrator
 * @date 2020年5月4日
 */
public class LogLevelRule extends AbstractAliRule {

    private static final String MESSAGE_KEY_LOGLEVEL = "java.extend.LogLevelRule.rule.msg";
    private static final String MESSAGE_KEY_EDOGETMESSAGE = "java.extend.edogetMessage.rule.msg";
    private static final String MESSAGE_KEY_CONSOLE_PRINT = "java.extend.ConsolePrint.rule.msg";

    private static final String LOG_INFO = ".info";
    private static final String LOG_ERROR = ".error";
    private static final String CONSOLE_PRINT = "System.out.print";
    private static final String METHOD_BLOCK_STATEMENT_XPATH = "//Block/BlockStatement/Statement/StatementExpression/PrimaryExpression"
        + "/PrimaryPrefix/Name";
    
    private static final String TRY_CATCH_STATEMENT_XPATH = "//TryStatement/Block/../PrimaryExpression/PrimaryPrefix/Name[@Image='%s.error']"
        +"|//TryStatement/CatchStatement/../PrimaryExpression/PrimaryPrefix/Name[@Image='%s.info']";

    
    @Override
    public Object visit(ASTTryStatement node, Object data) {
        
         List<Node> nodeList;
        try {
            nodeList = node.findChildNodesWithXPath("//Block/BlockStatement/Statement/StatementExpression/PrimaryExpression/PrimaryPrefix/Name");
            if(null!=nodeList&&nodeList.size()>0){
                for (Node tryChildnode : nodeList) {
                    String imageName = tryChildnode.getImage();
                    if(StringUtils.isNotEmpty(imageName)&&imageName.endsWith(LOG_ERROR)){
                        addViolationWithMessage(data, node, MESSAGE_KEY_LOGLEVEL);
                    }
                }
            }
        } catch (JaxenException e) {
            return super.visit(node, data);
        }
        return super.visit(node, data);
    }
    
    @Override
    public Object visit(ASTCatchStatement node, Object data) {
         List<Node> nodeList;
         List<Node> catchList;
        try {
            nodeList = node.findChildNodesWithXPath("//Block/BlockStatement/Statement/StatementExpression/PrimaryExpression/PrimaryPrefix/Name");
            catchList = node.findChildNodesWithXPath("//PrimarySuffix");
            if(null!=nodeList&&nodeList.size()>0){
                for (Node catchChildnode : nodeList) {
                    String imageName = catchChildnode.getImage();
                    if(StringUtils.isNotEmpty(imageName)&&imageName.endsWith(LOG_INFO)){
                        addViolationWithMessage(data, node, MESSAGE_KEY_LOGLEVEL);
                    }
                    
                }
            }
            
            if(null!=catchList&&catchList.size()>0){
                for (Node astPrimarySuffix : catchList) {
                    List<? extends Node> astPrimaryPrefixList = astPrimarySuffix.findChildNodesWithXPath("//Expression/PrimaryExpression/PrimaryPrefix");
                    if(null!=astPrimaryPrefixList&&astPrimaryPrefixList.size()>0){
                        for (Node astPrimaryPrefix : astPrimaryPrefixList) {
                            String edoget = astPrimaryPrefix.jjtGetChild(0).getImage();
                            if(StringUtils.isNotEmpty(edoget)&&edoget.contains("e.getMessage")){
                                addViolationWithMessage(data, node, MESSAGE_KEY_LOGLEVEL);
                            }
                        }
                    }
                }
            }
            
        } catch (JaxenException e) {
            return super.visit(node, data);
        }
        return super.visit(node, data);
    }
    
    @Override
    public Object visit(ASTBlockStatement node, Object data) {
         List<Node> nodeList;
        try {
            nodeList = node.findChildNodesWithXPath("//Statement/StatementExpression/PrimaryExpression/PrimarySuffix/Name");
            if(null!=nodeList&&nodeList.size()>0){
                for (Node tryChildnode : nodeList) {
                    String imageName = tryChildnode.getImage();
                    if(StringUtils.isNotEmpty(imageName)&&imageName.endsWith(LOG_ERROR)){
                        addViolationWithMessage(data, node, MESSAGE_KEY_LOGLEVEL);
                    }
                }
            }
        } catch (JaxenException e) {
            return super.visit(node, data);
        }
        return super.visit(node, data);
    }
    
}
