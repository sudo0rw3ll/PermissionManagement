package com.vveed.permissions.services;

import java.util.List;
import java.util.Optional;

public interface IService<T, ID> {
    List<T> findAll();
    Optional<T> findById(ID var1);
    <S extends T> S save(S var1);
    void deleteById(ID var1);
}
