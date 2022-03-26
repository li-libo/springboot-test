/**
 * Copyright 1993-2018 Agree Tech.
 * All rights reserved.
 */
package cn.com.rest;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
public class AbsController {

	private static LinkedBlockingDeque<String> adpsAddressSourceQueue = new LinkedBlockingDeque<>();
	
	private static Set<String> allAdpsAddressSet = ConcurrentHashMap.newKeySet();
	
	private static final String SESSION_ID = "SESSION_ID";
	
	private static Map<String, Map<String, Object>> sessionVarMap = new ConcurrentHashMap<>();
	
	private static Map<String, Integer> 年薪map = new ConcurrentHashMap<>();
	
	private static Map<String, Integer> 资金map = new ConcurrentHashMap<>();
	
	private static Map<String, Integer> 年限map = new ConcurrentHashMap<>();

	static {
		Map<String, Object> varMap = new HashMap<>();
		varMap.put("会话信息:呼入渠道", "C001");
		varMap.put("交易信息:代理人标志", "0");
		varMap.put("交易信息:支取方式", "A");
		varMap.put("交易信息:销户标志", "4");
		varMap.put("会话信息:应答渠道", "A001");
		varMap.put("客户信息:人脸识别通过标志", 2);
		varMap.put("客户信息:是否展示客户信息维护", "1");
		sessionVarMap.put("1", varMap);	
		
		Map<String, Object> resourceMap2 = new HashMap<>();
		resourceMap2.put("会话信息:呼入渠道", "C001");
		resourceMap2.put("交易信息:代理人标志", "0");
		resourceMap2.put("会话信息:应答渠道", "A002");
		resourceMap2.put("客户信息:人脸识别通过标志", 1);
		sessionVarMap.put("2", resourceMap2);	
		
		Map<String, Object> resourceMap3 = new HashMap<>();
		resourceMap3.put("交易信息:支取方式", "A");
		resourceMap3.put("交易信息:销户标志", "0");
		resourceMap3.put("会话信息:负债产品类型", "0");
		resourceMap3.put("客户信息:存单开户金额", 70000);
		sessionVarMap.put("3", resourceMap3);	
		
		Map<String, Object> resourceMap4 = new HashMap<>();
		resourceMap4.put("渠道/渠道信息:渠道号", "C001");
		resourceMap4.put("代理/代理信息:代理标志", "0");
		resourceMap4.put("证件/证件信息:证件类型", "01");
		sessionVarMap.put("4", resourceMap4);	
		
		Map<String, Object> resourceMap5 = new HashMap<>();
		resourceMap5.put("渠道/渠道信息:渠道号", "C001");
		resourceMap5.put("代理/代理信息:代理标志", "0");
		resourceMap5.put("test2", "2");
		sessionVarMap.put("5", resourceMap4);	
		
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
	
	public static boolean ping(String ipAddress) throws Exception {
	    int  timeOut =  3000 ;  //超时应该在3钞以上 
	    boolean status = InetAddress.getByName(ipAddress).isReachable(timeOut);
	    // 当返回值是true时，说明host是可用的，false则不可。
	    return status;
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
	
	@RequestMapping(path = "/agree/service/endpoint/register", method = RequestMethod.POST)
	public String registerApplicationAddresses(HttpServletRequest request) throws JSONException {
		String pod_ip = request.getParameter("pod_ip");
		System.out.println("注册POD_IP =" + pod_ip);
		allAdpsAddressSet.add(pod_ip);
		JSONObject returnJson = new JSONObject();
		returnJson.put("code", 0);
		returnJson.put("msg", "操作成功,666");
		returnJson.put("result", "注册POD_IP =" + pod_ip + "成功!!!");
		return returnJson.toString();
	}

	@RequestMapping(path = "/agree/service/endpoint", method = RequestMethod.GET)
	public String getAllApplicationAddresses(HttpServletRequest request) throws JSONException {
//		System.out.println("namespace=" + request.getParameter("namespace") + ",serviceName="
//				+ request.getParameter("serviceName"));
		JSONObject returnJson = new JSONObject();
		returnJson.put("code", 0);
		returnJson.put("msg", "操作成功,666");
		JSONArray ipJsonArray = new JSONArray(allAdpsAddressSet);
		JSONObject resultJson = new JSONObject().put("ips", ipJsonArray);
		returnJson.put("result", resultJson);
		System.out.println("***Acaas返回容器ips列表: "  + ipJsonArray.toString());
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
				Map<String, Object> resourceMap = sessionVarMap.get(sessionId);
				if (resourceMap != null && resourceMap.containsKey(value)) {
					if(value.equals("会话信息:应答渠道")) {
						System.out.println("key = " + key + ", value = " + value);
					}
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

	@RequestMapping(path = "/receiveNotification", method = RequestMethod.POST)
	public void 接收通知(@RequestBody String requestBody) throws JSONException{
		System.out.println(requestBody);
	}
}
