package com.example.meetings.user.favoriteplace.model.domain;

import com.example.meetings.user.model.domain.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user_favorite_places")
@Getter
@Setter
@Accessors(chain = true)
public class UserFavoritePlace {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "lat", nullable = false)
    private Double lat;

    @Column(name = "lng", nullable = false)
    private Double lng;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
