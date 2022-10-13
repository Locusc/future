package cn.locusc.cloud.function.endpoints;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.catalog.FunctionTypeUtils;
import org.springframework.cloud.function.context.catalog.SimpleFunctionRegistry;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Jay
 * functions健康检查端点
 * {
 *     "charCounter":{
 *         "type":"FUNCTION",
 *         "input-type":"string",
 *         "output-type":"integer"
 *     },
 *     "logger":{
 *         "type":"CONSUMER",
 *         "input-type":"string"
 *     },
 *     "functionRouter":{
 *         "type":"FUNCTION",
 *         "input-type":"object",
 *         "output-type":"object"
 *     },
 *     "words":{
 *         "type":"SUPPLIER",
 *         "output-type":"string"
 *     }
 * }
 * 2022/9/14
 */
@Endpoint(id = "functions")
public class ActuatorFunctionEndPoint {

    private final FunctionCatalog functionCatalog;

    public ActuatorFunctionEndPoint(FunctionCatalog functionCatalog) {
        this.functionCatalog = functionCatalog;
    }

    /**
     *  {
     *     "actualTypeArguments":[
     *         {
     *             "actualTypeArguments":[
     *                 "cn.locusc.cloud.function.domain.Foo"
     *             ],
     *             "rawType":"org.springframework.messaging.Message",
     *             "ownerType":null,
     *             "typeName":"org.springframework.messaging.Message&lt;cn.locusc.cloud.function.domain.Foo&gt;"
     *         },
     *         {
     *             "actualTypeArguments":[
     *                 "java.lang.String"
     *             ],
     *             "rawType":"org.springframework.messaging.Message",
     *             "ownerType":null,
     *             "typeName":"org.springframework.messaging.Message&lt;java.lang.String&gt;"
     *         }
     *     ],
     *     "rawType":"java.util.function.Function",
     *     "ownerType":null,
     *     "typeName":"java.util.function.Function&lt;org.springframework.messaging.Message&lt;cn.locusc.cloud.function.domain.Foo&gt;, org.springframework.messaging.Message&lt;java.lang.String&gt;&gt;"
     * }
     */
    @ReadOperation
    public String readFunctions() throws JsonProcessingException {
        Gson gson = new Gson();

        HashMap<String, HashMap<String, String>> resultMap = new HashMap<>();
        Set<String> functionCatalogNames = functionCatalog.getNames(null);
        functionCatalogNames.forEach(f -> {
            SimpleFunctionRegistry.FunctionInvocationWrapper lookup = functionCatalog.lookup(f);

            HashMap<String, String> lineMap = new HashMap<>();
            // if (lookup.isFunction()) {
                lineMap.put("type", "FUNCTION");
                lineMap.put("input-type", this.toSimplePolyIn(lookup));
                lineMap.put("output-type", this.toSimplePolyOut(lookup));
            // } else if (lookup.isConsumer()) {
                lineMap.put("type", "CONSUMER");
                lineMap.put("input-type", this.toSimplePolyIn(lookup));
            // } else {
                lineMap.put("type", "SUPPLIER");
                lineMap.put("output-type", this.toSimplePolyOut(lookup));
            // }

            resultMap.put(f, lineMap);
        });

        return gson.toJson(resultMap);
    }

    private String toSimplePolyOut(SimpleFunctionRegistry.FunctionInvocationWrapper function) {
        // return FunctionTypeUtils.getRawType(function.getItemType(function.getOutputType())).getSimpleName().toLowerCase();
        return null;
    }

    private String toSimplePolyIn(SimpleFunctionRegistry.FunctionInvocationWrapper function) {
        // return FunctionTypeUtils.getRawType(function.getItemType(function.getInputType())).getSimpleName().toLowerCase();
        return null;
    }

}
