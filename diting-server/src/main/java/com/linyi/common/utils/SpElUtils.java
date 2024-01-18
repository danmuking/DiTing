package com.linyi.common.utils;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @description el表达式解析工具类
 * @date 2024/1/17 22:38
 */
public class SpElUtils {
    private static final ExpressionParser parser = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * @param method:
     * @param args: 方法参数
     * @param spEl: spEL表达式
     * @return String
     * @description 解析spEL表达式
     * @date 2024/1/18 16:23
     */
    public static String parseSpEl(Method method, Object[] args, String spEl) {
//        解析参数名
        String[] params = Optional.ofNullable(parameterNameDiscoverer.getParameterNames(method)).orElse(new String[]{});
        EvaluationContext context = new StandardEvaluationContext();//el解析需要的上下文对象
        for (int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);//所有参数都作为原材料扔进去
        }
        Expression expression = parser.parseExpression(spEl);
        return expression.getValue(context, String.class);
    }

    public static String getMethodKey(Method method) {
//        返回方法全限定名
        return method.getDeclaringClass() + "#" + method.getName();
    }
}
