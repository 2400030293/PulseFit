package com.pulsefit.attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AttendanceRepository extends JpaRepository<Attendance,Long>{
 List<Attendance> findByMemberIdOrderByCheckInTimeDesc(Long memberId);
}
