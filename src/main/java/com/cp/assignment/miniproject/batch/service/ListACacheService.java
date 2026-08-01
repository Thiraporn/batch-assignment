package com.cp.assignment.miniproject.batch.service;


import com.cp.assignment.miniproject.batch.model.ListATransaction;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
//Keep A into cache
@Service
public class ListACacheService {

    private final Map<String, ListATransaction> cache = new HashMap<>();

    public void put(ListATransaction transaction) {
         //null never put transaction
        if (transaction == null || transaction.getOrderNumber() == null) {
            return;
        }
        cache.put(transaction.getOrderNumber(), transaction);
    }

    public ListATransaction remove(String orderNumber) {
        return cache.remove(orderNumber);
    }

    public ListATransaction get(String orderNumber) {
        return cache.get(orderNumber);
    }

    public Map<String, ListATransaction> getRemaining() {
        return cache;
    }
    //not use
    //use remove() in logic while matching
    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }
}