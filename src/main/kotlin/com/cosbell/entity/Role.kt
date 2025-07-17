package com.cosbell.entity

import jakarta.persistence.*

/*@Entity
@Table(name = "roles")
data class Role(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id

    @Column(updatable = false)
    val id: Long? = null,


    @Column(unique = true, nullable = false)
    val name: String? = null,

    @ManyToOne
    @JoinColumn(name = "user_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false,
        )
    var user: User? = null
)*/

@Entity
@Table(name = "roles")
data class Role(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val name: String,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permission",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "permission_id")]
    )
    val permissions: List<Permission> = listOf()
)





/*  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0,

  @Column(nullable = false)
  val role: String = "",

  @Column(nullable = false)
  val email: String = "",

  @Column(nullable = false)
  val password: String = "",

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  val user: User,

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "role_permission",
      joinColumns = [JoinColumn(name = "user_id")],
      inverseJoinColumns = [JoinColumn(name = "permission_id")]
  )
  val permissions: List<Permission> = emptyList()
) {
  companion object {
      val CLIENT: Role
  }
}*/
