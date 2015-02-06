package me.aboz.colorz.model.weixin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import me.aboz.colorz.RedisConst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

@Service
public class AccessTokenRetriever {

	private static final String FMT_ACCESSTOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
	
	@Value("${colorz.weixin.appid}")
	private String appId;
	
	@Value("${colorz.weixin.appsecret}")
	private String appSecret;
	
	@Autowired
	private StringRedisTemplate redis;
	
	protected AccessToken retrieveFromServer() throws IOException{
		String tokenJson = CharStreams.toString(new InputStreamReader(new URL(String.format(FMT_ACCESSTOKEN_URL, appId, appSecret)).openStream(), Charsets.UTF_8));
		return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create().fromJson(tokenJson, AccessToken.class);
	}
	
	public String retrieve() throws IOException{
		if(redis.hasKey(RedisConst.KEY_WEIXIN_ACCESSTOKEN)){
			return redis.opsForValue().get(RedisConst.KEY_WEIXIN_ACCESSTOKEN);
		}
		AccessToken token = retrieveFromServer();
		redis.opsForValue().set(RedisConst.KEY_WEIXIN_ACCESSTOKEN, token.getAccessToken(), token.getExpiresIn() - 60, TimeUnit.SECONDS);
		return token.getAccessToken();
	}
	
	class AccessToken{
		private String accessToken;
		
		private long expiresIn;

		public String getAccessToken() {
			return accessToken;
		}

		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}

		public long getExpiresIn() {
			return expiresIn;
		}

		public void setExpiresIn(long expiresIn) {
			this.expiresIn = expiresIn;
		}
	}
}
