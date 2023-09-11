package com.web.common.authcode;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.web.common.security.AuthcodeEntity;

/**
 * 生成图片验证码
 * 首先获取生成图片验证码中的文字
 * 接着根据生成的文字创建图片验证码
 * 
 * @since 2018.8.31
 * @author zhouhui
 * 
 * */
@Component
public class ImageAuthCode implements AuthCode{
	private static final String VERIFY_CODE = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
	private Random random = new Random();
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 生成对应的图片验证码
	 * @param int 图片的宽度
	 * @param int 图片的高度
	 * @param OutputStream 输出流
	 * @param String 图片验证码中的文字
	 * 
	 * @since 2018.08.31
	 * @author zhouhui
	 * 
	 * */
	public void outputImage(int width, int height, OutputStream os, String authcode) throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = image.getGraphics();
		
		//设置背景为白色
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, width, height);
		
		//设置文字
		char[] charcode = authcode.toCharArray();
		for(int i = 0 ;i<charcode.length;i++) {
			graphics.setColor(new Color(getRandomIntColor()));
	        Font font = new Font("Algerian", Font.ITALIC, 30);
	        graphics.setFont(font);
	        char[] c = new char[1];
	        c[0] = charcode[i];
	        graphics.drawChars(c, 0, c.length, (random.nextInt(10)+20)*i+10,30);
		}
        
		
		// 设置线条的颜色
        for (int i = 0; i < 10; i++) {  
        	graphics.setColor(new Color(getRandomIntColor())); 
            int x = random.nextInt(width - 1);  
            int y = random.nextInt(height - 1);  
            int xl = random.nextInt(width);  
            int yl = random.nextInt(height);  
            graphics.drawLine(x, y, xl, yl);  
        }  
		
		
		// 添加噪点  
        float yawpRate = 0.02f;// 噪声率  
        int area = (int) (yawpRate * width * height);  
        for (int i = 0; i < area; i++) {  
            int x = random.nextInt(width);  
            int y = random.nextInt(height);  
            int rgb = getRandomIntColor();  
            image.setRGB(x, y, rgb);  
        }
        
		graphics.dispose();
		ImageIO.write(image, "jpg", os);
		os.flush();
		os.close();
	}
	
	/**
	 * 将图片验证码转成base64
	 * @param authCode 图片验证码
	 * @return String base64
	 *
	 * @author zhouhui
	 * @since 2022.02.13 
	 */
	public String imageToBase64(String authCode) {
		ByteArrayOutputStream os = null;
		String imageBase64 = null;
		try {
			os = new ByteArrayOutputStream();
			
			outputImage(120, 40, os, authCode);
			
			byte[] byteArray = os.toByteArray();
			imageBase64 = Base64.getEncoder().encodeToString(byteArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageBase64;
	}
	
	/**
	 * 随机生成RGB,
	 * 例如255,255,255
	 * */
	private int[] getRandomRGB() {
		int[] rgb = new int[3];
		for(int i = 0;i < 3;i++) {
			rgb[i] = random.nextInt(100);
		}
		return rgb;
	}
	
	/**
	 * 十六进制RGB
	 * */
	private int getRandomIntColor() {  
        int[] rgb = getRandomRGB();  
        int color = 0;  
        for (int c : rgb) {  
            color = color << 8;  
            color = color | c;  
        }  
        return color;  
    } 
	
	
	/**
	 * 生成图片验证码的文字
	 * 
	 * @return String 验证码文字
	 * 
	 * @since 2018.08.31
	 * @author zhouhui
	 * 
	 * */
	private String getCode(int num) {
		StringBuilder authcode = new StringBuilder();
		for(int i = 0; i < num; i++) {
			int beginIndex = random.nextInt(VERIFY_CODE.length());
			authcode.append(VERIFY_CODE.substring(beginIndex, beginIndex+1));
		}
		return authcode.toString();
	}
	
	
	
	/**
	 * 生成图片验证码对应信息
	 * @return
	 *
	 * @author zhouhui
	 * @since 2022.02.13 
	 */
	@Override
	public AuthcodeEntity getAuthCode(String sessionKey) {
		String code = getCode(4);
		String imageBase64 = imageToBase64(code);
		String sessionId = getCode(16);
		if(StringUtils.hasText(sessionKey) && sessionKey.length() == 16) {
			sessionId = sessionKey;
		}
		
		//存储redis
		RedisSerializer<?> stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer);
		redisTemplate.setHashKeySerializer(stringSerializer);
		redisTemplate.setHashValueSerializer(stringSerializer);
		redisTemplate.opsForHash().put(sessionId, AuthcodeEntity.AUTHCODE_KEY_NAME, code);
		redisTemplate.opsForHash().getOperations().expire(sessionId, AuthcodeEntity.AUTHCODE_EXPIRE, TimeUnit.SECONDS);
		
		AuthcodeEntity entity = new AuthcodeEntity();
		entity.setAuthcode(code);
		entity.setImageBase64(imageBase64);
		entity.setSessionId(sessionId);
		
		return entity;
	}

	@Override
	public boolean verifyAuthCode(AuthcodeEntity entity) {
		if(!StringUtils.hasText(entity.getSessionId())) {
			return false;
		}
		if(!StringUtils.hasText(entity.getAuthcode())) {
			return false;
		}
		String storeCode = getSessionValue(entity.getSessionId(), AuthcodeEntity.AUTHCODE_KEY_NAME);
		if(!StringUtils.hasText(storeCode)) {
			return false;
		}
		if(!storeCode.equalsIgnoreCase(entity.getAuthcode())) {
			redisTemplate.opsForHash().delete(entity.getSessionId(), AuthcodeEntity.AUTHCODE_KEY_NAME);
			return false;
		}
		
		//认证成功，也需要删除缓存
		redisTemplate.opsForHash().delete(entity.getSessionId(), AuthcodeEntity.AUTHCODE_KEY_NAME);
		return true;
	}
	
	/**
	 * 获取session中的值，先根据sessionid获取hashMap
	 * 然后根据key获取对应的值
	 * @param sessionId 
	 * @param key hashMap中的key
	 * @return String 获取对应的值
	 * 
	 * @author zhouhui
	 * @since 2020.10.29
	 */
	private String getSessionValue(String sessionId,String key) {
		Object v = redisTemplate.opsForHash().get(sessionId, key);
		if(v == null) {
			return null;
		}else {
			return v.toString();
		}
	}
}
