package com.dq.client;

import com.dq.Server.function.Function;
import com.dq.Server.function.SingleChart;

import java.util.HashMap;
import java.util.Map;

public class FunctionController {
    private static Map<Integer,Class> functionMenu=new HashMap<>();

    static {
        SingleChart singleChart=new SingleChart();
        functionMenu.put(singleChart.getFunctionType().getCode(),singleChart.getClass());
    }

    public static Function getFunction(Integer function) throws IllegalAccessException, InstantiationException {
        return (Function) functionMenu.get(function).newInstance();
    }
}
