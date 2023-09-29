package com.nttdata.bc46account.repository;

import com.nttdata.bc46account.model.Movement;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Ntt Data - Top Employer 2023.
 * Todos los derechos Reservados.
 */
@Repository
public interface MovementRepository extends ReactiveMongoRepository<Movement, String> {
}
