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
    //���Ǵ�ҳ�洩����������
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("GBK");
		String orderid = request.getParameter("orderid");//������
		String amount = request.getParameter("amount");//֧�����
		String pd_FrpId = request.getParameter("pd_FrpId");//ѡ���֧������
		
		String p1_MerId = ConfigInfo.getValue("p1_MerId");
		String keyValue = ConfigInfo.getValue("keyValue");
		String merchantCallbackURL = ConfigInfo.getValue("merchantCallbackURL");
		
		String messageType = "Buy"; // ��������,����֧���̶�ΪBuy
		String currency = "CNY"; // ���ҵ�λ
		String productDesc = ""; // ��Ʒ����
		String productCat = ""; // ��Ʒ����
		String productId = ""; // ��ƷID
		String addressFlag = "0"; // ��Ҫ��д�ͻ���Ϣ 0������Ҫ 1:��Ҫ		
		String sMctProperties = ""; // �̼���չ��Ϣ
		String pr_NeedResponse = "0"; // Ӧ�����
		
		String md5hmac = PanymentUtil.buildHmac(messageType, p1_MerId, orderid, amount, currency,
				productId, productCat, productDesc, merchantCallbackURL, addressFlag, sMctProperties, 
				pd_FrpId, pr_NeedResponse, keyValue);//�������������ݽ��м��ܺ�õ�mad���ܺ�Ľ��
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
		request.setAttribute("hmac", md5hmac);//����mad5���ܺ�Ľ��
		
		request.getRequestDispatcher("/WEB-INF/page/connection.jsp").forward(request, response);
	}

}
