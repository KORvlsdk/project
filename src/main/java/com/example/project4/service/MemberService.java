package com.example.project4.service;

import com.example.project4.dto.MemberFormDto;
import com.example.project4.entity.Member;
import com.example.project4.repository.MemberRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;

    public Member saveMember (Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    public void validateDuplicateMember(Member member) {
        Optional<Member> findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember.isPresent()) {
            throw new IllegalStateException("이미 가입되어 있습니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        if(member == null){
            throw new UsernameNotFoundException(email);
        }
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();

    }


    public MemberFormDto readMember(String email) throws UsernameNotFoundException{
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        MemberFormDto memberFormDto = MemberFormDto.of(member);
        return memberFormDto;
    }


    public Member getMemberData(String email) {
        // 데이터베이스에서 데이터 가져오는 로직
        return memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }

    //이메일 중복
    public boolean isEmailDuplicate(String email) {
        Optional<Member> findMember = memberRepository.findByEmail(email);
        return findMember.isPresent(); // 이미 가입된 이메일이 존재하면 true 반환, 그렇지 않으면 false 반환
    }

    public boolean isEmailAlreadyInUse(String email) {
        // 이메일이 이미 데이터베이스에 존재하는지 확인하는 로직을 구현
        // 존재하면 true 반환, 아니면 false 반환
        return memberRepository.existsByEmail(email);
    }

    @Transactional
    public void deleteMember(String email) {
        // 이 메서드에서 회원 정보 및 관련 정보를 삭제하는 로직을 구현합니다.
        // MemberRepository를 사용하여 데이터베이스에서 회원 정보를 가져와 삭제하거나 관련 정보도 삭제할 수 있습니다.

        // 예시: 이메일을 기반으로 회원 정보를 가져온 후 삭제
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        if (member != null) {
            // 회원 정보를 삭제
            memberRepository.delete(member);
            // 다른 관련 정보도 삭제할 수 있습니다.
        }
    }
    }

