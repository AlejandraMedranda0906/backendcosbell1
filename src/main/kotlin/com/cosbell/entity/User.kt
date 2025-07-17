package com.cosbell.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, length = 50)
    var name: String,

    @Column(nullable = false, unique = true, length = 50)
    var email: String,

    @Column(nullable = false, length = 100)
    var password: String,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: List<Role> = listOf()
)

//var locked: Boolean = false,
//var disabled: Boolean = false)

/* @OneToMany(mappedBy= "users", fetch = FetchType.EAGER)
 @Enumerated(EnumType.STRING)
 var role: List<Role>? = null*/

/*@Column(nullable = false)
    var locked: Boolean = false

@Column(nullable = false)
    var disabled: Boolean = false*/