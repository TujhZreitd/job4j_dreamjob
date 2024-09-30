package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Collection;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;

    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        try (var connection = sql2o.open()) {
            var queryFindAll = connection.createQuery("SELECT * FROM users");
            var queryDelete = connection.createQuery("DELETE FROM users WHERE id = :id");
            Collection<User> users = queryFindAll.executeAndFetch(User.class);
            for (User user : users) {
                queryDelete.addParameter("id", user.getId());
                queryDelete.executeUpdate();
            }
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var user = sql2oUserRepository.save(new User(0, "egor@mail.ru", "Egor", "12345"));
        var savedUser = sql2oUserRepository.findByEmailAndPassword("egor@mail.ru", "12345");
        assertThat(savedUser).isEqualTo(user);
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oUserRepository.findByEmailAndPassword("egor@mail.ru", "12345").isEmpty()).isTrue();
    }

    @Test
    public void whenSaveTwoUsersWithOneEmail() {
        var user1 = sql2oUserRepository.save(new User(0, "egor@mail.ru", "Egor", "12345"));
        var user2 = sql2oUserRepository.save(new User(0, "egor@mail.ru", "Egor1", "123456"));
        var savedUser1 = sql2oUserRepository.findByEmailAndPassword("egor@mail.ru", "12345");
        var savedUser2 = sql2oUserRepository.findByEmailAndPassword("egor@mail.ru", "123456");
        assertThat(user2.get().getId()).isEqualTo(0);
        assertThat(savedUser1).isEqualTo(user1);
        assertThat(savedUser2.isEmpty()).isTrue();
    }
}