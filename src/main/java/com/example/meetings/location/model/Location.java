package com.example.meetings.location.model;

import com.example.meetings.chat.model.domain.Dialog;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "LOCATIONS")
public class Location {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lng")
    private Double lng;

    @Column(name = "radius")
    private Integer radius;

    @Column(name = "image")
    private String image;

    @Transient
    private Integer peopleCount;

    @OneToOne
    @JoinColumn(name = "dialog_id")
    private Dialog dialog;

}
