package io.github.alexcastelocoelho.quarkussocial.rest;

import io.github.alexcastelocoelho.quarkusocial.domain.model.Follower;
import io.github.alexcastelocoelho.quarkusocial.domain.model.Post;
import io.github.alexcastelocoelho.quarkusocial.domain.model.User;
import io.github.alexcastelocoelho.quarkusocial.domain.repository.FollowerRepository;
import io.github.alexcastelocoelho.quarkusocial.domain.repository.PostRepository;
import io.github.alexcastelocoelho.quarkusocial.domain.repository.UserRepository;
import io.github.alexcastelocoelho.quarkussocial.rest.dto.CreatPostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;


    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;



    @BeforeEach
    @Transactional
    public void setUP(){
        //usuario padrao dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        //criada a postagem para o usuario
        Post post = new Post();
        post.setText("hello");
        post.setUser(user);
        postRepository.persist(post);

        //usuario que nao segue ninguem
        var userNotFollower = new User();
        userNotFollower.setAge(33);
        userNotFollower.setName("ciclano");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        //usuario seguidor
        var userFollower = new User();
        userFollower.setAge(31);
        userFollower.setName("terceiro");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);


    }

    @Test
    @DisplayName("should create a post for a user")
    public void createPostTest(){
        var postRequest = new CreatPostRequest();
        postRequest.setText("Some text");


        given()
              .contentType(ContentType.JSON)
              .body(postRequest)
              .pathParam("userId", userId)
        .when()
              .post()
        .then()
              .statusCode(201);

    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an inexistent user")
    public void postForAnInexistentUserTest(){
        var postRequest = new CreatPostRequest();
        postRequest.setText("Some text");

        var inexistentUserId = 999;


        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", inexistentUserId)
        .when()
                .post()
        .then()
                .statusCode(404);

    }

    @Test
    @DisplayName("should return 404 when user doesn't exist")
    public void listPostUserNotFoundTest(){
        var inexistentUserId = 999;

        given()
                .pathParam("userId", inexistentUserId)
        .when()
                .get()
        .then()
                .statusCode(404);

    }

    @Test
    @DisplayName("should return 400 when followerId header is not present")
    public void listPostFollowerHeaderNotSendTest(){
        given()
               .pathParam("userId", userId)
        .when()
               .get()
        .then()
               .statusCode(400)
                .body(Matchers.is("you forgot the header followerId"));

    }

    @Test
    @DisplayName("should return 400 when follower doesn't exist")
    public void listPostFollowerNotFoundTest(){

        var inexistentFollowerId = 999;

        given()
               .pathParam("userId", userId)
               .header("followerId", inexistentFollowerId )
        .when()
               .get()
        .then()
               .statusCode(400)
               .body(Matchers.is("Inexistent followerId"));

    }

    @Test
    @DisplayName("should return 403 when follower isn't a follower")
    public void listPostNotAFollower(){

        given()
               .pathParam("userId", userId)
               .header("followerId", userNotFollowerId )
        .when()
               .get()
        .then()
               .statusCode(403)
               .body(Matchers.is("you can't see these posts"));

    }

    @Test
    @DisplayName("should return posts")
    public void listPostTest(){
        given()
               .pathParam("userId", userId)
               .header("followerId", userFollowerId )
        .when()
               .get()
        .then()
               .statusCode(200)
               .body("size()", Matchers.is(1));

    }

}