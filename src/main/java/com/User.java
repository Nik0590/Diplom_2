package com;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;

public class User {

    public static final String EMAIL_POSTFIX = "@yandex.ru";
    public String email;
    public String password;
    public String name;

    public User() {
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    @Step("Creation users with random credentials")
    public static User getRandom() {

        final String email = RandomStringUtils.randomAlphabetic(6) + EMAIL_POSTFIX;
        final String password = RandomStringUtils.randomAlphabetic(6);
        final String name = RandomStringUtils.randomAlphabetic(6);

        Allure.addAttachment("Login", email);
        Allure.addAttachment("Password", password);
        Allure.addAttachment("Name", name);

        return new User(email, password, name);
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public static User getUserWithEmailOnly() {
        return new User().setEmail(RandomStringUtils.randomAlphabetic(10));
    }

    public static User getUserWithPasswordOnly() {
        return new User().setPassword(RandomStringUtils.randomAlphabetic(10));
    }

    public static User getUserWithNameOnly() {
        return new User().setName(RandomStringUtils.randomAlphabetic(10));
    }

    public static User getUserWithoutName() {
        return new User().setEmail(RandomStringUtils.randomAlphabetic(10))
                .setPassword(RandomStringUtils.randomAlphabetic(10));
    }

    public static User getUserWithoutPassword() {
        return new User().setEmail(RandomStringUtils.randomAlphabetic(10))
                .setName(RandomStringUtils.randomAlphabetic(10));
    }

    public static User getUserWithoutEmail() {
        return new User().setPassword(RandomStringUtils.randomAlphabetic(10))
                .setName(RandomStringUtils.randomAlphabetic(10));
    }
}
