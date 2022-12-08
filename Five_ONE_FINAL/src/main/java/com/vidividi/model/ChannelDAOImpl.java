package com.vidividi.model;


import javax.inject.Inject;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;

import com.vidividi.variable.ChannelDTO;
import com.vidividi.variable.MemberDTO;

@Controller
public class ChannelDAOImpl implements ChannelDAO {
	
	@Inject
	private SqlSessionTemplate session;

	@Override
	public ChannelDTO getChannelOwner(MemberDTO memberDTO) {
		return this.session.selectOne("owner", memberDTO);
	}
}
