package com.example.catalog_service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.catalog_service.domain.Book;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dasniko.testcontainers.keycloak.KeycloakContainer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Testcontainers
public class CatalogServiceApplicationTests {
	@Autowired
	private WebTestClient webTestClient;

	private static KeycloakToken bjornTokens;
	private static KeycloakToken isabelleTokens;

	@Container
	private static final KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:latest")
			.withRealmImportFile("keycloak_config.json");

	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
				() -> keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop");
	}

	@BeforeAll
	static void generateAccessToken() {
		WebClient webClient = WebClient.builder()
				.baseUrl(keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop/protocol/openid-connect/token")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();

		isabelleTokens = authenticatedWith("isabelle", "password", webClient);
		bjornTokens = authenticatedWith("bjorn", "password", webClient);
	}

	private static KeycloakToken authenticatedWith(String username, String password, WebClient webClient) {
		return webClient
				.post()
				.body(BodyInserters.fromFormData("grant_type", "password")
						.with("client_id", "polar-test")
						.with("username", username)
						.with("password", password))
				.retrieve()
				.bodyToMono(KeycloakToken.class)
				.block();
	}

	private record KeycloakToken(String accessToken) {
		@JsonCreator
		private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
			this.accessToken = accessToken;
		}
	}

	@Test
	void whenPostRequestThenBookCreated() {
		var expectedBook = Book.build("1231231231", "Title", "Author", 9.90, "");
		webTestClient.post().uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken))
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
				});
	}

	@Test
	void whenPostRequestUnauthorizedThen403() {
		var expectedBook = Book.build("1231231231", "Title", "Author", 9.90, "Polarsophia");

		webTestClient.post().uri("/books")
				.headers(headers -> headers.setBearerAuth(bjornTokens.accessToken()))
				.bodyValue(expectedBook)
				.exchange().expectStatus().isForbidden();
	}

	@Test
	void whenPostRequestUanuthenticatedThen401() {
		var expectedBook = Book.build("1231231231", "Title", "Author", 9.90, "Polarsophia");

		webTestClient.post().uri("/books")
				.bodyValue(expectedBook)
				.exchange().expectStatus().isUnauthorized();
	}

	@AfterAll
	static void cleanup() {
		keycloakContainer.close();
	}

}
