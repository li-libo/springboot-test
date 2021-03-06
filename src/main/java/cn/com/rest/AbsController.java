/**
 * Copyright 1993-2018 Agree Tech.
 * All rights reserved.
 */
package cn.com.rest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AbsController {

	private static LinkedBlockingDeque<String> adpsAddressSourceQueue = new LinkedBlockingDeque<>();
	
	private static Set<String> allAdpsAddressSet = ConcurrentHashMap.newKeySet();
	
	private static final String SESSION_ID = "SESSION_ID";
	
	private static Map<String, Map<String, Object>> sessionResourceMap = new ConcurrentHashMap<>();
	
	private static Map<String, Integer> 年薪map = new ConcurrentHashMap<>();
	
	private static Map<String, Integer> 资金map = new ConcurrentHashMap<>();
	
	private static Map<String, Integer> 年限map = new ConcurrentHashMap<>();

	static {
		Map<String, Object> resourceMap = new HashMap<>();
		resourceMap.put("渠道/渠道信息:渠道号", "C001");
		resourceMap.put("代理/代理信息:代理标志", "0");
		resourceMap.put("证件/证件信息:客户号", 0);
		resourceMap.put("介质/介质信息:介质类型", 9);
		resourceMap.put("证件/证件信息:合法结果", 1);
		resourceMap.put("证件/选择证件类型:证件类型", "2");
		//证件/证件信息:证件类型
		resourceMap.put("证件/证件信息:证件类型", 2);
		resourceMap.put("证件/证件选择:证件类型", 1);
		sessionResourceMap.put("1", resourceMap);		
		// 准备adpsAddress地址
		for(int port = 8800; port< 8900; port++) {
			adpsAddressSourceQueue.offer("localhost:" + port);
		}
		年薪map.put("小胖子", 30000);
		年薪map.put("懂王", 100000);
		年薪map.put("马大帅", 10000);
		资金map.put("小胖子", 20000);
		资金map.put("懂王", 10000000);
		资金map.put("马大帅", 10000);
		年限map.put("小胖子", 4);
		年限map.put("懂王", 10);
		年限map.put("马大帅", 2);
		
	}

	@RequestMapping(path = "/clearBrokerAddressesOfRegistration", method = RequestMethod.GET)
	public String clearBrokerAddressesOfRegistration() throws JSONException, InterruptedException {
		allAdpsAddressSet.clear();
		return "success!";
	}
	
	@RequestMapping(path = "/getAdpsAddress", method = RequestMethod.GET)
	public String getAdpsAddress() throws JSONException, InterruptedException {
		JSONObject adpsAddressJson = new JSONObject();
		String adpsAddress = adpsAddressSourceQueue.poll(10, TimeUnit.SECONDS);
		allAdpsAddressSet.add(adpsAddress);
		adpsAddressJson.put("adpsAddress", adpsAddress);
		return adpsAddressJson.toString();
	}
	
	@RequestMapping(path = "/agree/service/endpoint", method = RequestMethod.GET)
	public String getAllApplicationAddresses(@RequestParam(name = "namespace") String namespace,
			@RequestParam(name = "serviceName") String serviceName)throws JSONException {		
		System.out.println("namespace="+namespace + ",serviceName="+serviceName);
		System.out.println("Acaas的brokerAddresses列表长度为:" + allAdpsAddressSet.size());
		JSONObject returnJson = new JSONObject();
		returnJson.put("code", 0);
		returnJson.put("msg", "操作成功,666");
		JSONArray ipJsonArray = new JSONArray(allAdpsAddressSet);
		JSONObject resultJson = new JSONObject().put("ips", ipJsonArray);
		returnJson.put("result", resultJson);
		return returnJson.toString();
	}
	
	@RequestMapping(path = "/getVariables", method = RequestMethod.GET)
	public Map<String, Object> getVariables(@RequestParam(name = "variablesJson") String variablesJson)
			throws JSONException {
		JSONObject variableJsonObject = new JSONObject(variablesJson);
		String sessionId = variableJsonObject.getString(SESSION_ID);
		Map<String, Object> resultMap = new HashMap<>();
		@SuppressWarnings("unchecked")
		Iterator<String> keyIterator = variableJsonObject.keys();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			if (!key.equals(SESSION_ID)) {
				String value = (String) variableJsonObject.get(key);
				Map<String, Object> resourceMap = sessionResourceMap.get(sessionId);
				if (resourceMap != null && resourceMap.containsKey(value)) {
					resultMap.put(key, resourceMap.get(value));
				}
			}
		}
		return resultMap;
	}
	
	@RequestMapping(path = "/获取资金", method = RequestMethod.GET)
	public String 获取资金(@RequestParam(name = "姓名") String 姓名) throws JSONException{
		JSONObject result = new JSONObject().put("资金", 资金map.get(姓名));
		return result.toString();
	}
	
	@RequestMapping(path = "/获取年限", method = RequestMethod.GET)
	public String 获取年限(@RequestParam(name = "姓名") String 年限) throws JSONException{
		JSONObject result = new JSONObject().put("年限", 年限map.get(年限));
		return result.toString();
	}
	
	@RequestMapping(path = "/获取年薪", method = RequestMethod.GET)
	public String 获取年薪(@RequestParam(name = "姓名") String 姓名) throws JSONException{
		JSONObject result = new JSONObject().put("年薪", 年限map.get(姓名));
		return result.toString();
	}

	@RequestMapping(path = "/接收通知", method = RequestMethod.PUT)
	public void 接收通知(@RequestBody String requestBody) throws JSONException{
		System.out.println(requestBody);
	}
}
