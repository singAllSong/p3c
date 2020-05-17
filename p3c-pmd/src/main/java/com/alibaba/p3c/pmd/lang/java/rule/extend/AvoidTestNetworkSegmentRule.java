package com.alibaba.p3c.pmd.lang.java.rule.extend;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;

import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;

/**
 * @author Administrator
 * @date 2020年5月4日
 */
public class AvoidTestNetworkSegmentRule extends AbstractAliRule {
    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        
        try {
            List<Node> nodes = node.findChildNodesWithXPath("//Literal");
            if(null!=nodes&&nodes.size()>0){
                for (Node node2 : nodes) {
                    String image = node2.getImage().replaceAll("\"", "");
                    if(isIpStr(image)){
                        if(image.startsWith("22.")||image.startsWith("28.")||image.startsWith("21.")){
                            addViolationWithMessage(data, node, "java.extend.testNetworkSegment.rule.msg");
                        }
                    }
                }
            }
        } catch (Exception e) {
            return super.visit(node, data);
        }
        return super.visit(node, data);
    }
    
    public static boolean isIpStr(String ip) {
        String rexp = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(ip);
        return mat.find();
    }
    
    public static void main(String[] args) {
        String s = "22.106.103.97";
        System.out.println(isIpStr(s));
    }
}
