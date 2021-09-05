package com.example.bom_spring_boot.worker;

import com.example.bom_spring_boot.enums.WorkerEnum;
import com.example.bom_spring_boot.exception.NoWorkerException;
import org.springframework.beans.factory.BeanFactory;

import java.util.HashMap;
import java.util.Map;

public class WorkerFactory
{
    private static Map<WorkerEnum, Class<?>> workerMap = new HashMap<>();
    static {
        workerMap.put(WorkerEnum.CASE_ONE, CaseOneWorker.class);
        workerMap.put(WorkerEnum.CASE_TWO, CaseTwoWorker.class);
        workerMap.put(WorkerEnum.CASE_THREE, CaseThreeWorker.class);
        workerMap.put(WorkerEnum.RETRY, RetryWorker.class);
    }

    public static IWorker from(BeanFactory factory, String workerName) throws NoWorkerException {
        IWorker worker = (IWorker) factory.getBean(workerMap.get(WorkerEnum.from(workerName)));

        if (worker == null) {
            throw new NoWorkerException(String.format("No worker class for work type: %s", workerName));
        }

        return worker;
    }
}
