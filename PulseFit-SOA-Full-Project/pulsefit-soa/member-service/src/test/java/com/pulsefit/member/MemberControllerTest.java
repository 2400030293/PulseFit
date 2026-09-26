package com.pulsefit.member;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class MemberControllerTest {
 @Test void memberObjectStoresData(){
   Member m=new Member(); m.setName("Test User"); m.setEmail("test@example.com");
   assertEquals("Test User",m.getName());
   assertEquals("test@example.com",m.getEmail());
 }
}
