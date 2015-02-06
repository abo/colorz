package me.aboz.colorz.ui.controller;

import java.io.IOException;
import java.net.URL;

import me.aboz.colorz.RedisConst;
import me.aboz.colorz.model.weixin.AccessTokenRetriever;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.io.ByteStreams;

@Controller
public class ColorMarkerController {
	
	@Autowired
	AccessTokenRetriever accessTokenRetriever;
	
	@Autowired
	private StringRedisTemplate redis;
	
	@ResponseBody
	@RequestMapping(value = "/image/{mediaId}")
	public byte[] image(@PathVariable("mediaId") String mediaId) throws IOException {
		return ByteStreams.toByteArray(new URL(redis.opsForValue().get(RedisConst.KEY_PREFIX_WEIXIN_PIC_URL + mediaId)).openStream());
	}

	@RequestMapping(value="/mark/{target}/in/{palette}")
	public String diff(@PathVariable("target") String target,@PathVariable("palette") String palette,Model model){
		model.addAttribute("target", target);
		model.addAttribute("palette", palette);
		return "index";
	}
}
