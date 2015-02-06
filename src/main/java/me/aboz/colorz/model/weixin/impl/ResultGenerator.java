/**
 * Title:		TRS SMAS
 * Copyright:	Copyright(c) 2011-2013,TRS. All rights reserved.
 * Company:		北京拓尔思信息技术股份有限公司(www.trs.com.cn)
 */
package me.aboz.colorz.model.weixin.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.aboz.colorz.RedisConst;
import me.aboz.colorz.model.weixin.IHandler;
import me.aboz.colorz.model.weixin.IHandlerSelector;
import me.aboz.colorz.model.weixin.IResponder;
import me.aboz.colorz.model.weixin.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 微信应答服务器
 * @since huangshengbo @ Dec 28, 2013 11:56:01 PM
 *
 */
@Service("weixinResponder")
public class ResultGenerator implements IResponder {
	
	private static final String FMT_RESULT_URL = "http://aboz.me/mark/%s/in/%s";
	
	@Autowired
	private StringRedisTemplate redis;
	
	private IHandlerSelector selector = new HandlerMapping().register(Message.MESSAGE_TYPE_IMAGE, new IHandler(){
		
		@Override
		public boolean validate(Message request) {
			return true;
		}

		@Override
		public Message handle(Message request) {
			Message response = new Message();
			response.setTo(request.getFrom());
			response.setFrom(request.getTo());
			response.setCreatedTime(System.currentTimeMillis());
			
			HashOperations<String, String, String> hashOps = redis.opsForHash();
			String openId = request.getFrom();
			String mediaId = (String)request.getAdditionalInfo().get(Message.IMAGE_ELEMENT_MEDIA_ID);
			String url = (String)request.getAdditionalInfo().get(Message.IMAGE_ELEMENT_PICTURE_URL);
			redis.opsForValue().set(RedisConst.KEY_PREFIX_WEIXIN_PIC_URL + mediaId, url, 2, TimeUnit.DAYS);
			
			if(hashOps.hasKey(RedisConst.KEY_PENDING_PALETTE, openId)){
				String palette = hashOps.get(RedisConst.KEY_PENDING_PALETTE, openId);
				hashOps.delete(RedisConst.KEY_PENDING_PALETTE, openId);
				
				response.setType(Message.MESSAGE_TYPE_NEWS);
				List<Map<String,String>> articles = new ArrayList<Map<String,String>>();
				Map<String,String> article = new HashMap<String,String>();
				article.put(Message.NEWS_ELEMENT_ARTICLE_URL, String.format(FMT_RESULT_URL,mediaId,palette));
				article.put(Message.NEWS_ELEMENT_ARTICLE_TITLE, "Color Recognition");
				article.put(Message.NEWS_ELEMENT_ARTICLE_PICURL, url);
				article.put(Message.NEWS_ELEMENT_ARTICLE_DESCRIPTION, "Recognize dominant color of second picture from the palette of first picture.");
				articles.add(article);
				
				response.getAdditionalInfo().put(Message.NEWS_ELEMENT_ARTICLECOUNT, articles.size());
				response.getAdditionalInfo().put(Message.NEWS_ELEMENT_ARTICLES, articles);
			}else{
				hashOps.put(RedisConst.KEY_PENDING_PALETTE, openId, mediaId);
				response.setType(Message.MESSAGE_TYPE_TEXT);
				response.getAdditionalInfo().put(Message.TEXT_ELEMENT_CONTENT, "Palette accepted. We need one more picture.");
			}
			return response;
		}
		
	});
	
	public Message respond(Message request){
		IHandler handler = selector.select(request);
		if(handler != null){
			return handler.handle(request);
		}
		
		Message response = new Message();
		response.setTo(request.getFrom());
		response.setFrom(request.getTo());
		response.setCreatedTime(System.currentTimeMillis());
		
		response.setType(Message.MESSAGE_TYPE_IMAGE);
		Map<String,String> image = new HashMap<String,String>();
//		image.put(Message.IMAGE_ELEMENT_MEDIA_ID, MEDIAIDs[new Random().nextInt(4)]);
		response.getAdditionalInfo().put(Message.IMAGE_ELEMENT_IMAGE, image);
		return response;
	}
}