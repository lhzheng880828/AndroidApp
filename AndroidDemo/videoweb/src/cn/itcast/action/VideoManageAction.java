package cn.itcast.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import cn.itcast.formbean.VideoForm;
import cn.itcast.utils.StreamTool;

public class VideoManageAction extends DispatchAction {
	
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		VideoForm formbean = (VideoForm)form;
		if("GET".equals(request.getMethod())){
			byte[] data = request.getParameter("title").getBytes("ISO-8859-1");
			String title = new String(data, "UTF-8");
			System.out.println("title:"+ title);
			System.out.println("timelength:"+ formbean.getTimelength());
		}else{
			System.out.println("title:"+ formbean.getTitle());
			System.out.println("timelength:"+ formbean.getTimelength());
		}
		//下面完成视频文件的保存
		if(formbean.getVideo()!=null && formbean.getVideo().getFileSize()>0){
			String realpath = request.getSession().getServletContext().getRealPath("/video");
			System.out.println(realpath);
			File dir = new File(realpath);
			if(!dir.exists()) dir.mkdirs();
			File videoFile = new File(dir, formbean.getVideo().getFileName());
			FileOutputStream outStream = new FileOutputStream(videoFile);
			outStream.write(formbean.getVideo().getFileData());
			outStream.close();
		}
		return mapping.findForward("result");
	}
	
	public ActionForward getXML(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		InputStream inStream = request.getInputStream();
		byte[] data = StreamTool.readInputStream(inStream);
		String xml = new String(data, "UTF-8");
		System.out.println(xml);
		return mapping.findForward("result");
	}
}
