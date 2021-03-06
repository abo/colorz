package me.aboz.colorz.ui.controller;

import java.util.Arrays;

import javax.xml.stream.XMLStreamException;

import me.aboz.colorz.model.weixin.IResponder;
import me.aboz.colorz.model.weixin.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.hash.Hashing;

@Controller
@RequestMapping(value = "/3rd/weixin")
public class WeixinController {

	private static final Logger LOG = LoggerFactory
			.getLogger(WeixinController.class);

	@Value("${colorz.weixin.token}")
	private String TOKEN;

	@Autowired
	@Qualifier("weixinResponder")
	private IResponder responder;

	public void setResponder(IResponder responder) {
		this.responder = responder;
	}

	private boolean isValid(String signature, long timestamp, long nonce) {
		String[] params = new String[] { TOKEN, String.valueOf(timestamp),
				String.valueOf(nonce) };
		Arrays.sort(params);
		String expect = Hashing.sha1()
				.hashString(Joiner.on("").join(params), Charsets.UTF_8)
				.toString();
		return signature.equals(expect);
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	public String verify(@RequestParam("signature") String signature,
			@RequestParam("timestamp") long timestamp,
			@RequestParam("nonce") long nonce,
			@RequestParam("echostr") String echostr) {
		if (isValid(signature, timestamp, nonce)) {
			return echostr;
		} else {
			LOG.warn("invalid verify request, timestamp: " + timestamp
					+ ", nonce: " + nonce + ", signature(" + signature
					+ "). our token: " + TOKEN);
			return "invalid signature.";
		}
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.POST)
	public String handle(@RequestParam("signature") String signature,
			@RequestParam("timestamp") long timestamp,
			@RequestParam("nonce") long nonce, @RequestBody String body) {
		if (!isValid(signature, timestamp, nonce)) {
			LOG.warn("invalid request, timestamp: " + timestamp + ", nonce: "
					+ nonce + ", signature(" + signature + "), body: " + body);
			return "invalid signature.";
		}
		Message message = null;
		try {
			message = Message.parseXML(body);
		} catch (XMLStreamException e) {
			LOG.warn("unknown massage format:" + body, e);
			return "";
		}
		LOG.info("message received:" + message.toString());
		try {
			return this.responder.respond(message).toXML();
		} catch (XMLStreamException e) {
			LOG.error("response can not been stringify to xml", e);
			return "";
		}
	}
}
