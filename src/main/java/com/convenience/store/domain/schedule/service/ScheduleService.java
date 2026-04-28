package com.convenience.store.domain.schedule.service;

import com.convenience.store.domain.schedule.dto.ScheduleRequest;
import com.convenience.store.domain.schedule.dto.ScheduleResponse;
import com.convenience.store.domain.schedule.entity.Schedule;
import com.convenience.store.domain.schedule.repository.ScheduleRepository;
import com.convenience.store.domain.user.entity.User;
import com.convenience.store.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    // 스케줄 생성 (점장)
    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        Schedule schedule = Schedule.builder()
                .user(user)
                .workDate(request.getWorkDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        return new ScheduleResponse(scheduleRepository.save(schedule));
    }

    // 스케줄 수정 (점장)
    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다."));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        Schedule updated = Schedule.builder()
                .id(schedule.getId())
                .user(user)
                .workDate(request.getWorkDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        return new ScheduleResponse(scheduleRepository.save(updated));
    }

    // 스케줄 삭제 (점장)
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new IllegalArgumentException("스케줄을 찾을 수 없습니다.");
        }
        scheduleRepository.deleteById(scheduleId);
    }

    // 전체 스케줄 조회 (점장)
    public List<ScheduleResponse> getAllSchedules() {
        return scheduleRepository.findAll()
                .stream()
                .map(ScheduleResponse::new)
                .toList();
    }

    // 본인 스케줄 조회 (알바생)
    public List<ScheduleResponse> getMySchedule(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
        return scheduleRepository.findByUser(user)
                .stream()
                .map(ScheduleResponse::new)
                .toList();
    }
}