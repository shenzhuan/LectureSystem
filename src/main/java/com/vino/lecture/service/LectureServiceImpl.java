package com.vino.lecture.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vino.lecture.entity.Attendance;
import com.vino.lecture.entity.Lecture;
import com.vino.lecture.entity.Student;
import com.vino.lecture.exception.LectureDuplicateException;
import com.vino.lecture.repository.AttendanceRepository;
import com.vino.lecture.repository.LectureRepository;
import com.vino.lecture.repository.StudentRepository;
import com.vino.scaffold.service.base.AbstractBaseServiceImpl;
import com.vino.scaffold.shiro.entity.Resource;
import com.vino.scaffold.shiro.entity.User;

@Service("lectureService")
public class LectureServiceImpl extends AbstractBaseServiceImpl<Lecture, Long>  implements LectureService{
	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private AttendanceRepository attendanceRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Override
	public void save(Lecture obj) {
		User curUser=getCurrentUser();
		obj.setCreatorName(curUser.getUsername());
		obj.setCreateTime(new Date());
		obj.setCreatorId(curUser.getId());
		super.save(obj);
	}
	@Override
	public void save(List<Lecture> objs) {
		User curUser=getCurrentUser();
		for(Lecture obj:objs){
			obj.setCreatorName(curUser.getUsername());
			obj.setCreateTime(new Date());
			obj.setCreatorId(curUser.getId());
		}
		super.save(objs);
	}

	 /**
     * 创建动态查询条件组合.
     */
    private Specification<Lecture> buildSpecification(final Map<String,Object> searchParams) {		
        Specification<Lecture> spec = new Specification<Lecture>(){           
			@Override
			public Predicate toPredicate(Root<Lecture> root,
				CriteriaQuery<?> cq, CriteriaBuilder cb) {
				Predicate allCondition = null;
				String title=(String) searchParams.get("title");
				String lecturer=(String) searchParams.get("lecturer");
				String address=(String) searchParams.get("address");
				String lectureTimeRange=(String) searchParams.get("lectureTimeRange");
				String createTimeRange=(String) searchParams.get("createTimeRange");
				if(title!=null&&!"".equals(title)){
					Predicate condition=cb.like(root.get("title").as(String.class),"%"+searchParams.get("title")+"%");
					if(null==allCondition)
						allCondition=cb.and(condition);//此处初始化allCondition,若按cb.and(allCondition,condition)这种写法，会导致空指针
					else
						allCondition=cb.and(allCondition,condition);
					}
				if(lecturer!=null&&!"".equals(lecturer)){
					Predicate condition=cb.like(root.get("lecturer").as(String.class),"%"+searchParams.get("lecturer")+"%");
					if(null==allCondition)
						allCondition=cb.and(condition);//此处初始化allCondition,若按cb.and(allCondition,condition)这种写法，会导致空指针
					else
						allCondition=cb.and(allCondition,condition);
					}
				if(address!=null&&!"".equals(address)){
					Predicate condition=cb.like(root.get("address").as(String.class),"%"+searchParams.get("address")+"%");
					if(null==allCondition)
						allCondition=cb.and(condition);//此处初始化allCondition,若按cb.and(allCondition,condition)这种写法，会导致空指针
					else
						allCondition=cb.and(allCondition,condition);
					}
				if(lectureTimeRange!=null&&!"".equals(lectureTimeRange)){			
					String lectureTimeStartStr=lectureTimeRange.split(" - ")[0]+":00:00:00";
					String lectureTimeEndStr=lectureTimeRange.split(" - ")[1]+":23:59:59";
					SimpleDateFormat format=new SimpleDateFormat("MM/dd/yyyy:hh:mm:ss");
					try {
						Date lectureTimeStart = format.parse(lectureTimeStartStr);
						Date lectureTimeEnd=format.parse(lectureTimeEndStr);
						Predicate condition=cb.between(root.get("time").as(Date.class),lectureTimeStart, lectureTimeEnd);
						if(null==allCondition)
							allCondition=cb.and(condition);//此处初始化allCondition,若按cb.and(allCondition,condition)这种写法，会导致空指针
						else
							allCondition=cb.and(allCondition,condition);
						
					} catch (ParseException e) {
						e.printStackTrace();
						Logger log =LoggerFactory.getLogger(this.getClass());
						log.error("lectureTime 转换时间出错");
					}				
				}
				
				if(createTimeRange!=null&&!"".equals(createTimeRange)){			
					String createTimeStartStr=createTimeRange.split(" - ")[0]+":00:00:00";
					String createTimeEndStr=createTimeRange.split(" - ")[1]+":23:59:59";
					SimpleDateFormat format=new SimpleDateFormat("MM/dd/yyyy:hh:mm:ss");
					try {
						Date createTimeStart = format.parse(createTimeStartStr);
						Date createTimeEnd=format.parse(createTimeEndStr);
						Predicate condition=cb.between(root.get("createTime").as(Date.class),createTimeStart, createTimeEnd);
						if(null==allCondition)
							allCondition=cb.and(condition);//此处初始化allCondition,若按cb.and(allCondition,condition)这种写法，会导致空指针
						else
							allCondition=cb.and(allCondition,condition);
						
					} catch (ParseException e) {
						e.printStackTrace();
						Logger log =LoggerFactory.getLogger(this.getClass());
						log.error("createTime 转换时间出错");
					}				
				}					
				return allCondition;
			}
        };
        return spec;
    }

	@Override
	public Page<Lecture> findLectureByCondition(Map<String, Object> searchParams, Pageable pageable) {
		
		return lectureRepository.findAll(buildSpecification(searchParams), pageable);
	}



	@Override
	public void update(Lecture lecture) {
		Lecture lecture2 = lectureRepository.findOne(lecture.getId());
		if (lecture.getTitle() != null)
			lecture2.setTitle(lecture.getTitle());
		if (lecture.getLecturer() != null)
			lecture2.setLecturer(lecture.getLecturer());
		if (lecture.getTime() != null)
			lecture2.setTime(lecture.getTime());
		if (lecture.getAddress() != null)
			lecture2.setAddress(lecture.getAddress());
		if (lecture.getMaxPeopleNum()>-1)
			lecture2.setMaxPeopleNum(lecture.getMaxPeopleNum());
		if(lecture.getCurrentPeopleNum()>-1)
			lecture2.setCurrentPeopleNum(lecture.getCurrentPeopleNum());
		if (lecture.getReserveStartTime() != null)
			lecture2.setReserveStartTime(lecture.getReserveStartTime());
		if (lecture.getDescription() != null)
			lecture2.setDescription(lecture.getDescription());
		if (lecture.getCreateTime() != null)
			lecture2.setCreateTime(lecture.getCreateTime());
		if (lecture.getCreatorName() != null)
			lecture2.setCreatorName(lecture.getCreatorName());
		if(lecture.getAvailable()!=null)
			lecture2.setAvailable(lecture.getAvailable());
		
		
		
	}
	/**
	 * 根据是否签到获取讲座
	 * @param studentId
	 * @param isAttended 是否签到
	 * @return
	 */
	
	@Override
	public List<Lecture> findLecturesByStudentId(long studentId,boolean isAttended) {
		List<Attendance> attendances=attendanceRepository.findAttendanceByStudentId(studentId);
		List<Lecture> lectures;
		List<Long> lectureIds=new ArrayList<>();
		for(Attendance a:attendances){
			//Lecture lecture=lectureRepository.findOne(a.getLectureId());
			if(isAttended){//保存签到的
				if(a.isAttended())
					lectureIds.add(a.getLectureId());
			}else{//保存未签到的
				if(!a.isAttended())
					lectureIds.add(a.getLectureId());
			}
		}		
		return lectures=lectureRepository.findAll(lectureIds);
	}
	@Override
	public List<Lecture> findLectureByAvailable(boolean available) {
		List<Lecture> lectures=lectureRepository.findLectureByAvailable(available);
		Collections.sort(lectures, new Comparator<Lecture>() {
			@Override

			public int compare(Lecture o1, Lecture o2) {
				if(o1.getTime().before(o2.getTime()))//o1比o2时间早
				return 1;
				else if(o1.getTime().after(o2.getTime()))
					return -1;
				else
					return 0;
			}
		});
		return lectures;
	}
	
}
