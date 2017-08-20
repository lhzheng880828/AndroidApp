package cn.itcast.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.utils.ConfigInfo;
import cn.itcast.utils.PanymentUtil;

public class PaymentResponse extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("GBK");
		String merchantID = ConfigInfo.getValue("p1_MerId"); // �̼�ID
		String keyValue = ConfigInfo.getValue("keyValue"); // �̼���Կ
		
		String sCmd = request.getParameter("r0_Cmd"); //ҵ������
		String sResultCode = request.getParameter("r1_Code"); //֧�����,���ֶ�ֵΪ1ʱ��ʾ�ۿ�ɹ�.
		String sTrxId = request.getParameter("r2_TrxId"); //YeePay�ױ����׶�����
		String amount = request.getParameter("r3_Amt");//�ۿ���,���׽�����,YeePay�ױ�����ϵͳ��ʵ�ʿۿ���ظ��̻�
		String currency = request.getParameter("r4_Cur");//���ױ���,�����ΪCNY
		String productId = request.getParameter("r5_Pid");//��ƷID
		String orderId = request.getParameter("r6_Order");//�̻�������
		String userId = request.getParameter("r7_Uid");//YeePay�ױ���ԱID
		String mp  = request.getParameter("r8_MP");//�̻���չ��Ϣ,����������д1K ���ַ���,���׷���ʱ��ԭ������
		String bType = request.getParameter("r9_BType");//���׽��֪ͨ����,1: ���׳ɹ��ص�(������ض���)2: ���׳ɹ�����֪ͨ(��������Ե�ͨѶ)
		String rb_BankId  = request.getParameter("rb_BankId");//֧������
		String rp_PayDate = request.getParameter("rp_PayDate");//������֧��ʱ��ʱ��
		String hmac = request.getParameter("hmac");//MD5����ǩ��
		
		boolean result = PanymentUtil.verifyCallback(hmac, merchantID, sCmd, sResultCode, sTrxId, amount,
				currency, productId, orderId, userId, mp, bType, keyValue);
		if(result){
			if("1".equals(sResultCode)){
				//��������ط�Ӧ�ð����ݿ��ж�����֧��״̬���ó��Ѿ�֧��.
			/*	payment(){
					Order order = getOrder(orderid);
					if(order.getPaymentState()==false){
						sql: select cardno,pwd from card where use=0 limit 1
						sql: insert into usercard(username, carno, pwd) values(?,?,?);
						sql: update card set use=1 where cardno=?
					}
					order.setPaymentState(true);
				}*/
				String message = "������Ϊ:"+ orderId+ "�Ķ���֧���ɹ���";
				message += ",�û�֧����"+ amount +"Ԫ";
				message +=",���׽��֪ͨ����:";
				if("1".equals(bType)){
					 message += "������ض���";
				}else if("2".equals(bType)){
					 message += "�ױ�֧�����غ�̨����֪ͨ";
				}
				message += ",�ױ�����ϵͳ�еĶ�����Ϊ:"+ sTrxId;
				request.setAttribute("message", message);
			}else{
				request.setAttribute("message", "�û�֧��ʧ��");
			}
		}else{
			request.setAttribute("message", "������Դ���Ϸ�");
		}
		request.getRequestDispatcher("/WEB-INF/page/paymentResult.jsp").forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
