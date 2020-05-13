package com.dq.Server.controller;

import com.dq.FunctionType;
import com.dq.Server.function.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FunctionController {
    private static Map<Integer,Class> functionMenu=new HashMap<>();

    static {
        functionMenu.put(FunctionType.SINGLE_CHART.getCode(),SingleChart.class);
        functionMenu.put(FunctionType.LOGIN.getCode(),Login.class);
        functionMenu.put(FunctionType.QUIT.getCode(), Quit.class);
        functionMenu.put(FunctionType.PEOPLE_CHART.getCode(), PeopleChart.class);
    }

    public static Function getFunction(Integer function) throws IllegalAccessException, InstantiationException {
        return (Function) functionMenu.get(function).newInstance();
    }
}
