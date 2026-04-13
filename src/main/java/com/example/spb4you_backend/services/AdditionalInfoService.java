package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.AdditionalInfo;
import com.example.spb4you_backend.models.Category;
import com.example.spb4you_backend.repositories.AdditionalInfoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdditionalInfoService extends GenericService<AdditionalInfo, Integer> {
    private final AdditionalInfoRepository additionalInfoRepository;

    public AdditionalInfoService(AdditionalInfoRepository additionalInfoRepository) {
        super(additionalInfoRepository);
        this.additionalInfoRepository = additionalInfoRepository;
    }

    public Optional<AdditionalInfo> findById(Integer id) {
        return additionalInfoRepository.findById(id);
    }
}
