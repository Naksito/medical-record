package com.medrec.persistence;

import java.util.List;

public interface ICrudRepository<DomainModelEntity, CreateDTO, UpdateRequest> {
    DomainModelEntity save(CreateDTO dto);
    DomainModelEntity findById(int id);
    List<DomainModelEntity> findAll();
    DomainModelEntity update(UpdateRequest entity);
    void delete(int id);
}
