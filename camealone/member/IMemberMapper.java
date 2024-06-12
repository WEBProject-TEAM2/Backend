package org.team2project.camealone.member;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface IMemberMapper {
    @Select("SELECT * FROM userinfo WHERE id = #{id}")
    MemberDTO login(String id);

    @Select("SELECT userinfo.id, userinfo.email, userinfo.password, userinfo.phone from userinfo where id=#{id}")
    MemberDTO findById(String id);

    @Select("SELECT name from userinfo where id=#{id}")
    String findName(String id);

    @Select("SELECT * FROM userinfo WHERE email = #{email}")
    MemberDTO findByEmail(String email);

    @Select("SELECT * FROM userinfo WHERE phone = #{phone}")
    MemberDTO findByPhone(String phone);

    @Insert("INSERT INTO userinfo (id, name, password, email, phone) VALUES (#{id}, #{name}, #{password}, #{email}, #{phone})")
    int addMember(MemberDTO memberDTO);

    // 회원 탈퇴
    @Delete("DELETE FROM userinfo WHERE id = #{id}")
    int deleteMember(String id);

    // 회원정보 수정
    @Update("UPDATE userinfo SET password=#{newPassword} where id=#{id}")
    int updatePassword(String id, String newPassword);
}