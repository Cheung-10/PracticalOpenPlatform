package com.scutsehm.openplatform.util;

import com.scutsehm.openplatform.POJO.entity.RuntimeParameter;
import com.scutsehm.openplatform.POJO.entity.RuntimeParameterTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RuntimeParameterUtils {
    /**
     * 主要功能是校验
     * @param parameterTemplateList
     * @param parameterList
     * @return
     */
    public static boolean validate(List<RuntimeParameterTemplate> parameterTemplateList, List<RuntimeParameter> parameterList){
        //转map方便查询
        Map<String, RuntimeParameter> parameterMap =
                parameterList.stream().collect(Collectors.toMap(RuntimeParameter::getName, item -> item, (oldVal, currVal) -> currVal));
        //遍历 校验
        for (RuntimeParameterTemplate parameterTemplate : parameterTemplateList) {
            String parameterName = parameterTemplate.getName();
            RuntimeParameter parameter = parameterMap.get(parameterName);
            if (parameter == null) {
                if(parameterTemplate.getIsNecessary()){
                    //TODO 可以细化Exception
                    return false;
//                    throw new RuntimeException("参数"+ parameterTemplate.getName() +"不能为空");
                }else{
                    continue;
                }
            }
            if (!validate(parameterTemplate, parameter)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 单个参数校验
     * @param parameterTemplate 参数模板
     * @param runtimeParameter 实际传参
     * @return 是否符合要求
     */
    public static boolean validate(RuntimeParameterTemplate parameterTemplate, RuntimeParameter runtimeParameter){
        //在实际参数为空的时候，检查是否为必要参数
        String parameterValue = runtimeParameter.getValue();
        if(parameterValue==null || parameterValue.isEmpty()){
            //若是为必要则返回校验出错，若非必要参数，返回校验通过
            if(parameterTemplate.getIsNecessary()){
                return false;
            }else{
                return true;
            }
        }
        //检查类型
        RuntimeParameterTemplate.Type type = parameterTemplate.getType();
        String value = runtimeParameter.getValue();
        switch (type){
            case DOUBLE:
                return validateDouble(value);
            case INTEGER:
                return validateInteger(value);
            case BOOLEAN:
                return validateBoolean(value);
            case STRING:
                return true;
        }
        //遇到了不支持的类型
        return false;
    }

    private static boolean validateDouble(String value){
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static boolean validateInteger(String value){
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static boolean validateBoolean(String value){
        String str = value.toLowerCase();
        return "true".equals(str) || "false".equals(str);
    }
}
