package com.duodot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
//@Getter
//@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "account_status")
    private String accountStatus;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "profile_picture")
    private String profilePicture;

    private String city;

    private String state;

    //@Column(name = "pin_id")
    //private String pinId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "is_paired")
    private Boolean paired = false;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pair_ids", columnDefinition = "jsonb")
    private List<String> pairIds = new ArrayList<>();


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Calendar createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Calendar updatedDate;

}
