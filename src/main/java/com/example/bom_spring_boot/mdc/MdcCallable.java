package com.example.bom_spring_boot.mdc;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public class MdcCallable<V> implements Callable<V> {
    private Callable<V> callable;
    private Map<String, String> contextMap;

    public MdcCallable(Callable<V> callable) {
        this(callable, true);
    }

    public MdcCallable(Callable<V> callable, boolean copyContext) {
        this.callable = callable;

        Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
        this.contextMap = copyContext && MapUtils.isNotEmpty(copyOfContextMap) ? copyOfContextMap : initContextMap();
    }

    @Override
    public V call() throws Exception {
        if (callable != null) {
            try {
                MDC.setContextMap(contextMap);
                return callable.call();
            } finally {
                MDC.clear();
            }
        }
        return null;
    }

    private Map<String, String> initContextMap() {
        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("UUID", "UUID:" + UUID.randomUUID().toString());
        return contextMap;
    }
}
