package com.pulsefit.member;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/members")
public class MemberController {
 private final MemberRepository repo;
 public MemberController(MemberRepository repo){this.repo=repo;}
 @GetMapping public List<Member> all(){return repo.findAll();}
 @GetMapping("/{id}") public ResponseEntity<Member> one(@PathVariable Long id){return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());}
 @PostMapping public ResponseEntity<Member> create(@RequestBody Member m){return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(m));}
 @PutMapping("/{id}") public ResponseEntity<Member> update(@PathVariable Long id,@RequestBody Member input){
   return repo.findById(id).map(m->{m.setName(input.getName());m.setEmail(input.getEmail());m.setPhone(input.getPhone());m.setFacility(input.getFacility());return ResponseEntity.ok(repo.save(m));}).orElse(ResponseEntity.notFound().build());
 }
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){repo.deleteById(id);return ResponseEntity.noContent().build();}
}
