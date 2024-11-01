package com.gzhu.dic_platform.service;

import com.gzhu.dic_platform.common.utils.Result;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ImageUpdateService {

    private final Map<String, CompletableFuture<Result>> pendingImageFutures = new ConcurrentHashMap<>();

    public void addPendingFuture(String deviceNumber, CompletableFuture<Result> future) {
        pendingImageFutures.put(deviceNumber, future);
    }

    public CompletableFuture<Result> removePendingFuture(String deviceNumber) {
        return pendingImageFutures.remove(deviceNumber);
    }
}
