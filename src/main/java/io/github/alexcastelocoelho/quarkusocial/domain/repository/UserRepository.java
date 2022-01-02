package io.github.alexcastelocoelho.quarkusocial.domain.repository;

import io.github.alexcastelocoelho.quarkusocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
}
