package com.pulsefit.attendance;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
class AttendanceTest {
 @Test void checkinStoresMemberAndFacility(){
   Attendance a=new Attendance(); a.setMemberId(5L);a.setFacility("Main");a.setCheckInTime(LocalDateTime.now());
   assertEquals(5L,a.getMemberId()); assertEquals("Main",a.getFacility()); assertNotNull(a.getCheckInTime());
 }
}
