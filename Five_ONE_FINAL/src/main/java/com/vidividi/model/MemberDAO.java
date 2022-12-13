package com.vidividi.model;

import java.util.List;

import com.vidividi.variable.LoginDTO;

import com.vidividi.variable.MemberDTO;

public interface MemberDAO {

	String checkMember(LoginDTO dto);
	MemberDTO getMember(LoginDTO dto);
	MemberDTO getMember(String memberCode);
	List<MemberDTO> getMemberList();
	void updateLastChannel(MemberDTO dto);
	int joinIdCheck(String id);
	int joinMember(MemberDTO dto);
	int mebmerInfoUpdate(MemberDTO dto);
}
