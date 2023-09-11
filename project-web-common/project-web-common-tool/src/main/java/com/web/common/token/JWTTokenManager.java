package com.web.common.token;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;

public class JWTTokenManager {

	/**
	 * 通过JWT生成token
	 * 
	 * @param option         token中存储的数据
	 * @param tokenKey       生成token的密钥
	 * @param tokenSignature 生成的token的签名
	 * @param expire         token的有效时间，如果有效时间大于0则使用有效时间
	 * @return String 生成的token字符串
	 * 
	 * @author zhouhui
	 * @since 2021.01.19
	 */
	@SuppressWarnings("unchecked")
	public String createToken(Map<String, Object> option, String tokenKey, String tokenSignature, int tokenExpire) {
		Builder build = JWT.create();
		// 设置签名
		build.withIssuer(tokenSignature);

		// 设置token存储属性值
		Iterator<String> keyIter = option.keySet().iterator();
		while (keyIter.hasNext()) {
			String key = keyIter.next();
			Object val = option.get(key);
			if (val == null) {
				continue;
			}
			if (val instanceof String) {
				build.withClaim(key, val.toString());
			} else if (val instanceof Integer) {
				build.withClaim(key, Integer.valueOf(val.toString()));
			} else if (val instanceof Double) {
				build.withClaim(key, Double.valueOf(val.toString()));
			} else if (val instanceof Boolean) {
				build.withClaim(key, Boolean.valueOf(val.toString()));
			} else if (val instanceof List) {
				build.withClaim(key, (List<?>) val);
			} else if (val instanceof Map) {
				build.withClaim(key, (Map<String, ?>) val);
			} else {
				build.withClaim(key, val.toString());
			}
		}

		// 设置有效的时间
		if(tokenExpire > 0) {
			build.withExpiresAt(getExpires(tokenExpire));
		}else {
			build.withExpiresAt(new Date());
		}
		
		// 进行加密处理
		Algorithm algorithm = Algorithm.HMAC256(tokenKey);
		return build.sign(algorithm);
	}
	
	/**
	 * 获取到期的日期
	 * @param 有效时间
	 * @return Date 到期时间
	 *
	 * @author zhouhui
	 * @since 2021.11.28
	 */
	private Date getExpires(int tokenExpire) {
		LocalDateTime cur = LocalDateTime.now();
		LocalDateTime expire = cur.plusSeconds(tokenExpire);
		return Date.from(expire.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 通过TOKEN获取USER信息
	 * 
	 * @param token TOKEN字符串
	 * @return HashMap<String, Object> 用户信息对象
	 * 
	 * @author zhouhui
	 * @since 2021.01.14
	 */
	public R<DecodedJWT> getDJWTFromToken(String token, String tokenKey, String tokenSignature,
			int expire) {
		R<DecodedJWT> json = new R<>();
		
		Algorithm algorithm = Algorithm.HMAC256(tokenKey);
		JWTVerifier verifier = JWT.require(algorithm)
		.withIssuer(tokenSignature)
		.acceptExpiresAt(expire)
		.build();
		
		DecodedJWT dJwt = null;
		try {
			dJwt = verifier.verify(token);
		} catch (TokenExpiredException e) {
			json.setResultEnum(ResultEnum.TOEKN_EXPIRE);
			return json;
		} catch (JWTVerificationException e) {
			json.setResultEnum(ResultEnum.TOEKN_ERROR);
			return json;
		}
		
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(dJwt);
		
		return json;
	}
	
	
	/**
	 * 根据获取的TOKEN，解析出对应的数据对象，并存储到HashMap中
	 * @param token token字符串
	 * @param tokenKey token的密钥
	 * @param tokenSignature token的签名
	 * @param expire token的有效期限
	 * @param keyNameMap 存储数据的key以及对应的数据类型
	 * HashMap中key存储名称，name存储数据类型，包括 String，Integer，
	 * Double，Boolean，List，Map
	 * @return ResultJson<HashMap<String, Object>>
	 * 
	 * @author zhouhui
	 * @since 2021.01.20
	 */
	public R<HashMap<String, Object>> getDataFromToken(String token, String tokenKey, String tokenSignature,
			int expire,Map<String, String> keyNameMap){
		R<HashMap<String, Object>> json = new R<>();
		
		R<DecodedJWT> getTokenResult = getDJWTFromToken(token, tokenKey, tokenSignature, expire);
		if(!ResultEnum.SUCCESS.getCode().equals(getTokenResult.getCode())) {
			json.setCode(getTokenResult.getCode());
			json.setMessage(getTokenResult.getMessage());
			return json;
		}
		
		HashMap<String, Object> dataMap = new HashMap<>();
		DecodedJWT jwt = getTokenResult.getData();
		
		// 设置token存储属性值
		Iterator<String> keyIter = keyNameMap.keySet().iterator();
		while (keyIter.hasNext()) {
			String key = keyIter.next();
			String val = keyNameMap.get(key);
			Claim claimVal = jwt.getClaim(key);
			if(claimVal == null) {
				continue;
			}
			if (val == null || "String".equals(val) || "".equals(val)) {
				dataMap.put(key, claimVal.asString());
			}else if("Integer".equals(val)) {
				dataMap.put(key, claimVal.asInt());
			}else if("Double".equals(val)) {
				dataMap.put(key, claimVal.asDouble());
			}else if("Boolean".equals(val)) {
				dataMap.put(key, claimVal.asBoolean());
			}else if("List".equals(val)) {
				dataMap.put(key, claimVal.asList(Object.class));
			}else if("Map".equals(val)) {
				dataMap.put(key, claimVal.asMap());
			}
		}
		
		json.setResultEnum(ResultEnum.SUCCESS);
		json.setData(dataMap);
		
		return json;
	}
}
