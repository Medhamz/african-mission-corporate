package com.africanmission.service;

import com.africanmission.model.TeamMember;
import com.africanmission.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    public List<TeamMember> getAllMembers() {
        return teamMemberRepository.findAll();
    }

    public List<TeamMember> getActiveMembers() {
        return teamMemberRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    public TeamMember getMemberById(Long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membre non trouvé"));
    }

    public TeamMember save(TeamMember member) {
        return teamMemberRepository.save(member);
    }

    public void delete(Long id) {
        teamMemberRepository.deleteById(id);
    }

    public void deleteAll() {
        teamMemberRepository.deleteAll();
    }
}