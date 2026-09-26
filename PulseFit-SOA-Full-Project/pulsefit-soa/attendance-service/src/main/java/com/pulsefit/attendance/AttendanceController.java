package com.pulsefit.attendance;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import java.time.LocalDateTime;
import java.util.*;

@RestController @RequestMapping("/api/attendance")
public class AttendanceController {
 private final AttendanceRepository repo; private final RestClient.Builder rest;
 public AttendanceController(AttendanceRepository r,RestClient.Builder b){repo=r;rest=b;}

 @PostMapping("/checkin")
 public ResponseEntity<?> checkin(@RequestBody Map<String,Object> body){
   Long memberId=((Number)body.get("memberId")).longValue();
   String facility=String.valueOf(body.getOrDefault("facility","Main Facility"));
   try {
     rest.build().get().uri("http://MEMBER-SERVICE/api/members/{id}",memberId).retrieve().toBodilessEntity();
   } catch(Exception e) {
     return ResponseEntity.badRequest().body(Map.of("message","member does not exist or Member Service is unavailable"));
   }
   Attendance a=new Attendance();a.setMemberId(memberId);a.setFacility(facility);a.setCheckInTime(LocalDateTime.now());
   return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(a));
 }

 @GetMapping("/member/{memberId}") public List<Attendance> history(@PathVariable Long memberId){return repo.findByMemberIdOrderByCheckInTimeDesc(memberId);}
 @GetMapping public List<Attendance> all(){return repo.findAll();}
}
