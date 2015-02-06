package me.aboz.colorz.model.weixin;

/**
 * 
 * @since huangshengbo @ Mar 13, 2014 9:07:48 AM
 *
 */
public interface IHandlerSelector {

	public IHandler select(Message request);
	
}
