package com.vidividi.five.one;

import java.io.Console;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.view.AbstractView;

import com.fasterxml.jackson.core.JsonParser;
import com.vidividi.model.WatchDAO;
import com.vidividi.service.FormatCnt;
import com.vidividi.service.LoginService;
import com.vidividi.variable.ReplyDTO;
import com.vidividi.variable.SubscribeDTO;
import com.vidividi.variable.BundleDTO;
import com.vidividi.variable.ChannelDTO;
import com.vidividi.variable.FeedbackDTO;
import com.vidividi.variable.GoodDTO;
import com.vidividi.variable.PlaylistDTO;
import com.vidividi.variable.VideoPlayDTO;

import lombok.RequiredArgsConstructor;

//@RequiredArgsConstructor
@Controller
public class WatchController{

	@Inject
	private WatchDAO dao;
	
	@Autowired
	private LoginService service;
	
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private String url = "보여줄 비디오 url 경로";
    

//    @RequestMapping(value = "/video/{name}")
//    public ResponseEntity<ResourceRegion> getVideo(@RequestHeader HttpHeaders headers, @PathVariable String name) throws IOException {
//        logger.info("VideoController.getVideo");
//        
//
//        UrlResource video = new UrlResource("file:(기본 경로 입력)"+name+".mp4");
//        ResourceRegion resourceRegion;
//
//        final long chunkSize = 1000000L;
//        long contentLength = video.contentLength();
//
//        Optional<HttpRange> optional = headers.getRange().stream().findFirst();
//        HttpRange httpRange;
//        if (optional.isPresent()) {
//            httpRange = optional.get();
//            long start = httpRange.getRangeStart(contentLength);
//            long end = httpRange.getRangeEnd(contentLength);
//            long rangeLength = Long.min(chunkSize, end - start + 1);
//            resourceRegion = new ResourceRegion(video, start, rangeLength);
//        } else {
//            long rangeLength = Long.min(chunkSize, contentLength);
//            resourceRegion = new ResourceRegion(video, 0, rangeLength);
//        }
//
//        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
//                .body(resourceRegion);
//    }

	
	
	
	
	
	@RequestMapping("watch.do")
	public String watch(@RequestParam("video_code") String video_code,@RequestParam(value="playlist_code", required = false) String playList_code,
			Model model, @SessionAttribute(name = "RepChannelCode", required = false) String repChannelCode) {
		
		
		// 재생목록 체크
		if(playList_code == null || playList_code.equals("")) {
		}else {
			List<VideoPlayDTO> playList = this.dao.getPlayList(playList_code);
			model.addAttribute("playList_dto", playList);
		}
		
		VideoPlayDTO video_dto = this.dao.getVideo(video_code);
		
		// 로그인 체크
		if(repChannelCode == null || repChannelCode.equals("")) {
		}else {
			SubscribeDTO subscribe_dto = this.dao.getSubscribe(video_dto.getChannel_code() , repChannelCode);
			GoodDTO good_dto = this.dao.getGood(video_code, repChannelCode);
			ChannelDTO channel_dto = this.dao.getChannel(repChannelCode);
			
			model.addAttribute("subscribe_dto", subscribe_dto);
			model.addAttribute("good_dto", good_dto);
			model.addAttribute("channel_dto", channel_dto);
		}
		
		
		
		int reply_count = this.dao.getReplyCount(video_code);
		int videoGood_count= this.dao.getVideoGoodCount(video_code);
		
		model.addAttribute("video_dto", video_dto);
		model.addAttribute("reply_count", FormatCnt.format(reply_count));
		model.addAttribute("videoGood_count", FormatCnt.format(videoGood_count));
		
		return "/watch/watch";
	}
	
	@ResponseBody
	@RequestMapping("getMyReply.do")
	public String getMyReply(@RequestParam("video_code") String video_code,@RequestParam("reply_option") String reply_option, @SessionAttribute(name = "RepChannelCode", required = false) String repChannelCode, HttpServletResponse response) {
		
		response.setContentType("text/html; charset=UTF-8");
		
		JSONArray jArray = new JSONArray();
		
		List<ReplyDTO> list = this.dao.getMyReply(video_code,reply_option, repChannelCode);
		jArray = setReplyArray(list, video_code, repChannelCode);
		
		return jArray.toString();
	}
	
	
	@ResponseBody
	@RequestMapping(value = "reply.do" , produces = "application/text; charset=UTF-8")
	public String getReplyList(@RequestParam("video_code") String video_code, @RequestParam("reply_option") String reply_option, @RequestParam("page") int page, HttpServletResponse response, @SessionAttribute(name = "RepChannelCode", required = false) String repChannelCode) {
		
		response.setContentType("text/html; charset=UTF-8");
		
		int rowsize = 5;
		int startNo = (page * rowsize) - (rowsize - 1);
		int endNo = (page * rowsize);
		
		JSONArray jArray = new JSONArray();
		
		// 로그인 체크
		if(repChannelCode == null || repChannelCode.equals("")) {
			List<ReplyDTO> list = this.dao.getReply(video_code, reply_option, startNo, endNo);
			
			jArray = setReplyArray(list, video_code);
		}else {
			List<ReplyDTO> list = this.dao.getReply(video_code, reply_option, repChannelCode, startNo, endNo);
			
			if(page == 1) {
				List<ReplyDTO> myList = this.dao.getMyReply(video_code, reply_option, repChannelCode);
				myList.addAll(list);
				
				for(ReplyDTO dto : myList) {
				}
				
				jArray = setReplyArray(myList, video_code, repChannelCode);
			}else {
				jArray = setReplyArray(list, video_code, repChannelCode);
			}
				
		}
		
		return jArray.toString();
	}
	
	@ResponseBody
	@RequestMapping(value = "comment.do" , produces = "application/text; charset=UTF-8")
	public String getComment(String video_code, String reply_group, int page, HttpServletResponse response, @SessionAttribute(name = "RepChannelCode", required = false) String repChannelCode) {
		
		response.setContentType("text/html; charset=UTF-8");
		
		int rowsize = 3;
		int startNo = (page * rowsize) - (rowsize - 1);
		int endNo = (page * rowsize);
		
		JSONArray jArray = new JSONArray();
		
		List<ReplyDTO> list = this.dao.getComment(video_code, reply_group, startNo, endNo);
		
		if(repChannelCode == null || repChannelCode.equals("")) {
			jArray = setCommentArray(list, video_code);
		}else {
			jArray = setCommentArray(list, video_code, repChannelCode);
		}
		
		
		return jArray.toJSONString();
	}
	
	@ResponseBody
	@RequestMapping("nav_list.do")
	public List<VideoPlayDTO> getNavListAll(@RequestParam(value="navOption", required = false) String navOption, @RequestParam(value="channel_code", required = false) String channel_code, @RequestParam(value="category_code", required = false) String category_code) {
		
		List<VideoPlayDTO> list = this.dao.getNavList(navOption, channel_code, category_code);
		
		return list;
	}
	
	@ResponseBody
	@RequestMapping("insertGood.do")
	public Map<Object, Object> insertGood(@RequestParam("video_code") String video_code, @RequestParam("good_bad") int good_bad, @SessionAttribute(name = "RepChannelCode", required = false) String repChannelCode) {
		
		String good_code = service.generateGoodCode();
		String option = "";
		
		this.dao.insertGood(video_code, good_code, good_bad, repChannelCode);
		
		if(good_bad == 1) {
			option = "video_good";
		}else if(good_bad ==2) {
			option = "video_bad";
		}
		
		String video_good = FormatCnt.format(this.dao.plusVideoGood(video_code, option));
		
		Map<Object, Object> map = new HashMap<Object, Object>();
		
		map.put("getCode", good_code);
		map.put("getGood", video_good);
		
		
		return map;
	}
	
	@ResponseBody
	@RequestMapping("deleteGood.do")
	public String deleteGood(@RequestParam("video_code")String video_code, @RequestParam("good_code") String good_code, @RequestParam("good_bad") int good_bad) {
		
		String option = "";
		
		this.dao.deleteGood(good_code);
		
		if(good_bad == 1) {
			option = "video_good";
		}else if(good_bad ==2) {
			option = "video_bad";
		}
		
		String video_good = FormatCnt.format(this.dao.minusVideoGood(video_code, option));
		
		return video_good;
	}
	
	@ResponseBody
	@RequestMapping("updateGood.do")
	public String updateGood(@RequestParam("video_code")String video_code, @RequestParam("good_code") String good_code, @RequestParam("good_bad") int good_bad) {
		
		String option = "";
		
		if(good_bad == 1) {
			option = "changeToGood";
		}else if(good_bad ==2) {
			option = "changeToBad";
		}
		
		this.dao.updateGood(good_code, good_bad);
		String video_good = FormatCnt.format(this.dao.changeGood(video_code, option));
	
		
		return video_good;
	}
	
	@ResponseBody
	@RequestMapping("getSession.do")
	public String getSession(@SessionAttribute(name = "RepChannelCode", required = false) String repChannelCode) {
		
		return repChannelCode;
	}
	
	@ResponseBody
	@RequestMapping("insertSubscribe.do")
	public String insertSubscribe(@RequestParam("channel_code") String channel_code, @SessionAttribute(name = "RepChannelCode", required = false) String repChannelCode) {
		
		String subscribe_code = service.generateSubscribeCode();
		
		this.dao.insertSubscribe(subscribe_code, channel_code, repChannelCode);
		
		return subscribe_code; 
	}
	
	@ResponseBody
	@RequestMapping("deleteSubscribe.do")
	public void deleteSubscribe(@RequestParam("subscribe_code") String subscribe_code) {
		this.dao.deleteSubscribe(subscribe_code);
	}
	
	@ResponseBody
	@RequestMapping("insertFeedback.do")
	public Map<Object, Object> insertFeedback(@RequestParam("video_code") String video_code, @RequestParam("reply_code") String reply_code, @RequestParam("feedback_good") int feedback_good, @SessionAttribute(name = "RepChannelCode", required = false) String repChannelCode) {
		
		String feedback_code = service.generateFeedbackCode();
		
		String option = "";
		
		this.dao.insertFeedback(feedback_code, video_code, reply_code, repChannelCode, feedback_good);
		
		if(feedback_good == 1) {
			option = "reply_good";
			
		}else if(feedback_good ==2) {
			option = "reply_bad";
		}
		
		String good = FormatCnt.format(this.dao.plusReplyGood(reply_code, option));
		
		Map<Object, Object> map = new HashMap<Object, Object>();
		
		map.put("getCode", feedback_code);
		map.put("getGood", good);
		
		return map;
	}
	
	@ResponseBody
	@RequestMapping("deleteFeedback.do")
	public String deleteFeedback(@RequestParam("reply_code")String reply_code, @RequestParam("feedback_code") String feedback_code, @RequestParam("feedback_good") int feedback_good) {
		
		this.dao.deleteFeedback(feedback_code);
		
		String option = "";
		
		if(feedback_good == 1) {
			option = "reply_good";
		}else if(feedback_good ==2) {
			option = "reply_bad";
		}
		
		String getGood = FormatCnt.format(this.dao.minusReplyGood(reply_code, option));
		
		System.out.println("getGood>" +getGood);
		
		return getGood;
	}
	
	@ResponseBody
	@RequestMapping("updateFeedback.do")
	public String updateFeedback(@RequestParam("reply_code")String reply_code, @RequestParam("feedback_code") String feedback_code, @RequestParam("feedback_good") int feedback_good) {
		
		this.dao.updateFeedback(feedback_code, feedback_good);
		
		String option = "";
		
		if(feedback_good == 1) {
			option = "changeToGoodReply";
		}else if(feedback_good ==2) {
			option = "changeToBadReply";
		}
		
		String getGood = FormatCnt.format(this.dao.changeReply(reply_code, option));
		
		return getGood;
	}
	
	@ResponseBody
	@RequestMapping("inputReply.do")
	public ReplyDTO inputReply(@RequestParam("video_code") String video_code, @RequestParam("reply_cont") String reply_cont, @RequestParam("reply_comment") int reply_comment,
			@RequestParam(name="reply_group", required = false) String reply_group, @RequestParam(name="reply_code", required = false) String reply_code,  @SessionAttribute(name = "RepChannelCode", required = false) String repChannelCode) {
		
		
		String new_code = service.generateReplyCode();
		
		if(reply_group == null || reply_group.equals("")) {
			reply_group = service.generateReplyGroupCode();
		}
		
		this.dao.inputReply(video_code, new_code, reply_cont, reply_comment, reply_group, repChannelCode);

		ReplyDTO dto = this.dao.getNewReply(new_code);
		
		if(reply_comment == 2) {
			this.dao.updateReplyComment(reply_code);

		}
		
		return dto;
	}
	
	@ResponseBody
	@RequestMapping("newPlaylist.do")
	public void newPlaylist(@RequestParam("playlist_title")String playlist_title, @RequestParam(name="playlist_open", required = false)int playlist_open,
			@SessionAttribute(name = "RepChannelCode", required = false) String repChannelCode) {
		
		System.out.println("open>" +playlist_open);
		String bundle_code = service.generateBundleCode();
		
		this.dao.newPlaylist(bundle_code, playlist_title, playlist_open, repChannelCode);
		
		
	}
	
	@ResponseBody
	@RequestMapping("getBundleList.do")
	public List<BundleDTO> getPlaylist(@RequestParam("video_code")String video_code, @SessionAttribute(name = "RepChannelCode", required = false) String repChannelCode){
		
		List<BundleDTO> myBundleList = this.dao.getBundleList(repChannelCode);
		List<PlaylistDTO> myPlayList = this.dao.getMyPlayList(video_code, repChannelCode);
		
		for(PlaylistDTO playlistDTO : myPlayList) {
			
			for(BundleDTO bundleDTO : myBundleList) {
			
				if(playlistDTO.getPlaylist_code().equals(bundleDTO.getBundle_code())) {
					bundleDTO.setCheck(1);
				}else {
					bundleDTO.setCheck(0);
				}
			
			}
		}
		
		return myBundleList;
	}
	

	
	@RequestMapping("test.do")
	public String test() {
		return "/watch/test";
	}
	
	@RequestMapping("test2.do")
	public String test(HttpServletRequest request, Model model) {
		
		String field = request.getParameter("field");
		String keyword = request.getParameter("keyword");
		
		model.addAttribute("field", field);
		model.addAttribute("keyword", keyword);
		
		
		return "/watch/test2";
	}
	

	
	public JSONArray setReplyArray(List<ReplyDTO> list, String video_code) {
		
			JSONArray jArray = new JSONArray();
		
			for(ReplyDTO dto : list) {
			
			JSONObject json = new JSONObject();
			
			int comment_count = this.dao.getCommentCount(video_code, dto.getReply_group());
			
			json.put("channel_code", dto.getChannel_code());
			json.put("channel_name", dto.getChannel_name());
			json.put("channel_profil", dto.getChannel_profil());
			json.put("reply_code", dto.getReply_code());
			json.put("reply_cont", dto.getReply_cont());
			json.put("reply_comment", dto.getReply_comment());
			json.put("reply_regdate", dto.getReply_regdate());
			json.put("reply_update", dto.getReply_update());
			json.put("reply_good", dto.getReply_good());
			json.put("reply_bad", dto.getReply_bad());
			json.put("reply_group", dto.getReply_group());
			json.put("comment_count", comment_count);
			
			jArray.add(json);
		}
			
		return jArray;
	}
	
	public JSONArray setReplyArray(List<ReplyDTO> list, String video_code, String repChannelCode) {
		
		JSONArray jArray = new JSONArray();
		
		for(ReplyDTO dto : list) {
			
			int check_good =0;
			String check_code = "";
			
			JSONObject json = new JSONObject();
			
			int comment_count = this.dao.getCommentCount(video_code, dto.getReply_group());
			List<FeedbackDTO> feedback_dto = this.dao.getFeedback(video_code, repChannelCode);
			
			json.put("channel_code", dto.getChannel_code());
			json.put("channel_name", dto.getChannel_name());
			json.put("channel_profil", dto.getChannel_profil());
			json.put("reply_code", dto.getReply_code());
			json.put("reply_cont", dto.getReply_cont());;
			json.put("reply_comment", dto.getReply_comment());
			json.put("reply_regdate", dto.getReply_regdate());
			json.put("reply_update", dto.getReply_update());
			json.put("reply_good", dto.getReply_good());
			json.put("reply_bad", dto.getReply_bad());
			json.put("reply_group", dto.getReply_group());
			json.put("comment_count", comment_count);
			
			for(FeedbackDTO feed : feedback_dto) {
				if(dto.getReply_code().equals(feed.getReply_code())){
					check_good = feed.getFeedback_good();
					check_code = feed.getFeedback_code();
				}
			}
			
			json.put("check_good", check_good);
			json.put("check_code", check_code);
			
			jArray.add(json);
		}
		
		
		
		return jArray;
	}
	
	public JSONArray setCommentArray(List<ReplyDTO> list, String video_code) {
		
		
		
		JSONArray jArray = new JSONArray();
		
		for(ReplyDTO dto : list) {
			int check_good =0;
			
			JSONObject json = new JSONObject();
			
			int comment_count = this.dao.getCommentCount(video_code, dto.getReply_group());
			
			json.put("channel_code", dto.getChannel_code());
			json.put("channel_name", dto.getChannel_name());
			json.put("channel_profil", dto.getChannel_profil());
			json.put("reply_code", dto.getReply_code());
			json.put("reply_cont", dto.getReply_cont());
			json.put("reply_comment", dto.getReply_comment());
			json.put("reply_regdate", dto.getReply_regdate());
			json.put("reply_update", dto.getReply_update());
			json.put("reply_good", dto.getReply_good());
			json.put("reply_bad", dto.getReply_bad());
			json.put("reply_group", dto.getReply_group());
			json.put("comment_count", comment_count);
			json.put("check_good", check_good);
			
			
			jArray.add(json);
		}
		
		return jArray;		
		
		
	}

	
	public JSONArray setCommentArray(List<ReplyDTO> list, String video_code, String repChannelCode) {
		
		
		JSONArray jArray = new JSONArray();
		
		for(ReplyDTO dto : list) {
			
			int check_good =0;
			String check_code = "";
			
			JSONObject json = new JSONObject();
			
			int comment_count = this.dao.getCommentCount(video_code, dto.getReply_group());
			List<FeedbackDTO> feedback_dto = this.dao.getFeedback(video_code, repChannelCode);
			
			json.put("channel_code", dto.getChannel_code());
			json.put("channel_name", dto.getChannel_name());
			json.put("channel_profil", dto.getChannel_profil());
			json.put("reply_code", dto.getReply_code());
			json.put("reply_cont", dto.getReply_cont());
			json.put("reply_comment", dto.getReply_comment());
			json.put("reply_regdate", dto.getReply_regdate());
			json.put("reply_update", dto.getReply_update());
			json.put("reply_good", dto.getReply_good());
			json.put("reply_bad", dto.getReply_bad());
			json.put("reply_group", dto.getReply_group());
			json.put("comment_count", comment_count);
			
			for(FeedbackDTO feed : feedback_dto) {
				if(dto.getReply_code().equals(feed.getReply_code())){
					check_good = feed.getFeedback_good();
					check_code = feed.getFeedback_code();
				}
			}
			
			json.put("check_good", check_good);
			json.put("check_code", check_code);
			
			jArray.add(json);
		}
		
		return jArray;
		
	}
	
}

