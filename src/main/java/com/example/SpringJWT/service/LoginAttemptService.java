package com.example.SpringJWT.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptService {

    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    private static final int ATTEMPT_INCREMENT = 1;
    private LoadingCache<String, Integer> loginAttemptCache;

    //Logging cache from Guava
    public LoginAttemptService(LoadingCache<String, Integer> loginAttemptCache) {
        super();
        loginAttemptCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(15, MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String s) throws Exception {
                        return 0;
                    }
                });
    }

    //Clear cache after success login
    public void evictUserFromLoginAttempt(String username) {
        loginAttemptCache.invalidate(username);
    }

    public void addUserToLoginAttemptCache(String username) throws ExecutionException {
        int attempts = 0;
        attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username);
        loginAttemptCache.put(username, attempts);
    }

    public boolean hasExceededMaxAttempts(String username) throws ExecutionException {
        return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
    }
}