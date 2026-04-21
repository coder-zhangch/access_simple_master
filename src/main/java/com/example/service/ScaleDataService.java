package com.example.service;

import com.example.entity.ScaleData;

import java.util.List;

public interface ScaleDataService {

    List<ScaleData> getById(String id);
}
