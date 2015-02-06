package me.aboz.colorz.model.weixin;

/**
 * 
 * @since huangshengbo @ Dec 29, 2013 1:17:59 AM
 *
 */
public interface IHandler {
	
	public boolean validate(Message request);

	public Message handle(Message request);
}
