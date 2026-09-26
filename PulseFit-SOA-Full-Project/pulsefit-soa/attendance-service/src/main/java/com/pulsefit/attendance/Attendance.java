package com.pulsefit.attendance;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="attendance")
public class Attendance {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private Long memberId;
 private String facility;
 private LocalDateTime checkInTime;
 public Attendance(){}
 public Long getId(){return id;} public Long getMemberId(){return memberId;} public void setMemberId(Long v){memberId=v;}
 public String getFacility(){return facility;} public void setFacility(String v){facility=v;}
 public LocalDateTime getCheckInTime(){return checkInTime;} public void setCheckInTime(LocalDateTime v){checkInTime=v;}
}
