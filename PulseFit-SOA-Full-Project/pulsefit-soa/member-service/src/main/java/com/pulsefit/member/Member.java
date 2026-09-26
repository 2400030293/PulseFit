package com.pulsefit.member;
import jakarta.persistence.*;

@Entity @Table(name="members")
public class Member {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String name;
 @Column(nullable=false) private String email;
 private String phone;
 private String facility;
 public Member(){}
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;}
 public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
 public String getFacility(){return facility;} public void setFacility(String v){facility=v;}
}
