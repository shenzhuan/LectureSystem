package com.vino.lecture.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vino.lecture.entity.Student;
import com.vino.scaffold.entity.Constants;
import com.vino.scaffold.shiro.entity.User;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
@Service("studentExcelService")
public class StudentExcelService {
	@Autowired  
	private HttpSession session; 
	@Autowired
	private StudentService studentService;

	public void setStudentService(StudentService studentService) {
		this.studentService = studentService;
	}
	public void saveToExcel(String path,Long...ids) throws FileNotFoundException{
		List<Student> students=studentService.find(ids);
		File file=new File(path);
		createExcel(new FileOutputStream(file), students);
	}
	public List<Student> getFromExcel(File file) throws BiffException, IOException{
		return readExcel(file);
	}
	/**
	 * index从零开始
	 * excel格式：第一行为标题，第2行开始为数据，第一列：用户名，第二列：用户别名
	 * @param file
	 * @return
	 * @throws IOException 
	 * @throws BiffException 
	 * @throws Exception
	 */
	
	public List<Student> readExcel(File file) throws BiffException, IOException   {
		List<Student> studentlist = new ArrayList<Student>();
		Workbook rwb = null;
		String cellStr = null;
		Student student = null;
		// 创建输入流
		// 获取Excel文件对象
		if (file == null || !file.exists()) {
			return null;
		}
		InputStream stream = new FileInputStream(file);
		rwb = Workbook.getWorkbook(stream);
		// 获取文件的指定工作表 默认的第一个
		Sheet sheet = rwb.getSheet(0);
		// 行数(表头的目录不需要，从1开始)
		for (int i = 1; i < sheet.getRows(); i++) {
			// 创建一个student对象对应一行， 用来存储每一列的值
			student = new Student();
			// 列数			
			cellStr = sheet.getCell(0, i).getContents().trim();// 第一列，业主姓名
			student.setUsername(cellStr);
			cellStr = sheet.getCell(1, i).getContents().trim();// 单元号
			student.setName(cellStr);
			cellStr = sheet.getCell(2, i).getContents().trim();// 单元号
			student.setMajor(cellStr);
			cellStr = sheet.getCell(3, i).getContents().trim();// 单元号
			student.setGrade(cellStr);
			//设置默认值
			student.setPassword(Constants.DEFAULT_PASSWORD);
			student.setCreateTime(new Date());
			student.setCreatorName(getCurrentUser().getUsername());
			
			// 把刚获取的列存入studentlist
			studentlist.add(student);
		}
		return studentlist;
	}
	private void createExcel(OutputStream os,List<Student> list){
		String[] heads={"学号","姓名","专业","年级"};
		// 创建工作区
		WritableWorkbook workbook=null;
		try {
			workbook = Workbook.createWorkbook(os);
		// 创建新的一页，sheet只能在工作簿中使用
		WritableSheet sheet = workbook.createSheet("student sheet1", 0);
		// 创建单元格即具体要显示的内容，new Label(0,0,"用户") 第一个参数是column 第二个参数是row
		// 第三个参数是content，第四个参数是可选项,为Label添加字体样式
		// 通过sheet的addCell方法添加Label，注意一个cell/label只能使用一次addCell
		// 第一个参数为列，第二个为行，第三个文本内容
		//添加字段名
		for(int i=0;i<heads.length;i++){
			sheet.addCell(new Label(i,0,heads[i]));
		}
		//添加字段内容
		for(int i=0;i<list.size();i++){
			sheet.addCell(new Label(0, i+1, list.get(i).getUsername()));
			sheet.addCell(new Label(1, i+1, list.get(i).getName()));
			sheet.addCell(new Label(2, i+1, list.get(i).getMajor()));
			sheet.addCell(new Label(3, i+1, list.get(i).getGrade()));
			//sheet.addCell(new Label(1, i+1, list.get(i).getGender()));
			
		}
		workbook.write();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			// 将内容写到输出流中，然后关闭工作区，最后关闭输出流
			
			try {
				if(workbook!=null)
				workbook.close();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(os!=null)
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public HttpSession getSession() {
		return session;
	}
	public void setSession(HttpSession session) {
		this.session = session;
	}
	public StudentService getStudentService() {
		return studentService;
	}
	public User getCurrentUser(){
		return (User) session.getAttribute(Constants.CURRENT_USER);
		
	}
}
