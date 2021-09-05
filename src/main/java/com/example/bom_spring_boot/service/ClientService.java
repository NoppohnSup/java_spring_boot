package com.example.bom_spring_boot.service;

import com.example.bom_spring_boot.entity.ClientsEntity;
import com.example.bom_spring_boot.repository.ClientsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.ConfigurationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.valid4j.Validation.validate;


@Service
public class ClientService {

    @Autowired
    ClientsRepository clientsRepository;

    @Autowired
    ObjectMapper objectMapper;

    /**
     *
     * @param clientName
     * @param requestAction = create_gr, cycle_count_by_sku
     * @param configType = sync_stock
     * @return
     */
    public Map<String, Object> getBuConfig(String clientName, String requestAction, String configType) throws Exception {
        ClientsEntity client = clientsRepository.findByClientName(clientName);
        validate(Optional.ofNullable(client).isPresent(), new ConfigurationException(String.format("bu: %s does not has configuration.", clientName)));

        Map<String, Map<String, Object>> config = objectMapper.convertValue(client.getConfig(), new TypeReference<Map<String, Map<String, Object>>>() {
        });

        Map<String, Object> syncStockMap = config.get(configType.toLowerCase());
        Boolean status = Boolean.valueOf(Optional.ofNullable(syncStockMap.get("status")).orElse("false").toString());
        validate(status, new ConfigurationException(String.format("bu: %s and %s configuration is disable.", clientName, configType)));

        validate(!Optional.ofNullable(syncStockMap.get("endpoint")).orElse("").toString().isEmpty(), new ConfigurationException(String.format("clientName: %s endpoint %s configuration is empty.", clientName, configType)));
        validate(!Optional.ofNullable(syncStockMap.get("method")).orElse("").toString().isEmpty(), new ConfigurationException(String.format("clientName: %s method %s configuration is empty.", clientName, configType)));

        Map<String, Object> actionMap = (Map<String, Object>) Optional.ofNullable(syncStockMap.get("action")).orElse(new HashMap<>());
        validate(Boolean.valueOf(Optional.ofNullable(actionMap.get(requestAction)).orElse("false").toString()), new ConfigurationException(String.format("bu: %s %s configuration not allow %s action.", clientName, configType, requestAction)));
        return syncStockMap;
    }
}
