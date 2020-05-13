package com.dq.Server.controller;

import com.dq.Server.Entity.FunctionType;
import com.dq.Server.function.*;

import java.util.HashMap;
import java.util.Map;

public class FunctionController {
    private static Map<Integer,MyFunction> functionMenu=new HashMap<>();

    static {
        functionMenu.put(FunctionType.SINGLE_CHART.getCode(),new SingleChart());
        functionMenu.put(FunctionType.LOGIN.getCode(),new Login());
        functionMenu.put(FunctionType.QUIT.getCode(), new Quit());
        functionMenu.put(FunctionType.PEOPLE_CHART.getCode(), new PeopleChart());
    }

    public static MyFunction getFunction(Integer function) throws IllegalAccessException, InstantiationException {
        return functionMenu.get(function);
    }
}
