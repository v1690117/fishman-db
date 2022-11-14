package com.v1690117.fdb.core.services.impl;

import com.v1690117.fdb.core.dao.TypeRepository;
import com.v1690117.fdb.core.services.TypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class BaseTypeService implements TypeService {
    private final TypeRepository typeRepository;

    @Override
    public void addType(String type) {
        typeRepository.addType(type);
    }

    @Override
    public void deleteType(String type) {
        typeRepository.deleteType(type);
    }

    @Override
    public void setTypeAttributes(String type, Map<String, String> attributes) {
        typeRepository.defineAttributes(type, attributes);
    }
}
