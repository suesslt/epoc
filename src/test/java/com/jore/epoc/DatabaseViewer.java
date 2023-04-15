package com.jore.epoc;

import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DatabaseViewer {
    @Transactional
    public static void logDatabase(EntityManager entityManager) {
        Metamodel metamodel = entityManager.getMetamodel();
        Set<EntityType<?>> entities = metamodel.getEntities();
        for (EntityType<?> entityType : entities) {
            Class<?> javaType = entityType.getJavaType();
            Object singleResult = entityManager.createQuery("select count(*) from " + javaType.getName()).getSingleResult();
            log.info(javaType + ": " + singleResult);
        }
    }
}
