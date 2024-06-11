package org.team2project.camealone.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.team2project.camealone.member.IMemberMapper;
import org.team2project.camealone.member.MemberDTO;

@Service
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private IMemberMapper mapper;

    @Autowired
    private HttpSession session;

    @Autowired
    private HttpServletRequest request;

    @Transactional
    public String register(MemberDTO memberDTO) {
        if (memberDTO.getId() == null || memberDTO.getId().trim().isEmpty()) {
            return "아이디는 필수항목입니다";
        }
        if (memberDTO.getName() == null || memberDTO.getName().trim().isEmpty()) {
            return "이름은 필수항목입니다";
        }
        if (memberDTO.getPassword() == null || memberDTO.getPassword().trim().isEmpty()) {
            return "비밀번호는 필수항목입니다";
        }
        if (memberDTO.getEmail() == null || memberDTO.getEmail().trim().isEmpty() || !memberDTO.getEmail().contains("@")) {
            return "유효하지 않은 이메일입니다";
        }

        if (isIdDuplicated(memberDTO.getId())) {
            return "이미 가입된 아이디입니다";
        }
        if (isEmailDuplicated(memberDTO.getEmail())) {
            return "이미 가입된 이메일입니다";
        }
        if (isPhoneDuplicated(memberDTO.getPhone())) {
            return "이미 가입된 전화번호 입니다";
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(encode);

        int result = mapper.addMember(memberDTO);
        if (result == 1) {
            return "success";
        } else {
            return "알 수 없는 이유로 가입에 실패했습니다";
        }
    }

    private boolean isIdDuplicated(String id) {
        MemberDTO member = mapper.login(id);
        return member != null;
    }

    private boolean isEmailDuplicated(String email) {
        MemberDTO member = mapper.findByEmail(email);
        return member != null;
    }

    private boolean isPhoneDuplicated(String phone) {
        // 이메일과 비슷하게 전화번호를 확인하는 쿼리를 작성합니다.
        MemberDTO member = mapper.findByPhone(phone);
        return member != null;
    }

    public String loginService(String id, String password) {
        logger.debug("로그인 시도: ID={}, Password={}", id, password);
        session = request.getSession(true); // 세션 생성

        if (id == null || id.trim().isEmpty()) {
            return "아이디를 입력하세요";
        }
        if (password == null || password.trim().isEmpty()) {
            return "비밀번호를 입력하세요";
        }

        MemberDTO check = mapper.login(id);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (check != null && encoder.matches(password, check.getPassword())) {
            session.setAttribute("id", check.getId());
            session.setAttribute("email", check.getEmail());
            session.setAttribute("password", check.getPassword());
            SecurityContextHolder.getContext().setAuthentication(SecurityContextHolder.getContext().getAuthentication());

            logger.info("로그인 성공: ID={}", id);

            return "성공";
        } else if (check == null) {
            logger.warn("로그인 실패: 회원가입을 먼저 해주세요. ID={}", id);
            return "회원가입을 먼저 해주세요.";
        } else {
            logger.warn("로그인 실패: 아이디나 비밀번호가 잘못됐습니다. ID={}", id);
            return "아이디나 비밀번호가 잘못됐습니다";
        }
    }

    // 회원 탈퇴를 처리하는 함수
    public boolean deleteUser(String sessionId, String password) {
        logger.debug("회원 탈퇴 시도: Session ID={}, Password={}", sessionId, password);

        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        MemberDTO check = mapper.login(sessionId);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (check != null && encoder.matches(password, check.getPassword())) {
            int result = mapper.deleteMember(sessionId);
            logger.debug("회원 탈퇴 처리 결과: Result={}", result);
            return result == 1;
        } else {
            logger.warn("회원 탈퇴 실패: 비밀번호가 일치하지 않습니다. Session ID={}", sessionId);
            return false;
        }
    }

    // 회원 정보를 수정하는 함수
    public String updateUserInfo(MemberDTO memberDTO) {
        String sessionId = request.getSession().getId();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (sessionId == null) {
            return "로그인이 필요합니다.";
        }else {
            if (encoder.matches(memberDTO.getPassword(), memberDTO.getPassword())) {
                String updatePassword = mapper.updatePassword(memberDTO.getId(), memberDTO.getPassword());
                logger.debug("회원 정보 수정 됨");
                return updatePassword;
            }else {
                return "비밀번호를 알맞게 입력하세요";
            }
        }
    }
}
