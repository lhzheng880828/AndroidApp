package cn.itcast.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.utils.ConfigInfo;
import cn.itcast.utils.PanymentUtil;

public class PaymentRequest extends HttpServlet {
    //这是从页面穿过来的数据
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("GBK");
		String orderid = request.getParameter("orderid");//订单号
		String amount = request.getParameter("amount");//支付金额
		String pd_FrpId = request.getParameter("pd_FrpId");//选择的支付银行
		
		String p1_MerId = ConfigInfo.getValue("p1_MerId");
		String keyValue = ConfigInfo.getValue("keyValue");
		String merchantCallbackURL = ConfigInfo.getValue("merchantCallbackURL");
		
		String messageType = "Buy"; // 请求命令,在线支付固定为Buy
		String currency = "CNY"; // 货币单位
		String productDesc = ""; // 商品描述
		String productCat = ""; // 商品种类
		String productId = ""; // 商品ID
		String addressFlag = "0"; // 需要填写送货信息 0：不需要 1:需要		
		String sMctProperties = ""; // 商家扩展信息
		String pr_NeedResponse = "0"; // 应答机制
		
		String md5hmac = PanymentUtil.buildHmac(messageType, p1_MerId, orderid, amount, currency,
				productId, productCat, productDesc, merchantCallbackURL, addressFlag, sMctProperties, 
				pd_FrpId, pr_NeedResponse, keyValue);//将传过来的数据进行加密后得到mad加密后的结果
		request.setAttribute("messageType", messageType);
		request.setAttribute("merchantID", p1_MerId);
		request.setAttribute("orderId", orderid);
		request.setAttribute("amount", amount);
		request.setAttribute("currency", currency);
		request.setAttribute("productId", productId);
		request.setAttribute("productCat", productCat);
		request.setAttribute("productDesc", productDesc);
		request.setAttribute("merchantCallbackURL", merchantCallbackURL);
		request.setAttribute("addressFlag", addressFlag);
		request.setAttribute("sMctProperties", sMctProperties);
		request.setAttribute("frpId", pd_FrpId);
		request.setAttribute("pr_NeedResponse", pr_NeedResponse);
		request.setAttribute("hmac", md5hmac);//设置mad5加密后的结果
		
		request.getRequestDispatcher("/WEB-INF/page/connection.jsp").forward(request, response);
	}

}
