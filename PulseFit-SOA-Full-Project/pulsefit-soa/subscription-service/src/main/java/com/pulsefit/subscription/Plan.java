package com.pulsefit.subscription;
import jakarta.persistence.*;

@Entity @Table(name="membership_plans")
public class Plan {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String name;
 private int durationMonths;
 private double price;
 public Plan(){}
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
 public int getDurationMonths(){return durationMonths;} public void setDurationMonths(int v){durationMonths=v;}
 public double getPrice(){return price;} public void setPrice(double v){price=v;}
}
