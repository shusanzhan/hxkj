/**
 * 
 */
package com.ystech.mem.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.core.dao.Page;
import com.ystech.core.util.DatabaseUnitHelper;
import com.ystech.mem.model.Member;
import com.ystech.mem.model.PointRecord;
import com.ystech.weixin.model.WeixinGzuserinfo;

/**
 * @author shusanzhan
 * @date 2014-1-17
 */
@Component("memberManageImpl")
public class MemberManageImpl extends HibernateEntityDao<Member>{
	@SuppressWarnings("unchecked")
	public List<Member> queryBirthdayMember(Integer userId,Integer memberShipLevelId,String mobilePhone){
		List<Member> result=new ArrayList<Member>();
		String querySql="SELECT mema.* FROM "+  
		"("+
				"SELECT mem.*,CASE WHEN mem.b>0 THEN mem.b ELSE mem.a END days FROM"+ 
				"("+  
				"	SELECT member.* ,"+
				"	DATEDIFF(CONCAT(DATE_FORMAT(NOW(),'%Y')+1,DATE_FORMAT(birthday,'-%m-%d')),NOW()) a,"+  
				"	DATEDIFF(CONCAT(DATE_FORMAT(NOW(),'%Y'),DATE_FORMAT(birthday,'-%m-%d')),NOW()) b"+  
				"	FROM  mem_Member "+
		"		) mem"+	
		") mema WHERE mema.creatorId="+userId+" and status="+Member.ENABLE ;
		if(memberShipLevelId>0){
			querySql=querySql+" and mema.memberShipLevelId= "+memberShipLevelId;
		}
		if(null!=mobilePhone&&mobilePhone.trim().length()>0){
			querySql=querySql+" and mema.mobilePhone like  '%"+mobilePhone+"%'";
		}
		querySql=querySql+" and mema.days BETWEEN 0 AND 5 ORDER BY days;  ";
		List<Member> list = executeSqlQuery(Member.class,querySql, new Object[]{}).list();
		
		/*String queryNongli="SELECT mema.* FROM "+  
				"("+
						"SELECT mem.*,CASE WHEN mem.b>0 THEN mem.b ELSE mem.a END days FROM"+ 
						"("+  
						"	SELECT member.* ,"+
						"	DATEDIFF(CONCAT(DATE_FORMAT(NOW(),'%Y')+1,DATE_FORMAT(birthday,'-%m-%d')),NOW()) a,"+  
						"	DATEDIFF(CONCAT(DATE_FORMAT(NOW(),'%Y'),DATE_FORMAT(birthday,'-%m-%d')),NOW()) b"+  
						"	FROM  member "+
				"		) mem"+	
				") mema WHERE mema.days BETWEEN 0 AND 5 ORDER BY days;  ";
		List<Member> list2 = executeSqlQuery(Member.class,queryNongli, new Object[]{}).list();
		result.addAll(list);
		result.addAll(list2);
		ComparatorMember comparatorMember=new ComparatorMember(); 
		Collections.sort(result,comparatorMember);*/
		return list;
	}

	/**
	 * @param pageNo
	 * @param pageSize
	 * @param dbid
	 * @param memberShipLevelId
	 * @param mobilePhone
	 * @return
	 */
	public Page<Member> pageQueryMember(Integer pageNo, Integer pageSize,
			Integer businessId, Integer memberShipLevelId,String name, String mobilePhone,Integer hasCar) {
		String hql="select * from mem_Member where   status=? ";
		List param= new ArrayList();
		//param.add(userId);
		param.add(Member.ENABLE);
		if(businessId>0){
			hql=hql+" and businessId=? ";
			param.add(businessId);
		}
		if(memberShipLevelId>0){
			hql=hql+" and memberShipLevel.dbid=? ";
			param.add(memberShipLevelId);
		}
		if(null!=name&&name.trim().length()>0){
			hql=hql+" and name like ? ";
			param.add("%"+name +"%");
		}
		if(null!=mobilePhone&&mobilePhone.trim().length()>0){
			hql=hql+" and mobilePhone like ? ";
			param.add("%"+mobilePhone+"%");
		}
		if(null!=hasCar&&hasCar>0){
			hql=hql+" and hasCar = ? ";
			param.add(hasCar);
		}
		hql=hql+"order by createTime";
		Page<Member> page = pagedQuerySql(pageNo, pageSize, Member.class, hql, param.toArray());
		return page;
	}

	/**
	 * @param memberLevelId
	 * @param phone
	 * @return
	 */
	public List<Member> memberSelect(Integer memberLevelId, String phone) {
		String hql ="from Member where 1=1 ";
		List param=new ArrayList();
		if(memberLevelId>0){
			hql=hql+" and memberShipLevel.dbid=?";
			param.add(memberLevelId);		
		}
		if(null!=phone&& phone.trim().length()>0){
			hql=hql+" and mobilePhone like  '%"+phone+"%'";
		}
		List<Member> members = createQuery(hql, param.toArray()).list();
		return members;
	}
	/**
	* 功能描述：
	* 参数描述：
	* 逻辑描述：
	* @return
	* @throws Exception
	*/
	public void updateMember(String memberIds,Integer memberShipLevelId)  {
		String sql="update mem_member set memberShipLevelId="+memberShipLevelId+ " where dbid in("+memberIds+")";
		try {
			DatabaseUnitHelper databaseUnitHelper = new DatabaseUnitHelper();
			java.sql.Connection jdbcConnection = databaseUnitHelper.getJdbcConnection();
			Statement createStatement = jdbcConnection.createStatement();
			createStatement.executeUpdate(sql);
			if(createStatement != null)createStatement.close();  
            if(jdbcConnection != null)jdbcConnection.close();  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ;
	}
	/**
	 * 功能描述：清空会员积分
	 * @param memberIds
	 */
	public void updateMemberPoint(String memberIds) {
		String sql="update mem_member set totalPoint=0,overagePiont=0 where dbid in("+memberIds+")";
		try {
			DatabaseUnitHelper databaseUnitHelper = new DatabaseUnitHelper();
			java.sql.Connection jdbcConnection = databaseUnitHelper.getJdbcConnection();
			Statement createStatement = jdbcConnection.createStatement();
			createStatement.executeUpdate(sql);
			if(createStatement != null)createStatement.close();  
            if(jdbcConnection != null)jdbcConnection.close();  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ;
	}
	/**
	 * 删除重复数据
	 * @param openId
	 */
	public void deleteDub(WeixinGzuserinfo weixinGzuserinfo){
		List<Member> members = findBy("weixinGzuserinfo.dbid", weixinGzuserinfo.getDbid());
		if(null!=members&&members.size()>1){
			int j=members.size();
			for (int i = 1; i < j; i++) {
				 Member member = members.get(i);
				delete(member);
			}
		}
	}

	/**
	 * 功能描述：验证推荐用户是否已经推荐
	 */
	public boolean validateMobilePhone(String mobilePhone,Integer memberId) {
		try {
			List<Member> members = findBy("mobilePhone", mobilePhone);
			if (null==members||members.size()<=0){
				return false;
			}
			if(members.size()>1){
				return true;
			}
			if(null==memberId){
				return true;
			}else{
				Member member = members.get(0);
				if(member.getDbid()==(int)memberId){
					return false;
				}else{
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 功能描述：查询当前用户的排名
	 * @param openId
	 * @return
	 */
	public Integer getCurentSn(String openId) {
		String sql="SELECT D.pm as pm FROM(	" +
				"	select ag.agentMoney,ag.microId, @rank:=@rank+1 as pm from mem_Member ag,(SELECT @rank:=0)B WHERE ag.agentMoney>0  ORDER BY agentMoney DESC" +
				")D WHERE D.microId='"+openId+"'";
		int sn=0;
		try {
			DatabaseUnitHelper databaseUnitHelper = new DatabaseUnitHelper();
			Connection jdbcConnection = databaseUnitHelper.getJdbcConnection();
			PreparedStatement createStatement = jdbcConnection.prepareStatement(sql);
			ResultSet resultSet = createStatement.executeQuery();
			while (resultSet.next()) {  
				sn = resultSet.getInt("pm");
			}
			resultSet.close();
			createStatement.close();
			jdbcConnection.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return sn;
	}
	/**
	 * 功能描述：查询当前用户的排名
	 * @param openId
	 * @return
	 */
	public List<Member> queryAgentTotal(String beginDate,String endDate) {
		List<Member> members=new ArrayList<Member>();
		String sql="SELECT "
				+ "mem.`name`,IFNULL(A.totalNum,0) AS totalNum,IFNULL(A.carMaster,0) AS carMasterNum "
				+ "from "
				+ "mem_member mem "
				+ "LEFT JOIN  "
				+ "(SELECT "
				+ "parentId,COUNT(parentId) AS totalNum,COUNT(IF(carMasterStatus=1,true,null)) AS carMaster FROM mem_member "
				+ "WHERE 1=1 ";
		 if(null!=beginDate&&beginDate.trim().length()>0){
			 sql=sql + " and agentDate>'"+beginDate+"' ";
		 }
		 if(null!=endDate&&endDate.trim().length()>0){
			 sql=sql + "and  DATE_FORMAT(agentDate,'%Y-%m-%d')<='"+endDate+"' ";
		 }
		 sql=sql + "GROUP BY parentId "
				+ ")A ON mem.dbid=A.parentId "
				+ "WHERE mem.memType=1 "
				+ "ORDER BY A.totalNum DESC;";
		try {
			DatabaseUnitHelper databaseUnitHelper = new DatabaseUnitHelper();
			Connection jdbcConnection = databaseUnitHelper.getJdbcConnection();
			PreparedStatement createStatement = jdbcConnection.prepareStatement(sql);
			ResultSet resultSet = createStatement.executeQuery();
			while (resultSet.next()) {  
				 String	name = resultSet.getString("name");
				 int totalNum = resultSet.getInt("totalNum");
				 int carMasterNum = resultSet.getInt("carMasterNum");
				 Member member=new Member();
				 member.setName(name);
				 member.setTotalNum(totalNum);
				 member.setCarMasterNum(carMasterNum);
				 members.add(member);
			}
			resultSet.close();
			createStatement.close();
			jdbcConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return members;
	}
	/**
	 * 功能描述：更新会员总积分记录
	 * @param member
	 * @param pointRecord
	 */
	public void updateMemberPoint(Member member,PointRecord pointRecord){
		//更新会员积分信息
		Integer totalPoint=0;
		if(null!=member.getTotalPoint()){
			totalPoint=member.getTotalPoint()+pointRecord.getNum();
		}else{
			totalPoint=pointRecord.getNum();
		}
		member.setTotalPoint(totalPoint);
		Integer overagePiont=0;
		if(null!=member.getOveragePiont()){
			overagePiont=member.getOveragePiont()+pointRecord.getNum();
		}else{
			overagePiont=pointRecord.getNum();
		}
		member.setOveragePiont(overagePiont);
		save(member);
	}

	public Member findByOpenId(String openid) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
