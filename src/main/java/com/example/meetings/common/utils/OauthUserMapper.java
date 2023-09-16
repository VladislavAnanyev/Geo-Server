package com.example.meetings.common.utils;

import com.example.meetings.auth.model.dto.input.GoogleToken;
import com.example.meetings.auth.model.dto.input.RegistrationModel;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class OauthUserMapper {

    private static final HttpTransport transport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = new JacksonFactory();
    private static final String CLIENT_ID = "681500590257-g4sdctvrglu3fqee4dl67abeksm463qi.apps.googleusercontent.com";

    public static RegistrationModel map(Object authenticationToken) {

        RegistrationModel user = new RegistrationModel();

        if (authenticationToken instanceof OAuth2AuthenticationToken authentication) {
            if (authentication.getAuthorizedClientRegistrationId().equals("google")) {

                user.setEmail((String) authentication.getPrincipal().getAttributes().get("email"));
                user.setFirstName((String) authentication.getPrincipal().getAttributes().get("given_name"));
                user.setLastName((String) authentication.getPrincipal().getAttributes().get("family_name"));
                user.setAvatar((String) authentication.getPrincipal().getAttributes().get("picture"));
                user.setUsername(((String) authentication.getPrincipal().getAttributes()
                        .get("email")).replace("@gmail.com", ""));


            } else if (authentication.getAuthorizedClientRegistrationId().equals("github")) {
                user.setUsername(authentication.getPrincipal().getAttributes().get("login").toString());
                user.setFirstName(authentication.getPrincipal().getAttributes().get("name").toString());
                user.setLastName(authentication.getPrincipal().getAttributes().get("name").toString());
                user.setAvatar(authentication.getPrincipal().getAttributes().get("avatar_url").toString());

                if (authentication.getPrincipal().getAttributes().get("email") != null) {
                    user.setEmail(authentication.getPrincipal().getAttributes().get("email").toString());
                } else {
                    user.setEmail("default@default.com");
                }
            }
        } else if (authenticationToken instanceof GoogleToken googleToken) {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = null;
            try {
                idToken = verifier.verify(googleToken.getIdTokenString());
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String userId = payload.getSubject();
                String email = payload.getEmail();
                boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String locale = (String) payload.get("locale");
                String familyName = (String) payload.get("family_name");
                String givenName = (String) payload.get("given_name");


                String username = email.replace("@gmail.com", "");

                user.setUsername(username);
                user.setEmail(email);
                user.setFirstName(givenName);
                user.setLastName(familyName);
                user.setAvatar(pictureUrl);
            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не поддерживаемый токен");
        }

        return user;
    }
}
